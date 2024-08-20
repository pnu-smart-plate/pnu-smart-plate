package pnu.project.smartplate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class FoodNutrient {
    private String Name;
    private double amount;
    private double calory;
    private double carbo; // 탄수화물
    private double protein; // 단백질
    private double fat; // 지방
    private double sugar; // 당류
}
