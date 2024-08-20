package pnu.project.smartplate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class FoodInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "식품명")
    private String foodName;

    @Column(name = "식품량")
    private String foodAmount;

    @Column(name = "img")
    private String imageUrl;

    @Column(name = "중량_g")
    private int weightG;

    @Column(name = "에너지_kcal")
    private int energyKcal;

    @Column(name = "탄수화물_g")
    private int carbohydrateG;

    @Column(name = "당류_g")
    private int sugarG;

    @Column(name = "지방_g")
    private int fatG;

    @Column(name = "단백질_g")
    private int proteinG;

    @Column(name = "칼슘_mg")
    private int calciumMg;

    @Column(name = "인_mg")
    private int phosphorusMg;

    @Column(name = "나트륨_mg")
    private int sodiumMg;

    @Column(name = "칼륨_mg")
    private int potassiumMg;

    @Column(name = "마그네슘_mg")
    private int magnesiumMg;

    @Column(name = "철_mg")
    private int ironMg;

    @Column(name = "아연_mg")
    private int zincMg;

    @Column(name = "콜레스테롤_mg")
    private int cholesterolMg;

    @Column(name = "트랜스지방_g")
    private int transFatG;

    public FoodInfo() {

    }

    public FoodInfo(String foodName,String foodAmount, String imageUrl) {
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.imageUrl = imageUrl;
    }
}
