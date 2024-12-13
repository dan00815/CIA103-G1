const CLIENT_ID = '402761261358-8j4oatc61kdgn23bbuk0mqk8s7hgoco7.apps.googleusercontent.com';
const API_KEY = 'AIzaSyDTFVY_ElsUuIPbk8LH79BdNvp4XFdQB2g';

let tokenClient;
let gapiInitialized = false;
let gisInitialized = false;

document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('calendarButton')) {
        initializeGoogleApi();
    }
});

async function initializeGoogleApi() {
    try {
        await new Promise((resolve) => gapi.load('client', resolve));
        await gapi.client.init({
            apiKey: API_KEY,
            discoveryDocs: ['https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest']
        });

        tokenClient = google.accounts.oauth2.initTokenClient({
            client_id: CLIENT_ID,
            scope: 'https://www.googleapis.com/auth/calendar.events',
            callback: ''
        });

        gapiInitialized = true;
        gisInitialized = true;
        console.log('Google APIs 初始化成功');
    } catch (error) {
        console.error('初始化 Google APIs 失敗:', error);
    }
}

async function addToGoogleCalendar() {
    const btn = document.getElementById('calendarButton');
    const originalContent = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 處理中...';

    try {
        if (!gapiInitialized || !gisInitialized) {
            throw new Error('API 尚未初始化完成，請重新整理頁面');
        }

        // 使用 data 屬性獲取資訊
        const eventName = document.querySelector('[data-event-name]')?.dataset.eventName;
        const startDate = document.querySelector('[data-start-date]')?.dataset.startDate;
        const orderId = document.querySelector('[data-order-id]')?.dataset.orderId;

        console.log('Event Details:', { eventName, startDate, orderId }); // 調試用

        if (!eventName || !startDate || !orderId) {
            throw new Error('無法取得行程資訊，請確認所有資料都已正確載入');
        }

        const event = {
            'summary': eventName,
            'description': `訂單編號: ${orderId}`,
            'start': {
                'date': startDate,
                'timeZone': 'Asia/Taipei'
            },
            'end': {
                'date': new Date(new Date(startDate).getTime() + 24*60*60*1000).toISOString().split('T')[0],
                'timeZone': 'Asia/Taipei'
            },
            'reminders': {
                'useDefault': false,
                'overrides': [
                    {'method': 'popup', 'minutes': 24 * 60}
                ]
            }
        };

        await new Promise((resolve, reject) => {
            tokenClient.callback = async (resp) => {
                if (resp.error !== undefined) {
                    reject(new Error('授權失敗'));
                    return;
                }
                try {
                    const response = await gapi.client.calendar.events.insert({
                        'calendarId': 'primary',
                        'resource': event
                    });
                    resolve(response);
                } catch (err) {
                    reject(err);
                }
            };

            if (gapi.client.getToken() === null) {
                tokenClient.requestAccessToken({ prompt: 'consent' });
            } else {
                tokenClient.requestAccessToken({ prompt: '' });
            }
        }).then(response => {
            if (response && response.result && response.result.htmlLink) {
                alert('行程已成功加入 Google 日曆！');
                window.open(response.result.htmlLink);
            }
        });

    } catch (error) {
        console.error('Error:', error);
        alert('新增失敗：' + (error.message || '請稍後再試'));
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalContent;
    }
}