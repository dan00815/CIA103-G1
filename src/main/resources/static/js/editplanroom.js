

document.addEventListener('DOMContentLoaded', function() {
    const roomQtyInput = document.getElementById('roomQty');
    const reservedRoomInput = document.getElementById('reservedRoom');
    const totalRoomQtyInput = document.getElementById('totalRoomQty');
    const form = document.querySelector('form');

    function validateRoomQty() {
        const roomQty = parseInt(roomQtyInput.value);
        const reservedRoom = parseInt(reservedRoomInput.value);
        const totalRoomQty = parseInt(totalRoomQtyInput.value);

        let isValid = true;
        let errorMessage = '';

        if (roomQty > totalRoomQty) {
            isValid = false;
            errorMessage = '分配房間數量不能大於房型總數！';
            roomQtyInput.classList.add('error');
        } else if (roomQty < reservedRoom) {
            isValid = false;
            errorMessage = '已預訂數量不能大於分配房間數量！';
            roomQtyInput.classList.add('error');
        } else {
            roomQtyInput.classList.remove('error');
        }

        return { isValid, errorMessage };
    }

    form.addEventListener('submit', function(e) {
        const validation = validateRoomQty();
        if (!validation.isValid) {
            e.preventDefault();
            alert(validation.errorMessage);
        }
    });

    roomQtyInput.addEventListener('input', function() {
        validateRoomQty();
    });
});