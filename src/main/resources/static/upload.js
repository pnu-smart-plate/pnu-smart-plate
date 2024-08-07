function previewImage(event) {
    const input = event.target;
    const reader = new FileReader();
    reader.onload = function () {
        const preview = document.getElementById('uploaded-image');
        preview.src = reader.result;

        // 로컬 스토리지에 이미지 데이터 저장
        localStorage.setItem('uploadedImage', reader.result);
    };
    reader.readAsDataURL(input.files[0]);
}
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

function analyzeImage(event) {
    event.preventDefault();

    const fileInput = document.getElementById('file-input');
    const file = fileInput.files[0];

    if (file) {
        const formData = new FormData();
        formData.append('imageFile', file);

        fetch('/result', {
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(data => {
                console.log('서버 응답:', data);
                window.location.href = '/result';
            })
            .catch(error => {
                console.error('이미지 전송 실패:', error);
                alert('이미지 분석에 실패했습니다. 다시 시도해주세요.');
            });
    } else {
        alert('이미지를 업로드해주세요.');
    }
}