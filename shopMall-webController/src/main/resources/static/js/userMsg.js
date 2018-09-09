$(function () {
    var btn = {

        getUserMsg: function () {
            var uMsg = "";
            $.ajaxSettings.async = false;
            $.post("/user/isLogin", {}, function (back) {
                if (back.status == 200) {
                    uMsg = back.data;
                } else if (back.status == "403") {
                    layer.alert(back.msg, {icon: 4}, function (index) {
                        location.href = "../../mall/toLogin";
                    })
                }
            })
            return uMsg;
        }
        , setUserImg: function (img) {
            $("#userHeadImgg").html("<img style='border-radius: 100px;border:1px solid white;position: relative;top:3px' width='25' height='25' src=" + img + ">");
            $("#img1").html("<img width='150' height='150' src=" + img + ">");
            $("#mainDa").html("<img width='250' height='250' src=" + img + ">");
            $("#mainSm").html("<img width='200' height='200' src=" + img + ">");
            $("#mainSsm").html("<img  style='border-radius: 100px' width='150' height='150' src=" + img + ">");
        }
    }

    var user = btn.getUserMsg();
    if (user.img != null || user.img.length > 0) {
        btn.setUserImg(user.img);
    }


    var tel = $("#userTel").val();
    var str = "";
    var isOk = "";
    $("#userTel").blur(function () {
        str = checkInputIsNull();//判断表单是否为空
        isOk = "";
        var isArr = [];
        if (tel != $(this).val()) {
            var flag = isPoneAvailable($(this).val());
            if (flag) {
                isArr = checkDataIsOnlyOne($(this).val(), "/user/phoneIsOnly.do");
                if (isArr[0] == "ok") {
                    //手机名可用
                    $("#teltxt").text("");
                } else {
                    //手机号
                    isOk = "false";
                    $("#teltxt").text("*手机号码已被注册");
                }
            } else {
                isOk = "false";
                $("#teltxt").text("*请输入正确的手机号");
            }
        }
    })
    $("#userSubmit").click(function () {
        layui.use(['layer'], function () {
            var layer = layui.layer;
            if (str.length == 0 && isOk.length == 0) {
                $.post("/user/updateuserMsg", $("#userForm").serialize(), function (back) {
                    if (back.status == 200) {
                        layer.msg("已保存");

                        $(".sex").each(function () {
                            var num = 0;
                            $(".sex").each(function (i) {
                                if ($(this).val() == back.data.sex) {
                                    num = i;
                                }
                            })
                            $(".sex:eq(" + num + ")").attr("checked", "checked");
                        })
                    } else if (back.status == "403") {
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                }).error(function (back) {
                    layer.msg("保存失败");
                })

            } else {
                layer.msg("手机号不正确");
            }
        })
    })

    //头像图片上传
    $("#updateImgMainBtn").off().on('click', function () {

        layui.use(['layer'], function () {
            var layer = layui.layer;
            if ($("#imgMain").val().length == 0) {
                layer.alert("请选择图片后上传", {icon: 5});
            } else {
                var loding = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });

                var formdata = new FormData(document.getElementById("updateMainImgForm"));
                formdata.append("oldImgPath", (user.img).substring(27, user.img.length));
                formdata.append("uId", user.uId);
                $.ajax({
                    url: '/user/userImgUpload',//
                    type: 'POST',
                    data: formdata,
                    async: true,
                    cache: false,
                    contentType: false,
                    processData: false,
                    success: function (result) {
                        //alert(JSON.stringify(result))
                        if (result.msg == 'OK') {
                            layer.close(loding);//关闭加载层
                            layer.alert("更换成功", function (index) {
                                // var loding = layer.msg('数据加载中', {
                                //     icon: 16
                                //     , shade: 0.2
                                // });
                                layer.close(index)
                            });
                        } else {
                            layer.alert("上传图片失败，请重新上传！", {icon: 5});
                            layer.close(loding);
                        }
                    },
                    error: function (result) {
                        layer.msg("系统繁忙，稍后再试！");
                        layer.close(loding);
                    }
                });
            }
        })
    })


    //密码修改
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
    $("#updatePasswordForm .layui-btn").click(function () {
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
            $.post("/user/equalPwd", {pwd: oldpwd}, function (back) {
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
                        $.post("/user/updateuserMsg", $("#updatePasswordForm").serialize(), function (back) {
                            if (back.status == 200) {
                                layer.alert("已修改，请重新登录",{icon:1}, function (index) {
                                    layer.close(key);
                                    location.href = "../../mall/toLogin";
                                })
                            } else {
                                layer.close(key);
                                layer.msg("系统繁忙，稍后再试");
                            }
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


//验证手机号码
function isPoneAvailable(str) {
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(str)) {
        return false;
    } else {
        return true;
    }

}

//校验表单是否有空
function checkInputIsNull(i) {
    var str = "";
    $("#userForm input").each(function (n) {
        if ($(this).val().length == 0) {
            str += ".";
        }
    })
    return str;
}

//校验数据是否唯一
function checkDataIsOnlyOne(obj, url) {
    var msg = '';
    var isOk = 0;
    var isArr = [];
    //设为同步请求
    $.ajaxSettings.async = false;
    $.post(url, {msg: obj}, function (back) {
        if (back != "ok") {
            isOk = -1;
        }
        msg = back;
    });

    isArr.push(msg, isOk);
    return isArr;
}





















