//秒杀倒计时
function countDown(productId, nowTime, startTime, endTime) {
    var seckillBox = $("#seckill_box");
    var seckillStatus = $("#seckillStatus");
    //秒杀时间未到 倒计时
    if (nowTime > endTime) {
        seckillBox.html("<lable class='label label-danger'>抢购已结束！</lable>");
    } else if (nowTime < startTime) {
        var killTime = new Date(startTime + 1000);
        seckillStatus.countdown(killTime, function (event) {
            var format = event.strftime("%D天 %H时 %M分 %S秒");
            seckillStatus.html(format);
            seckillBox.html("抢购未开始");

        }).on("finish.countdown", function () {
            seckillBox.text("抢购进行中");
            isKilling(productId, seckillStatus);
        });
    } else {
        seckillBox.html("抢购进行中");
        isKilling(productId, seckillStatus);
    }
}

//执行秒杀
function isKilling(productId, node) {
    node.html("<input type='button' id='killingBtn' class='layui-btn  layui-btn-danger' value='立即抢购'/>").hide();
    //先判断 秒杀 是否 开启
    $.post(isStart(productId), {}, function (data) {
        //alert(JSON.stringify(data))
        if (data.status == 1) {//1 代表秒杀开启
            /* node.show();
             //秒杀按钮绑定事件
             $("#killingBtn").click(function () {
                 //只能点击一次秒杀
                 $(this).attr("disabled",true);
                 layui.use(['layer'], function () {
                     var layer = layui.layer;

                     //loading层
                     var loding = layer.load(0, {
                         shade: [0.1,'#fff'] //0.1透明度的白色背景
                     });

                     //执行秒杀，发送请求
                     $.post("/seckill/seckilling", $("#orderForm").serialize(), function (data) {
                         //alert(JSON.stringify(data));
                         var state = data.status;
                         var killResult = data.msg;
                         var orderId = data.data;
                         if (state == 1) {
                             alert(JSON.stringify(data))
                             location.href = "../../payFor/money/" + orderId;
                         } else {
                             layer.close(loding);
                             //显示秒杀结果
                             node.html("<lable class='label label-success'>" + killResult + "</lable>");
                         }
                     }).error(function (back) {
                         //alert(JSON.stringify(back));
                         //alert(back.responseJSON.message);
                         layer.close(loding);
                         layer.msg(back.responseJSON.message);

                     })
                 })
             });*/

        } else if (data.status == -1) {
            //未开启秒杀；主要防止长时间的等待，出现的时间误差；
            var now = data.NowTime;
            var start = data.startTime;
            var end = data.endTime;
            //重新计算时间
            countDown(productId, now, start, end);
        } else { // 0
            $("#seckillStatus").html("<lable class='label label-danger'>秒杀结束！</lable>");
        }
    })
}

//判断是否开始
function isStart(productId) {
    return "../../seckill/isStart/" + productId;
}

$(function () {
    /* 时间戳转化开始 */
    function dateFormat(value) {

        var date = new Date(value);//如果date为13位不需要乘1000
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
        var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
        var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
        var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
        var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
        return Y + M + D + h + m + s;

    }

    //获取现在时间
    setInterval(function () {
        var time = new Date();
        var year = time.getFullYear();  //获取年份
        var month = checkTime(time.getMonth() + 1);  //获取月份
        var day = checkTime(time.getDate());   //获取日期
        var hour = checkTime(time.getHours());   //获取时
        var minite = checkTime(time.getMinutes());  //获取分
        var second = checkTime(time.getSeconds());  //获取秒
        /****当时、分、秒、小于10时，则添加0****/
        function checkTime(i) {
            if (i < 10) {
                return ("0" + i);
            } else {
                return i;
            }
        }

        var box = $(".now");
        box.text(year + "-" + month + "-" + day + "  " + hour + ":" + minite + ":" + second);
    }, 1000);


    //$.ajaxSettings.async = true;//开启异步
    $.post("/seckill/time/now", {}, function (data) {
        if (data.status == 200) {
            if (data.msg == "null") {
                $("#seckill_box").html("尚未开启抢购活动");
            } else {
                $.post("/seckill/isStart/" + data.msg, {}, function (back) {
                    //获取开始时间
                    var startTime = back.data.startTime;
                    $(".start").text(dateFormat(startTime));
                    //获取结束时间
                    var endTime = back.data.endTime;
                    $(".end").text(dateFormat(endTime));

                    var productId = data.msg;
                    $(".pruductIdTxt").text(productId);
                    //时间判断
                    var nowTime = data.data;
                    //alert(JSON.stringify(back)+","+nowTime+","+startTime+","+endTime);

                    $.post("/seckill/getSeckillPrroductStock/" + productId, {}, function (data) {
                        //alert(JSON.stringify(data));
                        $("#stcok").val(data.data.num);
                    }).error(function (data) {
                        layer.msg("系统异常，图片加载失败！");
                        //console.log(JSON.stringify(data));
                    })


                    countDown(productId, nowTime, startTime, endTime);
                })
            }
        } else {
            console.log('data:' + data);
        }
    }).error(function (data) {
        console.log(JSON.stringify(data))
    })
})


