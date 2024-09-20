package pnu.project.smartplate.controller;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pnu.project.smartplate.model.FoodNutrient;
import pnu.project.smartplate.service.FoodAnalysisService;

@Slf4j
@Controller
public class FoodAnalysisController {

    private final FoodAnalysisService foodAnalysisService;


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

    @GetMapping("/progressbar")
    public String progress() {
        return "progressbar";
    }

    @Autowired
    private HttpSession session;

    @GetMapping("/result")
    public String showResult(Model model) {
        Map<String, FoodNutrient> foodMap =
            (Map<String, FoodNutrient>) session.getAttribute("foodMap");
        String fileName = (String) session.getAttribute("fileName");
        if (foodMap != null && fileName != null) {
            log.info(fileName);
            model.addAttribute("foodNutrientMap", foodMap);
            model.addAttribute("imageName", fileName);
        } else {
            model.addAttribute("error", "분석된 데이터가 없습니다.");
        }

        return "result";
    }

    @PostMapping("/progressbar")
    public String analyze(@RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (!imageFile.isEmpty()) {
            try {
                session.removeAttribute("foodMap");
                session.removeAttribute("fileName");
                // 이미지 저장
                String fileName = foodAnalysisService.saveImg(imageFile);
                // 분석 수행
                Map<String, FoodNutrient> foodMap = new HashMap<>();
                foodMap = foodAnalysisService.analyzeImage(fileName);
                log.info(foodMap.toString());

                // 세션에 데이터 저장
                session.setAttribute("foodMap", foodMap);
                session.setAttribute("fileName", fileName);

                return "redirect:/result";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                // 오류 처리
                model.addAttribute("error", "이미지 업로드 및 분석 중 오류가 발생했습니다.");
                return "result";
            }
        } else {
            model.addAttribute("error", "이미지 파일을 선택해주세요.");
            return "upload";
        }
    }
}