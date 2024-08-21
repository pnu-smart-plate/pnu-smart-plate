const foodData = [
  { name: '김치', calories: 50, protein: 2, fat: 0.5, carbs: 10, baseWeight: 100 },
  { name: '비빔밥', calories: 500, protein: 12, fat: 10, carbs: 80, baseWeight: 250 },
  { name: '된장찌개', calories: 150, protein: 8, fat: 5, carbs: 20, baseWeight: 200 },
  { name: '불고기', calories: 300, protein: 20, fat: 15, carbs: 25, baseWeight: 150 },
  { name: '갈비탕', calories: 400, protein: 30, fat: 20, carbs: 35, baseWeight: 300 },
  { name: '삼겹살', calories: 600, protein: 15, fat: 55, carbs: 0, baseWeight: 100 },
  { name: '잡채', calories: 350, protein: 6, fat: 10, carbs: 65, baseWeight: 200 },
  { name: '김밥', calories: 200, protein: 4, fat: 2, carbs: 40, baseWeight: 150 },
  { name: '냉면', calories: 250, protein: 5, fat: 2, carbs: 50, baseWeight: 300 },
  { name: '떡볶이', calories: 400, protein: 6, fat: 10, carbs: 75, baseWeight: 250 }
];

let selectedFoods = [];

// 입력 리스트 처리 함수
function addFoodsFromList(foodList) {
  foodList.forEach(item => {
    const food = foodData.find(f => f.name === item.name);
    if (food) {
      const foodItem = {
          ...food,
          servingSize: item.weight
      };
      selectedFoods.push(foodItem);
    }
  });
  renderSelectedFoods();
  updateSummary();
}

function searchFood() {
  const query = document.getElementById('search-input').value.toLowerCase();
  const results = document.getElementById('search-results');
  results.innerHTML = '';

  if (query.length < 1) {
      return;
  }

  const filteredFoods = foodData.filter(food => food.name.toLowerCase().includes(query));
  filteredFoods.forEach(food => {
      const li = document.createElement('li');
      li.textContent = food.name;
      li.onclick = () => addFood(food);
      results.appendChild(li);
  });
}

function addFood(food) {
  if (!selectedFoods.some(f => f.name === food.name)) {
      const foodItem = {
          ...food,
          servingSize: food.baseWeight // 기본 무게로 초기 설정
      };
      selectedFoods.push(foodItem);
      renderSelectedFoods();
      updateSummary();
  }
}

function removeFood(foodName) {
  selectedFoods = selectedFoods.filter(food => food.name !== foodName);
  renderSelectedFoods();
  updateSummary();
}

function updateServingSize(foodName, servingSize) {
  const food = selectedFoods.find(f => f.name === foodName);
  if (food) {
      food.servingSize = parseFloat(servingSize);
      document.getElementById(`serving-size-${foodName}`).textContent = servingSize + 'g';
      updateSummary();
  }
}

function renderSelectedFoods() {
  const selectedFoodsContainer = document.getElementById('selected-foods');
  selectedFoodsContainer.innerHTML = '';

  selectedFoods.forEach(food => {
      const foodItemDiv = document.createElement('div');
      foodItemDiv.className = 'food-item';
      foodItemDiv.innerHTML = `
          <span>${food.name}</span>
          <input type="range" min="${food.baseWeight / 10}" max="${food.baseWeight * 3}" value="${food.servingSize}" oninput="updateServingSize('${food.name}', this.value)">
          <span id="serving-size-${food.name}">${food.servingSize}g</span>
          <button onclick="removeFood('${food.name}')">제거</button>
      `;
      selectedFoodsContainer.appendChild(foodItemDiv);
  });
}

function updateSummary() {
  let totalCalories = 0;
  let totalProtein = 0;
  let totalFat = 0;
  let totalCarbs = 0;

  selectedFoods.forEach(food => {
      const factor = food.servingSize / food.baseWeight;
      totalCalories += food.calories * factor;
      totalProtein += food.protein * factor;
      totalFat += food.fat * factor;
      totalCarbs += food.carbs * factor;
  });

  document.getElementById('total-calories').textContent = totalCalories.toFixed(2) + ' kcal';
  document.getElementById('total-nutrients').textContent = `단백질 ${totalProtein.toFixed(2)}g, 지방 ${totalFat.toFixed(2)}g, 탄수화물 ${totalCarbs.toFixed(2)}g`;
}

document.addEventListener("DOMContentLoaded", function() {
  // 테스트용 음식 리스트 입력
  const inputFoodList = [
    { name: '불고기', weight: 200 },
    { name: '김밥', weight: 150 },
    { name: '떡볶이', weight: 300 }
  ];

  addFoodsFromList(inputFoodList); // 리스트를 통해 음식 추가
});
