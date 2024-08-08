function updateProgressBar(percentage) {
    const progressBar = document.getElementById('progress-bar');
    progressBar.style.width = percentage + '%';
    progressBar.innerText = percentage + '%';
}

function checkAnalysisStatus() {
    fetch('/check-status')
        .then(response => response.json())
        .then(data => {
            if (data.status === 'completed') {
                updateProgressBar(100);
                window.location.href = 'result.html';
            } else {
                const progress = data.progress || 0;  // 백엔드에서 진행률을 제공한다고 가정
                updateProgressBar(progress);
                document.getElementById('progress-status').innerText = data.message;
                setTimeout(checkAnalysisStatus, 1000); // 1초마다 상태를 확인
            }
        })
        .catch(error => console.error('Error:', error));
}

// Start checking the analysis status when the page loads
window.onload = checkAnalysisStatus;