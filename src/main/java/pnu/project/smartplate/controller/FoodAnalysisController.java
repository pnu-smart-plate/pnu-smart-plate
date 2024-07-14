package pnu.project.smartplate.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pnu.project.smartplate.model.FoodInfo;
import pnu.project.smartplate.service.FoodAnalysisService;

@Controller
public class FoodAnalysisController {

    private final FoodAnalysisService foodAnalysisService;

    // 업로드 디렉토리 설정
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public FoodAnalysisController(FoodAnalysisService foodAnalysisService) {
        this.foodAnalysisService = foodAnalysisService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/analyze")
    public String analyze(@RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (!imageFile.isEmpty()) {
            try {
                // 고유한 파일명 생성
                String originalFileName = imageFile.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = UUID.randomUUID().toString() + extension;

                // 파일 저장 경로 설정
                String filePath = uploadDir + fileName;

                // 파일 저장
                Path path = Paths.get(filePath);
                Files.createDirectories(path.getParent());
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // 분석 수행
                FoodInfo foodInfo = foodAnalysisService.analyzeImage("/uploads/"+fileName);
                model.addAttribute("foodInfo", foodInfo);
                return "result";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                // 오류 처리
                model.addAttribute("error", "이미지 업로드 및 분석 중 오류가 발생했습니다.");
                return "upload";
            }
        } else {
            model.addAttribute("error", "이미지 파일을 선택해주세요.");
            return "upload";
        }


    }
}