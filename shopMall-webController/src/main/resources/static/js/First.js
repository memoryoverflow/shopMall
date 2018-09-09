$(function () {

    var btn = {
        getUserMsg: function () {
            var uMsg = "";
            $.ajaxSettings.async = false;
            $.post("/user/isLogin", {}, function (data) {
                //alert(JSON.stringify(data))
                //debugger;
                if (data.status == "403") {
                    uMsg = "";
                } else {
                    uMsg = data.data;
                }
            })
            return uMsg;
        }
    }
    var user = btn.getUserMsg();
    if (user != '') {
        if (user.img != null || user.img.length > 0) {

            $(".top-header-right ul").css("margin-top", "-10px");
            $("#userHeadImgg").html("<img style='border-radius: 100px;border:1px solid white;position: relative;top: 10px' width='25' height='25' src=" + user.img + ">");
        }else{
            $("#userHeadImgg").html("");
        }
    } else {
        $(".top-header-right ul").css("margin-top", "5px");
    }


    //商品描述
    $.post("/seckill/getSeckillExplainMsg", {}, function (data) {
        //alert(JSON.stringify(data))
        if (data.status == "403") {
            if ((data.msg).indexOf("权限不足") >= 0) {
                location.href = "/mall/toNoAuthority";
            }
        } else {
            $("#slide1 img").attr("src", data.data.img1);
            $("#slide2 img").attr("src", data.data.img2);
            $("#slide3 img").attr("src", data.data.img3);

            $(".slider-detils").each(function (i) {
                $(".slider-detils:eq(" + i + ") h3 label:eq(1)").text(data.data.name);
                $(".slider-detils:eq(" + i + ") h3 label:eq(0)").text(data.data.explain1);
                $(".slider-detils:eq(" + i + ") span:eq(0)").text(data.data.explain2);
                $(".slider-detils:eq(" + i + ") span:eq(1)").text(data.data.explain3);
            })
        }

    }).error(function (data) {
        //alert(JSON.stringify(data))
        //alert("系统异常，图片加载失败！");
        console.log(JSON.stringify(data));
    })


    //获取秒杀抢购商品入口
    $.post("/seckill/getProductId", {}, function (back) {
        //alert(JSON.stringify(back));
        if (back.status == "403") {
            $(".slide-btn").each(function (i) {
                this.onclick = function () {
                    location.href = "/mall/toLogin";
                }
            })
        } else {
            if (back.data != null) {
                //获取秒杀id
                $(".slide-btn").attr("href", "http://106.14.226.138/seckill/" + back.data);
                $(".imgA").attr("href", "http://106.14.226.138/seckill/" + back.data);
            } else {
                $(".slide-btn").css("cursor", "pointer");
                $(".slide-btn").each(function (i) {
                    this.onclick = function () {
                        //判断是否登录
                        var userId = $("#userId").text().replace(/\s|\xA0/g, "");
                        if (userId.length > 0) {
                            alert("当前没有活动商品！");
                        } else {
                            location.href = "/mall/toLogin";
                        }
                    }
                })
            }
        }

    }).error(function (b) {
        /*alert(JSON.stringify(b))*/
        console.log(JSON.stringify(b));
    })
})