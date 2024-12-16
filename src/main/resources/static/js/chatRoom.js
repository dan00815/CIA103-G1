//連接websocket
let userId = '';

const msgContainer = document.querySelector("#msgContainer");
let webSocket;

let con = false;

const user = '<div class="msg-bubble msg-left">';
const hoster = '<div class="msg-bubble msg-right">';

let isEmpOline = true;

//document.querySelector("#liveChatButton").addEventListener("click", function() {
//	if (userName.trim() === '') {
//		//    memberLogin();
//	} else {
//		// memberLogin();
//		//		chatbox.classList.toggle("hide");
//		//		msgContainer.scrollTop = msgContainer.scrollHeight;
//		//		if (!document.querySelector("#alert").classList.contains("hide")) {
//		//			document.querySelector("#alert").classList += " hide";
//		//		}
//		if (con === false) {
//			connect();
//		}
//	}
//});

const baseUrl = window.location.protocol + "//" + window.location.host + "/chatRoom";
// 網頁載入後執行
window.addEventListener("load", function(e) {
	this.fetch(baseUrl, {
		method: 'GET'
	}).then(response => {
		if (response.status == 401) {
			// 如果未授權，跳轉到登入頁面
			//            this.location.href = baseUrl + '/tmp/back_end/host/hostLogin.html';
		} else {
			// 授權成功，建立 WebSocket 連線
			connect();
		}
	});
});

function connect() {
	// 建立 websocket
	webSocket = new WebSocket(endPointURL + userName);
	webSocket.onopen = function(event) {
		// 初始化連線，只會連線一次
		console.log("Connect Success!");
		con = true;
		let jsonObj = {
			type: "openChatRoom",
			sender: userName,
			receiver: userName,
		};
		webSocket.send(JSON.stringify(jsonObj));
	}

	webSocket.onmessage = function(event) {
		let data = JSON.parse(event.data);
		console.log(data);
		// 一般訊息
		if (data.type === 0) {
			buildMessage(data.data);
		}
		// 建立聊天室清單
		if (data.type === 1) {
			buildChatRoomList(data.data);
		}
		//歷史訊息
		if (data.type === 2) {
			buildHisMessage(data.data);
		}
		// 客服上線
		if (data.type === 3) {
			isEmpOline = true;
			buildHisMessage(data.data);
		}
		// 客服不在線上
		if (data.type === 4) {
			isEmpOline = false;
			buildOfflineMessage();
		}
	}
}

function buildOfflineMessage() {
	let div = document.createElement("div");
	div.className = "msg-container";
	let now = new Date();
	let time = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes();
	let content =
		hoster +
		`<p>您好！目前客服人員不在線，我們會盡快回覆您的訊息，謝謝！</p>
		<span class="msg-time">${time}</span>
		</div>`;
	div.innerHTML = content;
	msgContainer.appendChild(div);
	msgContainer.scrollTop = msgContainer.scrollHeight;
}

//-- input 欄位按Enter(keycode:13)傳送訊息出去 --//
$("#btn-input").on("keydown", function(e) {
	if (e.which === 13) {
		$("#btn-chat").click();
	}
});

function sendMessage() {
	let inputMessage = document.getElementById("btn-input");
	let message = inputMessage.value.trim();

	if (message === "") {
		alert("您未輸入訊息");
		inputMessage.focus();
	} else {
		let now = new Date();
		let nowStr = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes();
		let jsonObj = {
			type: "message",
			sender: userName,
			receiver: "host",
			message: message,
			time: nowStr,
			status: "unread",
		};
		webSocket.send(JSON.stringify(jsonObj));
		inputMessage.value = "";
		inputMessage.focus();
		buildMessage(jsonObj);
	}
}

function buildMessage(data) {
	let div = document.createElement("div");
	div.className = "msg-container";
	let jsonObj = data;
	let showMsg = jsonObj.message;
	let time = jsonObj.time;
	let content = "";
	// 根據發送者是自己還是對方來給予不同的html
	if (jsonObj.sender === userName) {
		content =
			user +
			'<p>' + showMsg + '</p>' +
			'<span class="msg-time">' + time + '</span>'
		'</div>';
	} else {
		content =
			hoster +
			'<p>' + showMsg + '</p>' +
			'<span class="msg-time">' + time + '</span>'
		'</div>';
	}
	div.innerHTML = content;
	msgContainer.appendChild(div);
	if (chatbox.classList.contains("hide")) {
		document.querySelector("#alert").classList.toggle("hide");
	} else {
		msgContainer.scrollTop = msgContainer.scrollHeight;
	}
}

function buildHisMessage(data) {
	msgContainer.innerHTML = "";
	// 這行的jsonObj.message是從redis撈出跟客服的歷史訊息，再parse成JSON格式處理
	let messages = data;
	for (let i = 0; i < messages.length; i++) {
		let div = document.createElement("div");
		div.className = "msg-container";
		let historyData = JSON.parse(messages[i]);
		let showMsg = historyData.message;
		let time = historyData.time;
		let content = "";
		// 根據發送者是自己還是對方來給予不同的html
		if (historyData.sender === userName) {
			content =
				user +
				'<p>' + showMsg + '</p>' +
				'<span class="msg-time">' + time + '</span>'
			'</div>';
		} else {
			content =
				hoster +
				'<p>' + showMsg + '</p>' +
				'<span class="msg-time">' + time + '</span>'
			'</div>';
		}
		div.innerHTML = content;
		msgContainer.appendChild(div);
	}
	msgContainer.scrollTop = msgContainer.scrollHeight;
}
