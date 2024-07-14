package pnu.project.smartplate.model;

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

    private String foodType;
    private String foodAmount;
    private String imageUrl;

    public FoodInfo() {

    }

    public FoodInfo(String foodType, String foodAmount, String imageUrl) {
        this.foodType = foodType;
        this.foodAmount = foodAmount;
        this.imageUrl = imageUrl;
    }
}
