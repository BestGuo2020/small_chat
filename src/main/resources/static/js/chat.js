var ws = new ReconnectingWebSocket("ws://" + location.hostname + ":" + location.port + "/index/mainPoint");
// 接收到服务端发送的消息
ws.onmessage = function (e) {
    // console.log(e);
    // 得到 JSON 数据
    var data = JSON.parse(e.data);
    // 判断是否正常
    if(data.status === 1) {
        // 加载好友信息
        var friends = data.info;
        // 加载用户信息
        if(data.websocketStatus === 5) {
            // 将用户信息保存到 session 中
            sessionStorage.setItem("userInfo", JSON.stringify(data.info));
            // 保存完成之后，再次加载最近聊天记录
            loadRecent();
        } else if (data.websocketStatus === 10) {
            // 循环遍历
            (function (arr) {
                var friendListContent = $("#friends > .chat-list-content > .chat-list");
                var friendsHtml = "";
                // 获取
                for (var i = 0; i < arr.length; i++) {
                    // 在线状态判断
                    var onlineStatus = `<span class="online-status" title="在线"></span>`;
                    if (!arr[i].isOnline) {
                        onlineStatus = `<span class="online-status offline" title="离线"></span>`
                    }
                    // 没有头像的时候
                    var head = headExist(arr[i].head, arr[i].username, "big");
                    head += onlineStatus;
                    // 好友项拼接
                    var friendHtml =
                        `<li class="chat-list-item" onclick="selectFriend(this)" title="` + arr[i].userId + `">
                                <figure class="avatar user-online">` + head + `</figure>
                                <div class="list-body">
                                    <div class="chat-bttn">
                                        <h3 class="mb-1 mt-1">` + arr[i].username + `</h3>
                                        <p class="text-info">id: ` + arr[i].userId + `</p>
                                    </div>
                                    <div class="list-action mt-2 text-right">
                                        <a href="chat.html#" class="btn-plus dropdown-toggle" data-toggle="dropdown"><i class="ti-plus"></i></a>
                                        <div class="dropdown-menu dropdown-menu-right">
                                            <a href="chat.html#" class="dropdown-item text-danger">删除好友</a>
                                        </div>
                                    </div>
                                </div>
                            </li>`;
                    friendsHtml += friendHtml;
                }
                // 好友 html
                friendListContent.html(friendsHtml);
            })(friends);
        } else if (data.websocketStatus === 11) {
            // 获取
            var status = $(".online-status");
            // 循环遍历
            (function (arr, status) {
                for (var i = 0; i < arr.length; i++) {
                    // 在线状态判断
                    if (!arr[i].isOnline) {
                        status[i].classList.add("offline");
                        status[i].setAttribute("title", "离线");
                    } else if(arr[i].isOnline) {
                        status[i].classList.remove("offline");
                        status[i].setAttribute("title", "在线");
                    }
                }
            })(friends, status);

        } else if (data.websocketStatus === 20) {
            // 读取聊天信息
            console.log(data.info);
            $("#no-select").css("display", "none");
            $("#selected-friend").css("display", "flex");
            // 获取在线状态
            var online = `<small class="text-success">在线</small>`
            if(data.info.isOnline === 0) {
                online = `<small>离线</small>`
            }
            // 没有头像的时候
            var head = headExist(data.info.head, data.info.username, "big");
            // 加载头像和昵称
            $("#content-header").html(`<figure class="avatar">` + head + `</figure>
					<div>
						<h5 class="mt-2 mb-0">` + data.info.username + `</h5>
						` + online + `
					</div>`);
            // 将当前聊天的好友信息放入到 session 中
            sessionStorage.setItem("friend", JSON.stringify(data.info));
        } else if (data.websocketStatus === 21) {
            // 收到聊天消息
            console.log("收到哪个用户：", data.info.username);
            console.log("消息：", data.msg);
            // 播放音乐
            $("#notification")[0].play();
            // 获取当前的和哪位好友的会话状态
            var parse = JSON.parse(sessionStorage.getItem("friend"));
            var temp1 = $(".chat-content")[1];
            // 如果会话存储中有好友记录
            if(parse !== null) {
                // 如果收到的用户不是当前聊天的用户，或者移动端不处于聊天界面当中，则显示通知信息
                if(temp1.className.indexOf("mobile-active") === -1 || parse.userId !== data.info.userId) {
                    // 聊天通知
                    $.toast({
                        type: 'info',
                        title: data.info.username,
                        subtitle: '发了一条消息给你',
                        content: data.msg,
                        delay: 5000,
                        img: {
                            src: '/static/images/ring.png',
                            class: 'rounded-0', /**  Classes you want to apply separated my a space to modify the image **/
                            alt: 'Image'
                        }
                    });
                } else {
                    // 否则就将消息展示到当前的聊天界面中
                    renderChat(data.time, data.info.username, data.info.head, data.msg, "left", true);
                }
            } else {
                // 聊天通知
                $.toast({
                    type: 'info',
                    title: data.info.username,
                    subtitle: '发了一条消息给你',
                    content: data.msg,
                    delay: 5000,
                    img: {
                        src: '/static/images/ring.png',
                        class: 'rounded-0', /**  Classes you want to apply separated my a space to modify the image **/
                        alt: 'Image'
                    }
                });
            }
            setChatHeight();
            // 保存消息
            var item = JSON.parse(sessionStorage.getItem("userInfo"));
            saveChat(item.userId, data.info.userId, false, data.time, data.info.username, data.info.head, data.msg);
            saveNewMessage(item.userId, data.info.userId, data.time, data.info.head, data.info.username, data.msg);
            // 重新加载最近聊天
            loadRecent();
        } else if (data.websocketStatus === 30) {
            var html = "";
            for(var i = 0; i < data.info.length; i++) {
                var head3 = headExist(data.info[i].head, data.info[i].username, "big");
                html += `<div class="owl-item" data-user-id="` + data.info[i].userId + `" data-username="` + data.info[i].username + `" style="width: 72.333px;" onclick="confirmFriend(this)">
                                <div class="item">
                                    <div class="recent-chat float-left">
                                        <div class="user">` + head3 + `</div>
                                        <h3 class="mb-1">` + data.info[i].username + `</h3>
                                        <span>` + data.info[i].userId + `</span>
                                    </div>
                                </div>
                            </div>`;
            }
            $(".owl-stage").html(html);
        }
    }
};
// 错误
ws.onerror = function (e) {
    console.log(e);
    swal({
        title: "错误",
        text: "与服务器的连接发生了异常，请刷新重试",
        icon: "error"
    })
};
// 登出
function signout() {
    swal({
        title: "提示",
        text: "确定要注销吗？",
        buttons: {
            yes: {
                text: "确定",
                value: true,
            },
            no: {
                text: "取消",
                value: false,
            }

        }
    }).then(function (yes) {
        if(yes) {
            sessionStorage.removeItem("friend");
            ws.close(3001);
            location.href = "/signout";
        }
    })
}
// 选择一位好友，开始聊天
function selectFriend(e, recent = false) {
    console.log(e.title);
    // 清空消息内容
    $(".messages-content").html("");
    var userId = parseInt(e.title);
    ws.send(JSON.stringify({sendType: 20, userId: userId}));
    // 移动端就打开聊天界面
    $(".chat-content")[1].classList.add("mobile-active");
    // 加载本地缓存的聊天信息
    var item = JSON.parse(sessionStorage.getItem("userInfo"));
    var chatInfo = JSON.parse(localStorage.getItem(item.userId));
    // 如果有，就加载
    if(chatInfo !== null) {
        // 通过好友id获取本地聊天记录
        var chatInfoElement = chatInfo[userId];
        // 如果有聊天记录
        if(chatInfoElement !== undefined) {
            var message = chatInfoElement.message;
            // 循环加载聊天信息
            for(var i = 0; i < message.length; i++) {
                if(message[i].left !== null && message[i].left !== undefined) {
                    (function () {
                        var time = message[i].left.time;
                        var name = message[i].left.name;
                        var head = message[i].left.head;
                        var msg = message[i].left.message;
                        renderChat(time, name, head, msg, "left");
                    })(message, i);
                } else if(message[i].right !== null && message[i].right !== undefined) {
                    (function () {
                        var time = message[i].right.time;
                        var name = message[i].right.name;
                        var head = message[i].right.head;
                        var msg = message[i].right.message;
                        renderChat(time, name, head, msg);
                    })(message, i);
                }
            }
            // 如果有新消息，则设置为0
            if(chatInfoElement.new_message !== undefined) {
                chatInfoElement.new_message.count = 0;
                localStorage.setItem(item.userId, JSON.stringify(chatInfo));
                loadRecent();
            }
        }
    }
    // 重新设定高度
    setChatHeight();
}

