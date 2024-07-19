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
                window.location.href = '/progressbar';
            })
            .catch(error => {
                console.error('이미지 전송 실패:', error);
                alert('이미지 분석에 실패했습니다. 다시 시도해주세요.');
            });
    } else {
        alert('이미지를 업로드해주세요.');
    }
}