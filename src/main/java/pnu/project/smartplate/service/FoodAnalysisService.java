package pnu.project.smartplate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    public FoodInfo analyzeImage(String imagePath) {

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
//        Path path = Paths.get("src/main/resources/static/" + imagePath);
        FileSystemResource fileResource = new FileSystemResource(path.toFile());

        body.add("file", fileResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 요청
        System.out.println("requestEntity = " + requestEntity);
        ResponseEntity<FoodInfo> response = restTemplate.postForEntity(fullUrl, requestEntity, FoodInfo.class);

        // 리턴
        if (response.getStatusCode() == HttpStatus.OK) {
            FoodInfo foodInfo = response.getBody();
            if (foodInfo != null) {
                foodInfo.setImageUrl(imagePath);
            }
            return foodInfo;
        } else {
            throw new RuntimeException("Failed to analyze image. Status code: " + response.getStatusCode());
        }



    }

    public String saveImg(MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String extension = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID() + extension;

        String filePath = uploadDir + fileName;

        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}