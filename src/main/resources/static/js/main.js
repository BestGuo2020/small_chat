$(document).ready(function () {
    "use strict";
    $('#pagetour').modal();
    $('#pagetour').on('hidden.bs.modal', function (e) {
        $('#videocall').fadeOut(0);
        $('#videocall,.modal-backdrop,.model-bg').fadeOut();
        return !1
    })
    nicescroll();
    $('.nav-content-bttn').on('click', function () {
        $('.nav-content ul li a').removeClass('active');
        $(this).addClass('active');
        var data_tab = $(this).attr('data-tab');
        $('.sidebar').removeClass('active');
        $('#' + data_tab).addClass('active');
        return !1
    });
    // 夜间模式
    $('.night-mode').on('click', function () {
        $('body').toggleClass('dark');
        $(".friendly-hint").toggleClass('friendly-hint-dark');
        $("#send-text").toggleClass('send-text-dark');
        return !1
    });
    $('.profile-detail-bttn').on('click', function () {
        $('.profile-content').addClass('active');
        return !1
    });
    $('.close-icon').on('click', function () {
        $('.profile-content').removeClass('active');
        return !1
    });
    /*$('.chat-list-item .avatar,.chat-list-item .chat-bttn').on('click', function () {
        $('.chat-content').addClass('mobile-active');
        return !1
    });*/
    $('.back-chat-div').on('click', function () {
        $('.chat-content').removeClass('mobile-active');
        sessionStorage.removeItem("friend");
        return !1
    });
    $('.slider-0').owlCarousel({
        loop: !1,
        margin: 0,
        nav: !1,
        autoplay: !0,
        dots: !1,
        responsive: {0: {items: 6}, 600: {items: 6}, 1000: {items: 6}, 1400: {items: 3}}
    })
    var owlslide_1 = $('.slider-1')
    owlslide_1.owlCarousel({
        loop: !1,
        margin: 0,
        nav: !1,
        autoplay: !1,
        dots: !0,
        responsive: {0: {items: 1}, 600: {items: 1}, 1000: {items: 1}}
    })
    $('.start-tour,.next-tour').on('click', function () {
        owlslide_1.trigger('next.owl.carousel')
    });
    $('.tour-close-btn').on('click', function () {
        $('#pagetour').removeClass('show').fadeOut();
        $('.modal-backdrop,.model-bg').fadeOut(500);
        return !1
    });
    $('#videocall-bttn').on('click', function () {
        $('#videocall,.modal-backdrop,.model-bg').fadeIn();
        return !1
    });
    $('#video-close').on('click', function () {
        $('#videocall').fadeOut(0);
        $('#videocall,.modal-backdrop,.model-bg').fadeOut();
        return !1
    });
    $('#addfriend-bttn').on('click', function () {
        $('#addfriend,.modal-backdrop,.model-bg').fadeIn();
        return !1
    });
    $('#addfriend-close').on('click', function () {
        $('#addfriend').fadeOut(0);
        $('#addfriend,.modal-backdrop,.model-bg').fadeOut();
        return !1
    });
    $('#pagetour').addClass('show').fadeIn();
    console.log("欢迎运行简易 web 聊天室。\n项目地址：https://github.com/BestGuo2020/small_chat\n作者个人主页：https://www.bestguo.top\n原始项目版权归作者所有！！");
});



function nicescroll() {
    $(".chat-body,.chat-list-content,.scroll-profile").niceScroll({
        cursorcolor: "#eee",
        cursoropacitymin: 0,
        cursoropacitymax: 1,
        cursorwidth: "3px",
        cursorborder: "0px solid #fff",
        cursorborderradius: "5px",
        zindex: "auto",
        scrollspeed: 60,
        mousescrollstep: 40,
        touchbehavior: !1,
        hwacceleration: !0,
        boxzoom: !1,
        dblclickzoom: !0,
        gesturezoom: !0,
        grabcursorenabled: !0,
        autohidemode: !0,
        background: "",
        iframeautoresize: !0,
        cursorminheight: 32,
        preservenativescrolling: !0,
        railoffset: !1,
        bouncescroll: !1,
        spacebarenabled: !0,
        disableoutline: !0,
        horizrailenabled: !0,
        railalign: "right",
        railvalign: "bottom",
        enabletranslate3d: !0,
        enablemousewheel: !0,
        enablekeyboard: !0,
        smoothscroll: !0,
        sensitiverail: !0,
        enablemouselockapi: !0,
        cursorfixedheight: !1,
        hidecursordelay: 400,
        irectionlockdeadzone: 6,
        nativeparentscrolling: !0,
        enablescrollonselection: !0,
        cursordragspeed: 0.3,
        rtlmode: "auto",
        cursordragontouch: !1,
        oneaxismousemode: "auto",
        scriptpath: "",
        preventmultitouchscrolling: !0,
        disablemutationobserver: !1,
    })
}