// 发送消息给好友
function sendMessage() {
    // 用户id，头像状态
    var user = JSON.parse(sessionStorage.getItem("userInfo"));
    var userHead = user.head;
    // 好友id，消息，发送时间
    var friend = JSON.parse(sessionStorage.getItem("friend"));
    var friendId = friend.userId;
    var message = $("#send-text").val();
    // 发送时间
    var time = $.format.date(new Date(), "MM-dd HH:mm:ss");
    // 发送数据
    var sendData = JSON.stringify({message: message, userId: friendId, sendType: 21, time: time});
    if(message === '') {
        $("#send-text").attr("placeholder", "不能发空消息");
    } else {
        // 判断是否有头像
        var head = headExist(userHead, user.username);
        // 用户界面展示的消息
        renderChat(time, user.username, userHead, message, "right", true);
        // 发送给用户
        try {
            ws.send(sendData);
            // 保存消息
            var item = JSON.parse(sessionStorage.getItem("userInfo"));
            saveChat(item.userId, friendId, true, time, user.username, userHead, message);
            saveNewMessage(item.userId, friendId, time, friend.head, friend.username, message, true);
            setChatHeight();
            loadRecent();
        } catch (e) {}
        // 清空输入栏
        $("#send-text").val("");
    }
}

// 设置高度
function setChatHeight() {
    $(".chat-body")[0].scrollTop = $(".chat-body")[0].scrollHeight;
}

