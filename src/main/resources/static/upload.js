window.onload = function() {
  const savedImage = localStorage.getItem('uploadedImage');
  if (savedImage) {
    const imageContainer = document.getElementById('imageContainer');
    imageContainer.style.backgroundImage = `url(${savedImage})`;
    imageContainer.textContent = '';
  }
}

function startAnalysis() {
  const savedImage = localStorage.getItem('uploadedImage');
  if (savedImage) {
    // Convert base64 to blob
    const byteString = atob(savedImage.split(',')[1]);
    const mimeString = savedImage.split(',')[0].split(':')[1].split(';')[0];
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([ab], {type: mimeString});

    // Create FormData and append the image
    const formData = new FormData();
    formData.append('imageFile', blob, 'image.jpg');

    // Send the image to the server
    fetch('/progressbar', {
      method: 'POST',
      body: formData
    })
        .then(response => {
          if (response.ok) {
            // If successful, redirect to the progress page
            window.location.href = '/progressbar';
          } else {
            throw new Error('서버 응답 에러');
          }
        })
        .catch(error => {
          console.error('이미지 전송 실패:', error);
          alert('이미지 분석에 실패했습니다. 다시 시도해주세요.');
        });
  } else {
    alert('업로드된 이미지가 없습니다. 이미지를 먼저 선택해주세요.');
  }
}