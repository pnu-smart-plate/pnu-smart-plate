package pnu.project.smartplate.model;

public enum FoodClass {

    rice("쌀밥"),
    kimchiFriedRice("김치볶음밥"),
    curryRice("카레라이스"),
    soybeanPasteSoup("시래기된장국"),
    Yukgaejang("육개장"),
    softTofuStew("순두부찌개"),
    chickenLibs("닭갈비"),
    smokedDuck("훈제오리"),
    rolledOmmlet("달걀말이"),
    friedFishCake("어묵볶음"),
    friedSquid("오징어볶음"),
    radishKimch("총각김치");

    private final String krName;

    FoodClass(String krName) {
        this.krName = krName;
    }

    public String getKrName() {
        return krName;
    }
}