/**
 * 保存聊天记录
 *
 * @param userId 用户id
 * @param friendId 好友id
 * @param sender 是否为发送者
 * @param time 发送时间
 * @param name 用户名
 * @param head 头像
 * @param message 消息
 */
function saveChat(userId, friendId, sender, time, name, head, message) {
    // 从本地存储获取当前用户的聊天记录
    var chatInfoObjs = localStorage.getItem(userId);
    // 判断是否有聊天记录
    if(chatInfoObjs !== null) {
        // 有聊天记录，从本地记录中取出，然后再添加
        (function (userId, friendId, sender, time, name, head, message) {
            // 获取当前用户的聊天记录
            var chatInfoObj = JSON.parse(chatInfoObjs);
            // 获取当前好友的聊天记录
            var message1 = chatInfoObj[friendId + ""];
            if(message1 === undefined) {
                chatInfoObj[friendId + ""] = {};
                chatInfoObj[friendId + ""].message = [];
            }
            message1 = chatInfoObj[friendId + ""].message;
            // 判断是发送端还是接收端
            if(sender){
                message1.push({right: {time: time, name: name, head: head, message: message}});
            } else {
                message1.push({left: {time: time, name: name, head: head, message: message}});
            }
            localStorage.setItem(userId, JSON.stringify(chatInfoObj));
        })(userId, friendId, sender, time, name, head, message);
    } else {
        (function (userId, friendId, sender, time, name, head, message) {
            // 没有聊天记录，将聊天记录添加进去，创建当前好友记录的聊天对象
            var chatInfoObj = {};
            // 设置相关信息
            chatInfoObj[friendId + ""] = {};
            chatInfoObj[friendId + ""].message = [];
            // 判断是发送端还是接收端
            if(sender){
                chatInfoObj[friendId.toString()].message.push({right: {time: time, name: name, head: head, message: message}});
            } else {
                chatInfoObj[friendId.toString()].message.push({left: {time: time, name: name, head: head, message: message}});
            }
            // 放到本地存储中
            localStorage.setItem(userId, JSON.stringify(chatInfoObj));
        })(userId, friendId, sender, time, name, head, message);
    }
}

/**
 * 显示新消息
 *
 * @param userId 登录的用户 id
 * @param friendId 好友的 id
 * @param time 受到时间
 * @param head 头像
 * @param name 用户名
 * @param message 消息
 * @param sender 是否为发送者
 */
function saveNewMessage(userId, friendId, time, head, name, message, sender = false) {
    // 从本地存储获取当前用户的聊天记录
    var chatInfoObjs = localStorage.getItem(userId);
    // 判断是否有聊天记录
    if(chatInfoObjs !== null) {
        // 有聊天记录，从本地记录中取出，然后再添加
        (function (friendId, time, head, name, message) {
            // 获取当前用户的聊天记录
            var chatInfoObj = JSON.parse(chatInfoObjs);
            // 获取和当前好友的最新聊天记录
            var new_message = chatInfoObj[friendId + ""];
            if(new_message === undefined) {
                chatInfoObj[friendId + ""] = {};
                chatInfoObj[friendId + ""].new_message = {};
                chatInfoObj[friendId + ""].new_message.count = 0;
            } else if (new_message.new_message === undefined) {
                chatInfoObj[friendId + ""].new_message = {};
                chatInfoObj[friendId + ""].new_message.count = 0;
            }
            new_message = chatInfoObj[friendId + ""].new_message;
            var parse = JSON.parse(sessionStorage.getItem("friend"));
            // 如果不是发送者，则增加消息数量
            if(!sender && parse === null) {
                new_message.count++;
            } else if (!sender && parse !== null && parse.userId !== friendId) {
                new_message.count++;
            }
            // 头像
            new_message.head = head;
            // 名字
            new_message.name = name;
            // 时间
            new_message.time = time;
            // 消息
            new_message.message = message;
            localStorage.setItem(userId, JSON.stringify(chatInfoObj));
        })(friendId, time, head, name, message);
    } else {
        (function (friendId, time, head, name, message, sender) {
            // 没有聊天记录，将聊天记录添加进去，创建当前好友记录的聊天对象
            var chatInfoObj = {};
            chatInfoObj[friendId + ""].new_message = {};
            chatInfoObj[friendId + ""].new_message.count = 0;
            // 消息数量
            var parse = JSON.parse(sessionStorage.getItem("friend"));
            if(!sender && parse === null) {
                chatInfoObj[friendId + ""].new_message.count++;
                // 头像
                chatInfoObj[friendId + ""].new_message.head = head;
                // 名字
                chatInfoObj[friendId + ""].new_message.name = name;
            } else if (!sender && parse !== null && parse.userId !== friendId) {
                chatInfoObj[friendId + ""].new_message.count++;
                // 头像
                chatInfoObj[friendId + ""].new_message.head = head;
                // 名字
                chatInfoObj[friendId + ""].new_message.name = name;
            }
            // 时间
            chatInfoObj[friendId + ""].new_message.time = time;
            // 消息
            chatInfoObj[friendId + ""].new_message.message = message;
            // 放到本地存储中
            localStorage.setItem(userId, JSON.stringify(chatInfoObj));
        })(friendId, time, head, name, message, sender);
    }
}

