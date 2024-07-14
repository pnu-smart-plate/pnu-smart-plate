document.addEventListener('DOMContentLoaded', (event) => {
  const calendarElement = document.getElementById('calendar');
  const savedResults = JSON.parse(localStorage.getItem('savedResults')) || {};

  function generateCalendar() {
    const today = new Date();
    const month = today.getMonth();
    const year = today.getFullYear();
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    
    let calendarHtml = '<table><tr>';
    for (let i = 0; i < firstDay.getDay(); i++) {
      calendarHtml += '<td></td>';
    }
    
    for (let day = 1; day <= lastDay.getDate(); day++) {
      if ((firstDay.getDay() + day - 1) % 7 === 0) {
        calendarHtml += '</tr><tr>';
      }
      const dateKey = `${year}-${month + 1}-${day}`;
      const result = savedResults[dateKey] ? savedResults[dateKey].join(', ') : '';
      calendarHtml += `<td><div>${day}</div><div>${result}</div></td>`;
    }
    
    calendarHtml += '</tr></table>';
    calendarElement.innerHTML = calendarHtml;
  }

  generateCalendar();
});
