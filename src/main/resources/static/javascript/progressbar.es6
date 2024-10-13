document.addEventListener("DOMContentLoaded", function() {
    const statusHeader = document.getElementById("status-header");

    setTimeout(function() {
      statusHeader.textContent = "음식 종류 분석중...";
    }, 0); // 페이지 로드 후 즉시 변경

    setTimeout(function() {
      statusHeader.textContent = "양 추정 중...";
    }, 2000); // 2초 후 변경\
    setTimeout(function () {
      window.location.href = '/result';
    }, 4000);
  });