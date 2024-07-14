document.addEventListener('DOMContentLoaded', (event) => {
    // 로컬 스토리지에서 이미지 데이터 가져오기
    const resultImage = document.getElementById('result-image');
    const uploadedImage = localStorage.getItem('uploadedImage');
  
    if (uploadedImage) {
      resultImage.src = uploadedImage;
    }
  
    // 분석 결과를 백엔드에서 받아오는 로직 구현 (예시 하드코딩 값)
    const foodType = document.getElementById('food-type');
    const foodAmount = document.getElementById('food-amount');
  
    foodType.innerText = '밥'; // 예시로 '밥'으로 설정
    foodAmount.innerText = '보통'; // 예시로 '보통'으로 설정
  });
  
  function submitResults() {
    const customType = document.getElementById('custom-type').value;
    const customAmount = document.getElementById('custom-amount').value;
  
    // 결과를 로컬 스토리지에 저장
    const today = new Date();
    const dateKey = `${today.getFullYear()}-${today.getMonth() + 1}-${today.getDate()}`;
    const savedResults = JSON.parse(localStorage.getItem('savedResults')) || {};
  
    savedResults[dateKey] = savedResults[dateKey] || [];
    savedResults[dateKey].push(`${customType} (${customAmount})`);
  
    localStorage.setItem('savedResults', JSON.stringify(savedResults));
  
    alert('결과가 저장되었습니다.');
    
    // 저장된 결과 페이지로 이동
    window.location.href = 'calendar.html';
  }
  