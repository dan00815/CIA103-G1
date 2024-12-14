// 獲取頁面中的 DOM 元素
const roomTypeSelect = document.getElementById("roomTypeSelect"); // 房型選擇下拉框
const addRoomButton = document.getElementById("addRoomButton"); // 添加房型按鈕
const selectedRoomsContainer = document.getElementById("selectedRooms"); // 已選擇房型的容器

// 使用 Map 存儲已選擇的房型及其數量
const selectedRooms = new Map();

// 為 "添加房型" 按鈕添加點擊事件
addRoomButton.addEventListener("click", () => {
    const selectedOption = roomTypeSelect.options[roomTypeSelect.selectedIndex]; // 獲取選中的房型選項

    // 如果未選擇有效的房型，提示錯誤並退出
    if (!selectedOption || !selectedOption.value) {
        alert("請選擇房型！");
        return;
    }

    // 獲取房型的相關資訊
    const roomTypeId = selectedOption.value; // 房型 ID
    const roomTypeName = selectedOption.dataset.name; // 房型名稱
    const roomPrice = selectedOption.dataset.price; // 房型價格
    const roomQty = parseInt(selectedOption.dataset.qty); // 可用房間數量

    // 如果房型已被選擇，提示錯誤並退出
    if (selectedRooms.has(roomTypeId)) {
        alert("該房型已選擇，請修改數量！");
        return;
    }

    // 創建房型顯示區域的 DOM 結構
    const roomDiv = document.createElement("div");
    roomDiv.classList.add("room-item"); // 添加 CSS 類名
    roomDiv.setAttribute("data-room-id", roomTypeId); // 設置房型 ID

    // 設置房型的 HTML 內容，包括名稱、價格、數量控制和刪除按鈕
    roomDiv.innerHTML = `
        <div>
            <h5>${roomTypeName}</h5>
            <span>${roomPrice}元</span>
            <input type="hidden" name="roomTypeId" value="${roomTypeId}">
            <input type="hidden" name="roomTypeName" value="${roomTypeName}">
            <input type="hidden" name="roomPrice" value="${roomPrice}">
        </div>
        <div class="quantity-controls">
            <button type="button" class="quantity-btn" onclick="decreaseQty(this)">-</button>
            <input type="number" name="roomQty" value="1" min="1" max="${roomQty}" class="quantity-input">
            <button type="button" class="quantity-btn" onclick="increaseQty(this)">+</button>
        </div>
        <button type="button" class="delete-btn" onclick="deleteRoom(this)"><i class="fas fa-trash-alt"></i></button>
    `;

    // 將房型 ID 和數量存入 Map
    selectedRooms.set(roomTypeId, 1);

    // 將房型區域添加到容器中
    selectedRoomsContainer.appendChild(roomDiv);
});

// 增加房型數量的函數
function increaseQty(button) {
    const input = button.previousElementSibling; // 獲取數量輸入框
    if (parseInt(input.value) < parseInt(input.max)) {
        input.value = parseInt(input.value) + 1; // 增加數量
    }
}

// 減少房型數量的函數
function decreaseQty(button) {
    const input = button.nextElementSibling; // 獲取數量輸入框
    if (parseInt(input.value) > 1) {
        input.value = parseInt(input.value) - 1; // 減少數量
    }
}

// 刪除房型的函數
function deleteRoom(button) {
    const roomDiv = button.parentElement; // 獲取房型區域
    const roomTypeId = roomDiv.getAttribute("data-room-id"); // 獲取房型 ID
    selectedRooms.delete(roomTypeId); // 從 Map 中刪除房型
    roomDiv.remove(); // 從頁面移除房型區域
}




/////////////////////////////////////////////////////