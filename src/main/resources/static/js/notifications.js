$(document).ready(function() {
    // 更新未读消息数量
    function updateUnreadCount() {
        $.get('/member/notify/unread-count', function(count) {
            $('#unreadCount').text(count);
        });
    }

    // 加载通知列表
    function loadNotifications(type) {
        let url = `/member/notify/type/${type}`;
        $.get(url, function(notifications) {
            $('#notificationList').empty();
            notifications.forEach(function(notify) {
                let notificationHtml = createNotificationHtml(notify);
                $('#notificationList').append(notificationHtml);
            });
        });
    }

    // 加载通知历史记录
    function loadNotificationHistory(notifyId, historyList) {
        $.get(`/member/notify/${notifyId}/history`, function(history) {
            historyList.empty();
            history.forEach(function(item) {
                let historyHtml = `
                    <div class="history-item">
                        <div class="d-flex justify-content-between">
                            <small class="text-muted">${new Date(item.notifyTime).toLocaleString()}</small>
                        </div>
                        <div class="mt-1">${item.notifyCon}</div>
                    </div>
                `;
                historyList.append(historyHtml);
            });
        });
    }

    // 创建通知HTML
    function createNotificationHtml(notify) {
        return `
            <div class="notification-item ${notify.isRead ? 'read' : ''}" data-id="${notify.notifyId}">
                <div class="notification-header">
                    <div>
                        <span class="badge bg-info">${notify.notifyTypeText}</span>
                        <span class="notification-time">${new Date(notify.notifyTime).toLocaleString()}</span>
                        <i class="bi bi-chevron-down expand-btn" title="展开历史记录"></i>
                    </div>
                    <div class="notification-actions">
                        ${!notify.isRead ? `
                            <button class="btn btn-sm btn-outline-primary mark-read-btn">
                                <i class="bi bi-check"></i>
                            </button>
                        ` : ''}
                        <button class="btn btn-sm btn-outline-danger delete-btn">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="notification-content">${notify.notifyCon}</div>
                <div class="history-list"></div>
            </div>
        `;
    }

    // 展开/收起历史记录
    $(document).on('click', '.expand-btn', function() {
        let notificationItem = $(this).closest('.notification-item');
        let historyList = notificationItem.find('.history-list');
        let notifyId = notificationItem.data('id');

        if (historyList.is(':visible')) {
            historyList.slideUp();
            $(this).removeClass('bi-chevron-up').addClass('bi-chevron-down');
        } else {
            loadNotificationHistory(notifyId, historyList);
            historyList.slideDown();
            $(this).removeClass('bi-chevron-down').addClass('bi-chevron-up');
        }
    });

    // 标记单个通知为已读
    $(document).on('click', '.mark-read-btn', function() {
        let notificationItem = $(this).closest('.notification-item');
        let notifyId = notificationItem.data('id');
        
        $.post(`/member/notify/${notifyId}/read`, function() {
            notificationItem.addClass('read');
            updateUnreadCount();
            $(this).remove();
        });
    });

    // 删除通知
    $(document).on('click', '.delete-btn', function() {
        if (!confirm('确定要删除这条通知吗？')) return;
        
        let notificationItem = $(this).closest('.notification-item');
        let notifyId = notificationItem.data('id');
        
        $.ajax({
            url: `/member/notify/${notifyId}`,
            method: 'DELETE',
            success: function() {
                notificationItem.fadeOut(function() {
                    $(this).remove();
                    updateUnreadCount();
                });
            }
        });
    });

    // 全部标记为已读
    $('#markAllReadBtn').click(function() {
        $.post('/member/notify/read-all', function() {
            $('.notification-item').addClass('read');
            $('.mark-read-btn').remove();
            updateUnreadCount();
        });
    });

    // 搜索通知
    let searchTimeout;
    $('#searchInput').on('input', function() {
        clearTimeout(searchTimeout);
        let keyword = $(this).val();
        
        searchTimeout = setTimeout(function() {
            if (keyword) {
                $.get(`/member/notify/search?keyword=${encodeURIComponent(keyword)}`, function(notifications) {
                    $('#notificationList').empty();
                    notifications.forEach(function(notify) {
                        let notificationHtml = createNotificationHtml(notify);
                        $('#notificationList').append(notificationHtml);
                    });
                });
            } else {
                loadNotifications('1');
            }
        }, 300);
    });

    // 类型筛选
    $('.btn-group button').click(function() {
        $('.btn-group button').removeClass('active');
        $(this).addClass('active');
        loadNotifications($(this).data('type'));
    });

    // 初始化：选中第一个按钮并加载相应通知
    $('.btn-group button:first').addClass('active');
    loadNotifications('1');  // 默认加载活动通知

    updateUnreadCount();
});
