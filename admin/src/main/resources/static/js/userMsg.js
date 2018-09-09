//表格行 悬停 变色
layui.use(['table', 'form', 'upload', 'element'], function () {
    var table = layui.table,
        form = layui.form,
        upload = layui.upload,
        element = layui.element,
        $ = layui.$;


    var btn = {
        getUser: function () {
            var user;
            $.ajaxSettings.async = false;
            $.post("/mall/getUser", function (back) {
                //layer.alert(JSON.stringify(back));
                user = back.data;
                $(".ip").html(back.msg);
            })
            return user;
        },
        /* 时间戳转化开始 */
        dateFormat: function (value) {
            var date = new Date(value);//如果date为13位不需要乘1000
            var Y = date.getFullYear() + '-';
            var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
            var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
            var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
            var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
            var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
            return Y + M + D + h + m + s;
        }
    }


    //初始化信息
    var user = btn.getUser();
    if (user.img != null || user.img.length > 0) {
        $(".img").attr("src", user.img);
        $("#mainDa").html("<img width='300' height='300' src=" + user.img + ">");
        $("#mainSm").html("<img width='200' height='200' src=" + user.img + ">");
    }
    $(".role").html(user.role.roleChar);
    $(".name").html(user.name);
    $(".usex").html(user.sex);
    $(".uemail").html(user.email);
    $(".utel").html(user.tel);
    $(".date").html(btn.dateFormat(user.createTime));


//    修改头像
    $(".updateImgBtn").click(function () {
        var Index = layer.open({
            type: 1
            , anim: 2
            , shadeClose: true //开启遮罩关闭
            , title: '更换头像'
            , area: ['600px', '500px']
            , content: $("#updateMainImg")
        })
        $("#updateImgMainBtn").off().on('click', function () {
            if ($("#imgMain").val().length == 0) {
                layer.alert("请更换图片后上传", {icon: 5});
            } else {
                var loding = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.5
                });
                var formdata = new FormData(document.getElementById("updateMainImgForm"));
                formdata.append("oldImgPath", user.img.substring(27, user.img.length));
                formdata.append("uId", user.uId);
                $.ajax({
                    url: 'mall/userImgUpload/',
                    type: 'POST',
                    data: formdata,
                    async: true,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (result) {
                        // if (result.msg == 'OK') {
                        //     layer.close(addmsg);//关掉当前 修改图片层
                        //     layer.close(loading);//关闭加载层
                        //     layer.alert("更换成功", {icon:1},function (index) {
                        //         var loding = layer.msg('数据加载中', {
                        //             icon: 16
                        //             , shade: 0.5
                        //         });
                        //         layer.close(index);
                        //         tabReload(loding);
                        //     });
                        // } else {
                        //     layer.alert("上传图片失败，请重新上传！", {icon: 5});
                        //     layer.close(loading);
                        // }

                        layer.alert("演示模式，不允许操作！", {icon: 2});
                    },
                    error: function (result) {
                        layer.msg("系统繁忙，稍后再试！");
                        layer.close(loding);
                    }
                });
            }
        })
    })

//    修改密码
    $(".updatePwd").click(function () {
        var Index = layer.open({
            type: 1
            , anim: 2
            , shadeClose: true //开启遮罩关闭
            , title: '更换头像'
            , area: ['600px', '400px']
            , content: $("#updatePWD")
        })
        $("#updatePasswordForm span").hide();
        var arr = [" *请输入旧密码", " *请输入新密码", " *密码再次确认"];
        $("#updatePasswordForm input").each(function (i) {
            this.onblur = function () {
                if ($(this).val().length == 0) {
                    $("#updatePasswordForm span:eq(" + i + ")").text(arr[i]).css("color", "red").show();
                } else {
                    $("#updatePasswordForm span:eq(" + i + ")").text("").css("color", "red").hide();
                }
            }
        })
        //点击提交
        $("#updatePWD .layui-btn").click(function () {
            var isOk = "";
            $("#updatePasswordForm input").each(function (i) {
                if ($(this).val().length == 0) {
                    isOk += ";";
                }
            })
            if (isOk.length > 0) {
                layer.msg("请把表单数据填写完！", {icon: 2, anim: 6});
            } else {
                var loding = layer.msg('正在验证...', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });
                var oldpwd = $("#updatePasswordForm input[name='oldP']").val();
                var npwd = $("#updatePasswordForm input[name='newsP']").val();
                var pwd = $("#updatePasswordForm input[name='password']").val();
                //作旧密码比对
                $.ajaxSettings.async = true;
                $.post("/mall/equalPwd", {pwd: oldpwd}, function (back) {
                    if (back.status != 200) {
                        layer.close(loding);
                        layer.alert("旧密码不正确!", {icon: 2})
                    } else {
                        if (npwd != pwd) {
                            layer.close(loding);
                            layer.alert("新密码与密码确认 不匹配！", {icon: 2})
                        } else {
                            layer.close(loding);
                            var key = layer.msg('正在修改...', {
                                icon: 16
                                , shade: 0.1
                                , time: 20000
                            });
                            $.ajaxSettings.async = true;
                            $.post("/mall/updateuserMsg", $("#updatePasswordForm").serialize(), function (back) {
                                // if (back.status == 200) {
                                //     layer.alert("已修改，请重新登录", {icon: 1}, function (index) {
                                //         layer.close(key);
                                //         if (window != top) {
                                //             top.location.href = "../../loginOut";
                                //         }
                                //     })
                                // } else {
                                //     layer.close(key);
                                //     layer.msg("系统繁忙，稍后再试");
                                // }
                                layer.alert("演示模式，不允许操作！", {icon: 2});
                            }).error(function (b) {
                                layer.msg("系统繁忙，稍后再试");
                                console.log(JSON.stringify(b));
                                layer.close(key);
                            })
                        }
                    }
                }).error(function (b) {
                    layer.msg("系统繁忙，稍后再试");
                    layer.close(loding)
                    console.log(JSON.stringify(b));
                })


            }
        })
    })

    //    信息修改
    $(".updateAdminBtn").click(function () {
        var Index = layer.open({
            type: 1
            , anim: 2
            , shadeClose: true //开启遮罩关闭
            , title: '修改信息'
            , area: ['650px', '400px']
            , content: $("#updateAdminMsgDiv")
        })
        $("#updateAdminForm input[name='tel']").val(user.tel);
        $("#updateAdminForm input[name='name']").val(user.name);
        $("#updateAdminForm input[name='email']").val(user.email);
        $("#updateAdminForm input[type='radio']").each(function () {
            if (user.sex==$(this).val()) {
                this.checked=true;
            }
        })
        form.render();

        $("#UpdateAdminBtn").click(function () {
            var tel=$("#updateAdminForm input[name='tel']").val();
            if (isPoneAvailable(tel)) {
                $.post("/mall/updateuserMsg",$("#updateAdminForm").serialize(),function (back) {
                    layer.alert("已修改",{icon:1},function (i) {
                        layer.close(i);
                        layer.close(Index);
                        btn.getUser();
                    });
                   // layer.alert("演示模式，不允许操作！",{icon:2});
                })
            }else{
                layer.msg("手机格式不正确！",{icon:2,anim:6})
            }
        }).error(function (b) {
            console.log(JSON.stringify(b));
            layer.msg("系统繁忙，请稍后再试！");

        })
    })
//验证手机号码格式
    function isPoneAvailable(value) {
        var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
        if (!myreg.test(value)) {
            return false;
        } else {
            return true;
        }
    }
});
