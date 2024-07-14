package pnu.project.smartplate.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pnu.project.smartplate.model.FoodInfo;

@Service
public class FoodAnalysisService {

    private Map<String, FoodInfo> savedResults = new HashMap<>();

    public void saveResult(String customType, String customAmount,String imageUrl) {
        String dateKey = LocalDateTime.now().toString();
        savedResults.put(dateKey, new FoodInfo(customType, customAmount,imageUrl));
    }

    public Map<String, FoodInfo> getSavedResults() {
        return savedResults;
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
}
