package pnu.project.smartplate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import pnu.project.smartplate.model.FoodClass;
import pnu.project.smartplate.model.FoodInfo;
import pnu.project.smartplate.model.FoodNutrient;

@Service
@Slf4j
public class FoodAnalysisService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${fastapi.port}")
    private String fastApiPort;

    @Value("${fastapi.endpoint}")
    private String fastApiEndpoint;

    private final RestTemplate restTemplate;


    @Value("${fastapi.scheme}")
    private String fastApiScheme;

    @Value("${fastapi.host}")
    private String fastApiHost;

    @Autowired
    public FoodAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, FoodNutrient> analyzeImage(String imagePath) throws IOException { // 이미지 분석
        String fullUrl = getFullUrlString(); // FastAPI 주소 가져오기
        HttpEntity<MultiValueMap<String, Object>> requestEntity = getResult(
            imagePath); // RequestEntity
        ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, requestEntity,
            String.class); // ResponseEntity
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> dataList = mapper.readValue(response.getBody(),
            new TypeReference<>() {
            });

        List<Integer> classes = new ArrayList<>();
        List<Double> confidences = new ArrayList<>();
        List<Double> amounts = new ArrayList<>();

        addDataToList(dataList, classes, confidences, amounts);
        log.info(classes.toString());
        return getStringFoodNutrientMap(response, classes, amounts);
    }

    private String getFullUrlString() { // FastAPI 주소 Get
        return UriComponentsBuilder.newInstance().scheme(fastApiScheme).host(fastApiHost)
            .port(fastApiPort).path(fastApiEndpoint).toUriString();
    }

    private Map<String, FoodNutrient> getStringFoodNutrientMap(ResponseEntity<String> response,
        List<Integer> classes, List<Double> amounts) throws IOException {
        if (response.getStatusCode() == HttpStatus.OK) {
            ArrayList<FoodInfo> foodInfos = getFoodInfos(classes, amounts);
            Map<String, Integer> foods = getStringIntegerMap(foodInfos);
            return getStringFoodNutrientMap(foods); // CSV에서 해당 음식의 정보 가져오기
        } else {
            throw new RuntimeException(
                "Failed to analyze image. Status code: " + response.getStatusCode());
        }
    }

    private Map<String, FoodNutrient> getStringFoodNutrientMap(Map<String, Integer> foods)
        throws IOException {
        Map<String, FoodNutrient> foodMap = findFood(foods);
        for (String foodName : foodMap.keySet()) {
            log.info(foodMap.get(foodName).toString());
        }
        return foodMap;
    }

    private static Map<String, Integer> getStringIntegerMap(ArrayList<FoodInfo> foodInfos) {
        Map<String, Integer> foods = new LinkedHashMap<>();
        for (FoodInfo foodInfo : foodInfos) {
            foods.put(foodInfo.getFoodName(),
                (int) Math.round(Double.parseDouble(foodInfo.getFoodAmount())));
        }
        return foods;
    }

    private ArrayList<FoodInfo> getFoodInfos(List<Integer> classes, List<Double> amounts) {
        ArrayList<FoodInfo> foodInfos = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            String foodName = FoodClass.values()[classes.get(i)].getKrName();
            foodInfos.add(new FoodInfo(foodName, amounts.get(i).toString()));
        }
        return foodInfos;
    }

    private static void addDataToList(List<Map<String, Object>> dataList, List<Integer> classes,
        List<Double> confidences, List<Double> amounts) {
        for (Map<String, Object> item : dataList) {
            classes.add((Integer) item.get("class"));
            confidences.add((Double) item.get("confidence"));
            amounts.add((Double) item.get("amount"));
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> getResult(
        String imagePath) { // Python과 Http 통신
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 바디
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Path path = Paths.get(uploadDir + imagePath);
        FileSystemResource fileResource = new FileSystemResource(path.toFile());
        body.add("file", fileResource);
        return new HttpEntity<>(body, headers);
    }

    public String saveImg(MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String extension =
            originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID() + extension;
        String filePath = uploadDir + fileName;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private Map<String, FoodNutrient> readCSV() throws IOException {
        File file = new File("FoodNutrient.csv");
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        String line;
        // foods, <음식명, 음식 영양 정보>
        Map<String, FoodNutrient> foods = new LinkedHashMap<>();
        while ((line = bufferedReader.readLine()) != null) {
            // 쉼표 뒤 문자열이거나 큰따옴표로 둘러쌓인 문자열이 나오거나 둘 중 하나이며 큰따옴표 사이의 쉼표 및 특수기호는 무시
            // 제한 없이 split을 한다 -> 구분자가 나올면 무제한 분할한다.
            // positive lookahaed (?=...)
            String[] split = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            String name = split[0];
            double amount = Double.parseDouble(split[1]);
            double calory = Double.parseDouble(split[2]);
            double carbo = Double.parseDouble(split[3]);
            double protein = Double.parseDouble(split[6]);
            double fat = Double.parseDouble(split[5]);
            double sugar = Double.parseDouble(split[4]);
            // 음식 영양 정보 객체 생성
            FoodNutrient nutrient = new FoodNutrient(name, amount, calory, carbo, protein, fat,
                sugar);
            foods.put(name, nutrient);
        }
        return foods;
    }

    private Map<String, FoodNutrient> findFood(Map<String, Integer> foodInfos) throws IOException {
        Map<String, FoodNutrient> foodNutrientMap = readCSV();
        return foodInfos.entrySet().stream()
            .filter(entry -> foodNutrientMap.containsKey(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                FoodNutrient nutrient = foodNutrientMap.get(entry.getKey());
                calculateNutrient(nutrient, entry.getValue());
                return nutrient;
            }));
    }

    private void calculateNutrient(FoodNutrient foodNutrient, Integer amount) {
        double amountRatio = amount / foodNutrient.getAmount(); // CSV에 기재된 중량과 인식된 음식의 중량 비율
        foodNutrient.setCalory(Math.round(foodNutrient.getCalory() * amountRatio));
        foodNutrient.setCarbo(Math.round(foodNutrient.getCarbo() * amountRatio));
        foodNutrient.setFat(Math.round(foodNutrient.getFat() * amountRatio));
        foodNutrient.setProtein(Math.round(foodNutrient.getProtein() * amountRatio));
        foodNutrient.setSugar(Math.round(foodNutrient.getSugar() * amountRatio));
        foodNutrient.setAmount(amount);
    }

}