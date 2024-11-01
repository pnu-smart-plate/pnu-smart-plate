

let selectedFoods = [];

document.addEventListener('DOMContentLoaded', function() {
    loadImage();
    setupSearchListener();
    addFoodsFromList(inputFoodList);
});


function setupSearchListener() {
    const searchInput = document.getElementById('search-input');
    searchInput.addEventListener('input', searchFood);
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

function addFood(food) {
    if (!selectedFoods.some(f => f.name === food.name)) {
        const foodItem = {
            ...food,
            servingSize: food.baseWeight
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
            <input type="range" min="${food.baseWeight / 2}" max="${food.baseWeight * 2}" value="${food.servingSize}" oninput="updateServingSize('${food.name}', this.value)">
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

window.onload = function () {
  const savedImage = localStorage.getItem('uploadedImage');
  if (savedImage) {
    console.log("저장된 이미지를 찾았습니다.");
    const resultImage = document.getElementById('result-image');
    resultImage.src = savedImage;
    resultImage.style.display = 'block'; // 이미지가 보이도록 설정
  } else {
    console.log("저장된 이미지가 없습니다.");
  }
}

function loadImage() {
    const img = document.getElementById('result-image');
    const imageName = img.dataset.imageName;
    const savedImage = localStorage.getItem('uploadedImage');

    if (savedImage) {
        console.log("localStorage에서 이미지를 불러왔습니다.");
        img.src = savedImage;
    } else if (imageName) {
        let attempts = 0;
        const maxAttempts = 5;

        function tryLoadImage() {
            img.src = `/uploads/${imageName}?t=${new Date().getTime()}`;
            img.onerror = function() {
                if (attempts < maxAttempts) {
                    attempts++;
                    setTimeout(tryLoadImage, 1000);
                } else {
                    console.error('이미지 로딩 실패:', imageName);
                }
            };
            img.onload = function() {
                console.log('이미지 로딩 성공:', imageName);
            };
        }

        tryLoadImage();
    } else {
        console.error('이미지를 불러올 수 없습니다.');
    }
}