// 加载最近的聊天信息
function loadRecent() {
    // 在最近聊天板块中显示
    var htmlStr = '';
    var userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
    var userMessages = JSON.parse(localStorage.getItem(userInfo.userId));
    // 遍历对象
    for(var key in userMessages) {
        if(userMessages[key] !== undefined) {
            // 每一条消息
            var new_message = userMessages[key].new_message;
            if(new_message !== undefined) {
                // 判断是否有头像
                var head = headExist(new_message.head, new_message.name, "big");
                var actionHtml = `<div class="message-count bg-primary">` + new_message.count + `</div>
						<small class="text-primary">` + new_message.time + `</small>
						`;
                // 判断消息数是否为0
                if(new_message.count === 0) {
                    actionHtml = `<small>` + new_message.time + `</small>`;
                }
                htmlStr = htmlStr.concat(`<li class="chat-list-item" title="` + key + `" onclick="selectFriend(this, true)">
						  <figure class="avatar user-online">
							 ` + head + `
						  </figure>
						  <div class="list-body">
								  <div class="chat-bttn">
								  <h3 class="mb-0 mt-2">` + new_message.name + `</h3>
								  <p>` + new_message.message + `</p>
							  </div>
							  <div class="list-action mt-2 text-right">
								  ` + actionHtml + `
							  </div>
						  </div>
					</li>`);
            }
        }
    }
    $("#recent-chat").html(htmlStr);
}

/**
 * 渲染聊天数据
 *
 * @param time 时间
 * @param name 用户名
 * @param head 头像
 * @param message 消息
 * @param side 哪一边
 * @param loadAnimate 加载动画特效
 */
function renderChat(time, name, head, message, side = "right", loadAnimate = false) {
    // 判断是否有头像
    var head2 = headExist(head, name);
    // 用户界面展示的消息
    var animationRight = "", animationLeft = "";
    if(loadAnimate){
        animationRight = "animate__animated animate__fadeInRight";
        animationLeft = "animate__animated animate__fadeInLeft";
    }
    var messageHtml = '';
    if(side === "left") {
        messageHtml = `<div class="message-item ` + animationLeft + `">
                                    <div class="message-user">
                                        <figure class="avatar">` + head2 + `</figure>
                                        <div>
                                            <h5>` + name + `</h5>
                                            <div class="time">` + time + `<i class="text-info"></i></div>
                                        </div>
                                    </div>
                                    <div class="message-wrap">` + message + `</div>
                                </div>`;
    } else if (side === "right") {
        messageHtml = `<div class="message-item outgoing-message  ` + animationRight + `">
                                    <div class="message-user">
                                        <figure class="avatar">` + head2 + `</figure>
                                        <div>
                                            <h5>` + name + `</h5>
                                            <div class="time">` + time + `<i class="text-info"></i></div>
                                        </div>
                                    </div>
                                    <div class="message-wrap">` + message + `</div>
                                </div>`;
    }
    // 添加到界面去
    $(".messages-content").append(messageHtml);
}

/**
 * 头像是否存在
 *
 * @param head 头像
 * @param name 名字
 * @param size 头像大小
 */
function headExist(head, name, size = "small") {
    var headSize = "";
    if("small" === size) {
        headSize = "non-head-small"
    }
    if(head === null || head === undefined) {
        return `<span class="avatar-title bg-primary rounded-circle non-head ` + headSize + `">` + name.charAt(0).toUpperCase() + `</span>`;
    } else {
        return `<img src="/static/images/user-7.png" class="rounded-circle" alt="image">`;
    }
}