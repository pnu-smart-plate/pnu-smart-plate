function uploadFile(event) {
  const file = event.target.files[0];
  const reader = new FileReader();
  reader.onload = function (e) {
    const imageContainer = document.getElementById('imageContainer');
    const imageDataUrl = e.target.result;
    imageContainer.style.backgroundImage = `url(${imageDataUrl})`;
    imageContainer.textContent = '';

    // Store the image data URL in localStorage
    localStorage.setItem('uploadedImage', imageDataUrl);
    console.log(imageDataUrl)
  };
  reader.readAsDataURL(file);
}

// Load the image from localStorage if available
window.onload = function () {
  const savedImage = localStorage.getItem('uploadedImage');
  if (savedImage) {
    const imageContainer = document.getElementById('imageContainer');
    imageContainer.style.backgroundImage = `url(${savedImage})`;
    imageContainer.textContent = '';
  }
}