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
        li.onclick = () => selectFood(food);
        results.appendChild(li);
    });
}

function selectFood(food) {
    document.getElementById('analyzed-food').textContent = food.name;
    document.getElementById('food-calories').textContent = calculatePerServing(food.calories) + ' kcal';
    document.getElementById('food-nutrients').textContent = `단백질 ${calculatePerServing(food.protein)}g, 지방 ${calculatePerServing(food.fat)}g, 탄수화물 ${calculatePerServing(food.carbs)}g`;
}

function updateServingSize() {
    const selectedFood = foodData.find(food => food.name === document.getElementById('analyzed-food').textContent);
    if (selectedFood) {
        selectFood(selectedFood);
    }
}

function calculatePerServing(value) {
    const servingSize = parseFloat(document.getElementById('serving-size').value);
    return (value * servingSize).toFixed(2);
}

function confirmSelection() {
    const selectedFood = document.getElementById('analyzed-food').textContent;
    alert(`선택된 음식: ${selectedFood}`);
}