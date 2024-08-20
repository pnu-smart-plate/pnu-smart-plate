const foodData = [
  { name: '김치', calories: 50, protein: 2, fat: 0.5, carbs: 10 },
  { name: '비빔밥', calories: 500, protein: 12, fat: 10, carbs: 80 },
  { name: '된장찌개', calories: 150, protein: 8, fat: 5, carbs: 20 },
  { name: '불고기', calories: 300, protein: 20, fat: 15, carbs: 25 },
  { name: '갈비탕', calories: 400, protein: 30, fat: 20, carbs: 35 },
  { name: '삼겹살', calories: 600, protein: 15, fat: 55, carbs: 0 },
  { name: '잡채', calories: 350, protein: 6, fat: 10, carbs: 65 },
  { name: '김밥', calories: 200, protein: 4, fat: 2, carbs: 40 },
  { name: '냉면', calories: 250, protein: 5, fat: 2, carbs: 50 },
  { name: '떡볶이', calories: 400, protein: 6, fat: 10, carbs: 75 }
];

let selectedFoods = [];

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
          servingSize: 1
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
          <select onchange="updateServingSize('${food.name}', this.value)">
              <option value="0.5" ${food.servingSize === 0.5 ? 'selected' : ''}>적음</option>
              <option value="1" ${food.servingSize === 1 ? 'selected' : ''}>보통</option>
              <option value="1.5" ${food.servingSize === 1.5 ? 'selected' : ''}>많음</option>
          </select>
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
      totalCalories += food.calories * food.servingSize;
      totalProtein += food.protein * food.servingSize;
      totalFat += food.fat * food.servingSize;
      totalCarbs += food.carbs * food.servingSize;
  });

  document.getElementById('total-calories').textContent = totalCalories.toFixed(2) + ' kcal';
  document.getElementById('total-nutrients').textContent = `단백질 ${totalProtein.toFixed(2)}g, 지방 ${totalFat.toFixed(2)}g, 탄수화물 ${totalCarbs.toFixed(2)}g`;
}

function previewImage() {
  const file = document.getElementById('image-upload').files[0];
  const reader = new FileReader();

  reader.onloadend = function () {
      document.getElementById('result-image').src = reader.result;
  }

  if (file) {
      reader.readAsDataURL(file);
  } else {
      document.getElementById('result-image').src = "";
  }
}
