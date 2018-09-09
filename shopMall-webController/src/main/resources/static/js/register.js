//给input 框绑定失焦事件

$(function () {

    $("#registerForm input").each(function (i) {
        this.onblur = function () {
            var arr = checkInputIsNull(i);//校验当前input是否为空和啊报表单是否还有空的input
            var val = $(this).val();//获取当前表单的值
            var msg = "";//接收校验结果的
            var isArr = [];
            var isOnlyOne = 0;//判断校验结果是否全正确
            var isPwd = 0;
            //alert(i);

            //当前input 为空的提示消息
            var array = ["用户名", "联系电话", "邮箱地址", "收货地址", "登录密码", "*两次密码不匹配"];
            if (arr[1] == -1) {
                var txt = "*请填写";
                if (i == 7) {
                    $(".iStr:eq(" + (i - 2) + ")").text(array[i - 2]);
                } else if (i == 6) {
                    $(".iStr:eq(" + (i - 2) + ")").text(txt + array[i - 2]);
                } else {
                    $(".iStr:eq(" + i + ")").text(txt + array[i]);
                }
            } else {
                //比较两次密码是否相等
                if (i == 7) {
                    if ($(this).val() != $("#registerForm input:eq(6)").val()) {
                        $(".iStr:eq(" + (i - 2) + ")").text(array[i - 2]);
                        isPwd++;
                    } else {
                        $(".iStr:eq(" + (i - 2) + ")").text("");
                    }
                }
                if (i == 6) {
                    $(".iStr:eq(" + (i - 2) + ")").text("");
                }
                $(".iStr:eq(" + i + ")").text("");


                //检测唯一性
                if (i == 0) {
                    isArr = checkDataIsOnlyOne(val, "/user/nameIsOnly.do");
                    if (isArr[0] == "ok") {//用户名可用
                        $(".iStr:eq(" + i + ")").text("");
                    } else {//用户名被注册
                        $(".iStr:eq(" + i + ")").text("*用户名已被注册");
                    }
                } else if (i == 1) {
                    //检验电话
                    var flag = isPoneAvailable(val); //电话格式是否正确
                    if (flag) {
                        isArr = checkDataIsOnlyOne(val, "/user/phoneIsOnly.do");//与数据库比对是否唯一
                        if (isArr[0] == "ok") {
                            //手机名可用
                            $(".iStr:eq(" + i + ")").text("");
                        } else {
                            //手机号
                            $(".iStr:eq(" + i + ")").text("*手机号码已被注册");
                        }
                    } else {
                        $(".iStr:eq(" + i + ")").text("*请输入正确的手机号");
                    }
                } else if (i == 2) {
                    //校验邮箱
                    var flag = isEmailAvailable(val);//格式是否正确
                    if (flag) {
                        isArr = checkDataIsOnlyOne(val, "/user/emailIsOnly.do");//与数据库比对是否唯一
                        if (isArr[0] == "ok") {
                            //用户名可用
                            $(".iStr:eq(" + i + ")").text("");
                        } else {
                            //邮箱被注册
                            $(".iStr:eq(" + i + ")").text("*邮箱已被注册");
                        }
                    } else {
                        $(".iStr:eq(" + i + ")").text("*邮箱格式不正确");
                    }
                }
            }
            //如果表单的所有信息填写完整并且正确 即可注册

            //alert(arr[0].length+" "+checkIsAllTrue().length)
            if (arr[0].length == 0 && checkIsAllTrue().length == 0) {
                $("#submit").attr("disabled", false);
                $("#submit").removeClass("aa");
            } else {
                $("#submit").attr("disabled", true);
                $("#submit").addClass("aa");
            }
        }
    })

    //表单提交
    $("#submit").off().on('click', function () {
        $("#shade").show();
        $("#imgLoading").show();
        register();
    })

    function register() {
        var isOk = 0;
        $.ajaxSettings.async = true;
        $.post("/user/register.do", $("#registerForm").serialize(), function (back) {
            //alert(JSON.stringify(back));
            if (back.msg == "邮件发送失败") {
                $("#shade").hide();
                $("#imgLoading").hide();
                alert("注册成功,激活账户邮件发送失败！请联系管理员（1375668614@qq.com），否则无法登陆。");
            }
            if (back.msg == "注册成功") {
                alert("注册成功,收到邮件请前去邮箱激活");
                location.href = "../../mall/toLogin";
            }
        }).error(function (back) {
            $("#shade").hide();
            $("#imgLoading").hide();
            alert("系统繁忙，稍后再试");
            isOk = -1;
        })
        //return isOk;
    }

})


function isEmailAvailable(str) {
    //对电子邮件的验证
    var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
    if (!myreg.test(str)) {
        return false;
    } else {
        return true;
    }
}

//验证手机号码
function isPoneAvailable(str) {
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(str)) {
        return false;
    } else {
        return true;
    }

}

//校验表单所有的数据是否正确
function checkIsAllTrue() {
    var str = "";
    $(".iStr").each(function (i) {
        if ($(this).text().length != 0) {
            str += ".";
        }
    })
    return str;
}

//校验表单是否有空
function checkInputIsNull(i) {
    var arr = [];
    var str = "";
    var m = 0;
    $("#registerForm input").each(function (n) {
        if ($(this).val().length == 0) {
            str += ".";
        }
    })
    if ($("#registerForm input:eq(" + i + ")").val().length == 0) {
        m = -1;
    } else {
        m = 1;
    }
    arr.push(str, m);
    return arr;
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