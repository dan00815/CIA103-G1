document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
});

function initializeEventListeners() {
    document.querySelectorAll('.event').forEach(function(eventElement) {
        eventElement.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            //宣告=
            const eventData = {
                id: this.getAttribute('data-event-id'),
                title: this.getAttribute('data-event-name'),
                desc: this.getAttribute('data-event-desc'),
                date: this.getAttribute('data-event-date'),
                time: this.getAttribute('data-event-time'),
                max: this.getAttribute('data-event-max'),
                attend: this.getAttribute('data-event-attend')
            };

            showEventPopup(eventData);
        });
    });
}

function showEventPopup(eventData) {
    const backdrop = document.createElement('div');
    backdrop.className = 'popup-backdrop';

    // 參與人數和上限為數字進行比較 先轉換= =
    const attendCount = parseInt(eventData.attend) || 0;
    const maxCount = parseInt(eventData.max) || Infinity;

    // 看是否已額滿
    const isFull = maxCount !== Infinity && attendCount >= maxCount;

    const popup = document.createElement('div');
    popup.className = 'event-popup';


    popup.innerHTML = `
        <button class="popup-close">&times;</button>
        <div class="popup-header"><a href="/front/detail/${eventData.id}">${eventData.title}</a></div>
        <div class="popup-content">
            <div class="popup-row">
                <span class="popup-label">日期：</span>
                <span class="popup-value">${eventData.date}</span>
            </div>
            <div class="popup-row">
                <span class="popup-label">時間：</span>
                <span class="popup-value">${eventData.time}</span>
            </div>
            <div class="popup-row">
                <span class="popup-label">描述：</span>
                <span class="popup-value">${eventData.desc || '暫無描述'}</span>
            </div>

            <div class="attendance-info">
                <div class="attendance-header">報名資訊</div>
                <div class="attendance-stats">
                    <div class="attendance-item">
                        <div class="attendance-label">已報名人數</div>
                        <div class="attendance-number already-signed">${eventData.attend || '0'}</div>
                    </div>
                    <div class="attendance-item">
                        <div class="attendance-label">人數上限</div>
                        <div class="attendance-number ${isFull ? 'full' : ''}">${isFull ? '已額滿' : (eventData.max || '無限制')}</div>
                    </div>
                </div>
            </div>
        </div>
    `;

    function closePopup() {
        document.body.removeChild(backdrop);
        document.body.removeChild(popup);
    }

    popup.querySelector('.popup-close').onclick = closePopup;
    backdrop.onclick = closePopup;

    document.body.appendChild(backdrop);
    document.body.appendChild(popup);
}