$(function () {
//商品进入购物车
    $("#login").click(function () {

        $("#shade").show();
        $("#imgLoading").show();

        function hide() {
            $("#shade").hide();
            $("#imgLoading").hide();
        }

        $.post("/user/login.do", $("#loginForm").serialize(), function (data) {

            if (data.msg == "OK") {
                location.href = "../";
            } else {
                hide();
                $("#titMsg").text(data.msg);
            }
        }).error(function (data) {
            hide();
            alert("系统繁忙，请稍后再试！");
        })
    })
})

