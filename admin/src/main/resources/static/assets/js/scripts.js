jQuery(document).ready(function () {

    var btn = {
        showImgShade: function () {
            $("#shade").show();
            $("#imgLoading").show();
        }
        , hideImgShade: function () {
            $("#shade").hide();
            $("#imgLoading").hide();
        }
    }



    $('.submit_button').click(function () {

        var username = $('.myform').find('.username').val();
        var password = $('.myform').find('.password').val();
        var Captcha = $('.myform').find('.Captcha').val();


        if (username == '') {
            $('.myform').find('.error').fadeOut('fast', function () {
                $('.error').css('top', '27px');
            });
            $('.myform').find('.error').fadeIn('fast', function () {
                $('.error').parent().find('.username').focus();
            });
            return false;
        }

        if (password == '') {
            $('.myform').find('.error').fadeOut('fast', function () {
                $('.error').css('top', '96px');
            });
            $('.myform').find('.error').fadeIn('fast', function () {
                $('.error').parent().find('.password').focus();
            });
            return false;
        }

        if (Captcha == '') {
            $('.myform').find('.error').fadeOut('fast', function () {
                $('.error').css('top', '166px');
            });
            $('.myform').find('.error').fadeIn('fast', function () {
                $('.error').parent().find('.Captcha').focus();
            });
            return false;
        }


        btn.showImgShade();

        layui.use(['table', 'layer'], function () {
            var table = layui.table;
            var layer = layui.layer;

            //ajax登录
            $.ajaxSettings.async = true;
            $.post("/login.do", $(".myform").serialize(), function (back) {
                btn.hideImgShade();
                layer.alert(back.msg,{icon:2});
                location.href="/index";
            }).error(function (back) {
                layer.alert("登录失败！");
                console.log(JSON.stringify(back))
            })
        })

    });

    $('.page-container form .username, .page-container form .password').keyup(function () {
        $(this).parent().find('.error').fadeOut('fast');
    });

})
;
