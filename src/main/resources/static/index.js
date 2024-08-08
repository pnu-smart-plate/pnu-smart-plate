document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('fileInput');
    fileInput.addEventListener('change', uploadFile);

    function uploadFile(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const imageContainer = document.getElementById('imageContainer');
                const imageDataUrl = e.target.result;
                imageContainer.style.backgroundImage = `url(${imageDataUrl})`;
                imageContainer.textContent = '';

                // Store the image data URL in localStorage
                localStorage.setItem('uploadedImage', imageDataUrl);
            };
            reader.readAsDataURL(file);
        }
    }

    // Load the image from localStorage if available
    const savedImage = localStorage.getItem('uploadedImage');
    if (savedImage) {
        const imageContainer = document.getElementById('imageContainer');
        imageContainer.style.backgroundImage = `url(${savedImage})`;
        imageContainer.textContent = '';
    }
});
