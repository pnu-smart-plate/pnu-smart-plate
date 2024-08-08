package java.pnu.project.smartplate.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.project.smartplate.model.FoodInfo;

@Service
public class FoodAnalysisService {

    // 업로드 디렉토리 설정
    @Value("${file.upload-dir}")
    private String uploadDir;

    private Map<String, FoodInfo> savedResults = new HashMap<>();

    public void saveResult(String customType, String customAmount,String imageUrl) {
        String dateKey = LocalDateTime.now().toString();
        savedResults.put(dateKey, new FoodInfo(customType, customAmount,imageUrl));
    }



    public FoodInfo analyzeImage(String imagePath) {
        // 이미지 분석 로직 구현
        // 예시로 하드코딩된 값 반환
        String foodType = "볶음밥";
        String foodAmount = "보통";

        // 음식 정보 객체 생성 (음식종류, 음식의 양, 음식 이미지 파일 경로)
        var foodInfo = new FoodInfo(foodType, foodAmount, imagePath);
        return foodInfo;
    }

    public String saveImg(MultipartFile imageFile) throws IOException {
        // 고유한 파일명 생성
        String originalFileName = imageFile.getOriginalFilename();
        String extension = null;
        if (originalFileName != null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + extension;

        // 파일 저장 경로 설정
        String filePath = uploadDir + fileName;

        // 파일 저장
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
