function previewImage(event) {
    const input = event.target;
    const reader = new FileReader();
    reader.onload = function() {
      const preview = document.getElementById('uploaded-image');
      preview.src = reader.result;
  
      // 로컬 스토리지에 이미지 데이터 저장
      // localStorage.setItem('uploadedImage', reader.result);
    };
    reader.readAsDataURL(input.files[0]);
  }
  
  function analyzeImage() {
    // 결과 페이지로 이동
    window.location.href = 'result.html';
  }
  