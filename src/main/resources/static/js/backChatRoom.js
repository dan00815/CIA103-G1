const baseUrl = window.location.protocol + "//" + window.location.host + "/backChatRoom";

//設定websocket連線
const ServerPoint = `/chat/host`;
const lohost = window.location.host;
const path = window.location.pathname;
const webCtx = path.substring(0, path.indexOf("/", 1));
const endPointURL = "ws://" + lohost + webCtx + ServerPoint;

const msgBody = document.querySelector("#message-list");
// const self = host;
let webSocket;

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
	// create a websocket
	webSocket = new WebSocket(endPointURL);
	console.log(endPointURL);
	webSocket.onopen = function(event) {
		console.log("Connect Success!");
		initChatRoom();
	};

	function initChatRoom() {
		let jsonObj = {
			type: "userList",
			sender: "host",
		};
		if (webSocket.readyState === WebSocket.OPEN) {
			// 確保 WebSocket 已經連接成功
			webSocket.send(JSON.stringify(jsonObj));
		}
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
		// 歷史訊息
		if (data.type === 2) {
			buildHisMessage(data.data);
		}
	}

	
}

// 建立聊天室清單
//function buildChatRoomList(data) {
//	let userList = data;
//	let chatRoomList = document.getElementById("online-list");
//	chatRoomList.innerHTML = "";
//	
//	for (let user of userList) {
//		let userRow = "";
//		userRow =
//			`<a href="#" class="d-flex align-items-center a target-member" id="user${user.userName}" onclick="showUserChatBox(event);">
//					<div class="side-user">
//	                    <img id="avatar-${user.userName}" alt="User Avatar" width="40" height="40">
//						<div class="flex-grow-1 ms-3">
//	                    <span class="userName">${user.userName}</span>
//						<p class="userMessage">${user.lastMessage.message}</p>
//						</div>
//	                </div>
//                </a>`;
//		chatRoomList.innerHTML += userRow;
//		if (user.lastMessage.status === "read") {
//			console.log(user.lastMessage.status);
////			document.querySelector(`#alert${user.userName}`).classList.toggle("hide");
//		}
//	}
//}

function buildChatRoomList(data) {
    let userList = data;
    let chatRoomList = document.getElementById("online-list");
    chatRoomList.innerHTML = "";

	// 假設每個用戶都有 userName
	for (let user of userList) {
	    let userRow = "";
	    userRow = 
	        `<a href="#" class="d-flex align-items-center a target-member" id="user${user.userName}" onclick="showUserChatBox(event);">
	            <div class="side-user">
	                <img id="avatar-${user.userName}" alt="User Avatar" width="40" height="40">
	                <div class="flex-grow-1 ms-3">
	                    <span class="userName">${user.userName}</span>
	                    <p class="userMessage">${user.lastMessage.message}</p>
	                </div>
	            </div>
	        </a>`;
	    chatRoomList.innerHTML += userRow;

	    // 圖片加載
	    const avatarEndpoint = `/member/${user.userName}/avatar`;

	    fetch(avatarEndpoint)
	        .then(response => response.text()) // 直接返回圖片的 URL 字符串
	        .then(imageUrl => {
	            const avatarImage = document.getElementById(`avatar-${user.userName}`);
	            avatarImage.src = imageUrl;  // 設置圖片 URL
	        })
	        .catch(error => {
	            console.error("Error fetching avatar:", error);
	        });
	}
}


let currentMember = "";
function showUserChatBox(e) {
	// 被觸擊的元素
	let triggerEl = $(e.target);
	// 找出共同父層且轉為jQuery Object
	let targetParent = $(triggerEl.parents(".target-member")[0]);
	// 找出目標img且轉為jQuery Object
	//    let alertImg = $(targetParent.find(".flex-shrink-0").find("img")[1]);
	// 移除img
	//    alertImg.remove();

	// 找出userName
	let userName = $(targetParent.find("span")).text();

	let jsonObj = {
		type: "openChatRoom",
		sender: "host",
		receiver: userName,
	};
	currentMember = userName;
	webSocket.send(JSON.stringify(jsonObj));
}


function buildHisMessage(data) {
	// 歷史訊息時
	document.querySelector("#userName").innerText = currentMember; // jsonObj.receiver
//	document.querySelector("#userImg").innerHTML = '<img style="width: 50px;" src="./img/kitty.png">';
//	document.querySelector("#userId").innerText = "u" + JSON.parse(data[0]).receiver;
	// 這行的jsonObj.message是從redis撈出跟客服的歷史訊息，再parse成JSON格式處理
	let div = $("#message-list");
	div.html("");
	for (let i = 0; i < data.length; i++) {
		let historyData = JSON.parse(data[i]);
		let className = historyData.sender === "host" ? "msg-bubble msg-right" : "msg-bubble msg-left";
		let showMsg = '<div class="msg-container">' + "<div class='" + className + "'><p>" + historyData.message +"</p>"+ '<span class="msg-time">'+ historyData.time +'</span></div></div>';
		// 根據發送者是自己還是對方來給予不同的class名, 以達到訊息左右區分
		div.append(showMsg);
	}
//	document.querySelector("#chatbox").style.display = "flex";
	msgBody.scrollTop = msgBody.scrollHeight;
}

//-- input 欄位按Enter(keycode:13)傳送訊息出去 --//
$(document).on("keydown", function (e) {
    if (e.which === 13) {
        e.preventDefault();
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
        // let userIds = document.querySelector("#userName").innerText.substring(1);
        let now = new Date();
        let nowStr = now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + " " + now.getHours() + ":" + now.getMinutes();
        let jsonObj = {
            type: "message",
            sender: "host",
            receiver: currentMember,
            message: message,
            time: nowStr,
        };
        webSocket.send(JSON.stringify(jsonObj));
        inputMessage.value = "";
        inputMessage.focus();
        buildMessage(jsonObj);
    }
}

function buildMessage(data) {
    if (currentMember) {
        let div = $("#message-list");
        let historyData = data;
		let className = historyData.sender === "host" ? "msg-bubble msg-right" : "msg-bubble msg-left";
		let showMsg = '<div class="msg-container">' + "<div class='" + className + "'><p>" + historyData.message +"</p>"+ '<span class="msg-time">'+ historyData.time +'</span></div></div>';
		// 根據發送者是自己還是對方來給予不同的class名, 以達到訊息左右區分
        div.append(showMsg);
//        document.querySelector("#chatbox").style.display = "flex";
        msgBody.scrollTop = msgBody.scrollHeight;
    }
}

function disconnect() {
    webSocket.close();
}

