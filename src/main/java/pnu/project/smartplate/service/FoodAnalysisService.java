package pnu.project.smartplate.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pnu.project.smartplate.model.FoodInfo;
import pnu.project.smartplate.model.FoodNutrient;

@Service
@Slf4j
public class FoodAnalysisService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    @Value("${fastapi.port}")
    private String fastApiPort;

    @Value("${fastapi.endpoint}")
    private String fastApiEndpoint;

    private final RestTemplate restTemplate;

    private Map<String, FoodInfo> savedResults = new HashMap<>();

    Logger logger = LoggerFactory.getLogger(FoodAnalysisService.class);

    @Value("${fastapi.scheme}")
    private String fastApiScheme;

    @Value("${fastapi.host}")
    private String fastApiHost;


    @Autowired
    public FoodAnalysisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void saveResult(String customType, String customAmount, String imageUrl) {
        String dateKey = LocalDateTime.now().toString();
        savedResults.put(dateKey, new FoodInfo(customType, customAmount, imageUrl));
    }

    public FoodInfo analyzeImage(String imagePath) throws IOException {

        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String fullUrl = UriComponentsBuilder.newInstance()
            .scheme(fastApiScheme)
            .host(fastApiHost)
            .port(fastApiPort)
            .path(fastApiEndpoint)
            .toUriString();

        // 바디
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        Path path = Paths.get(uploadDir + imagePath);
        FileSystemResource fileResource = new FileSystemResource(path.toFile());

        body.add("file", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 요청
        System.out.println("requestEntity = " + requestEntity);
        ResponseEntity<FoodInfo> response = restTemplate.postForEntity(fullUrl, requestEntity,
            FoodInfo.class);

        // 리턴
        if (response.getStatusCode() == HttpStatus.OK) {
            FoodInfo foodInfo = response.getBody();
            if (foodInfo != null) {
                foodInfo.setImageUrl(imagePath);
            }

            // TODO: FastAPI에서 응답을 Map 형식으로 정제하는 과정 구현 -(gimsanghae, 2024-08-16, 18:27)

            Map<String, Integer> foods = new LinkedHashMap<>();
            foods.put("김치볶음밥", 100);
            foods.put("달걀말이", 40);
            foods.put("시래기된장국", 200);

            // CSV에서 해당 음식의 정보 가져오기
            Map<String, FoodNutrient> foodMap = findFood(foods);
            for (String foodName: foodMap.keySet()) {
                log.info(foodMap.get(foodName).toString());
            }
            return foodInfo;
        } else {
            throw new RuntimeException(
                "Failed to analyze image. Status code: " + response.getStatusCode());
        }


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

    // TODO: 12가지 음식 영양 정보 CSV 파일 읽어서 LIST형식으로 리턴 -(gimsanghae, 2024-08-16, 18:28)
    private Map<String, FoodNutrient> readCSV() throws IOException {
        File file = new File("FoodNutrient.csv");
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), "UTF-8"));
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

    // TODO(gimsanghae, 2024-08-15, 목, 22:09): 전달받은 음식명과 중량 데이터로 영양 정보 CSV에서 해당 음식 영양 정보 찾기
    private Map<String, FoodNutrient> findFood(Map<String, Integer> foodInfos) throws IOException {
    Map<String, FoodNutrient> foodNutrientMap = readCSV();
    return foodInfos.entrySet().stream()
        .filter(entry -> foodNutrientMap.containsKey(entry.getKey()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> {
                FoodNutrient nutrient = foodNutrientMap.get(entry.getKey());
                calculateNutrient(nutrient, entry.getValue());
                return nutrient;
            }
        ));
}

    // TODO(gimsanghae, 2024-08-15, 목, 22:13): 인식된 음식의 중량을 바탕으로 영양 정보 계산하기
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