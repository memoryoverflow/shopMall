/**
 * Created by Administrator on 2017/8/25.
 */
// 配置扩展方法路径
// layui.config({
//     base: './../frame/static/js/'   // 模块目录
// }).extend({                         // 模块别名
//     vip_nav: 'vip_nav'
//     , vip_tab: 'vip_tab'
//     , vip_table: 'vip_table'
// });


$(function () {
    //用户
    $.post("/mall/userList", {}, function (back) {
        //alert(JSON.stringify(back))
        $(".userCount").html(back.count);
    }).error(function (b) {
        console.log(JSON.stringify(b))
    })
    //在线用户
    $.post("/mall/OnlineUserList", {}, function (back) {
        //alert(JSON.stringify(back))
        $(".userLineCount").html(back.count);
    }).error(function (b) {
        console.log(JSON.stringify(b))
    })

    //商品个数
    $.post("/mall/productList", {}, function (back) {
        //alert(JSON.stringify(back))
        $(".productCount").html(back.count);
    }).error(function (b) {
        console.log(JSON.stringify(b))
        //alert(JSON.stringify(b))
    })


    //订单
    $.post("/mall/orderList", {page: 1, limit: 300000}, function (back) {
        //alert(JSON.stringify(back))
        $(".orderCount").html(back.count);
        var data = back.data;
        var a = 0;
        var b = 0;
        var c = 0;
        var d = 0;
        for (var i = 0; i < data.length; i++) {
            debugger;
            if (data[i].isPayfor == 0) {
                b++;
                $(".nopayfor").html(b);
            }
            if ((data[i].isPayfor == 1) || (data[i].isPayfor == 2)) {
                b++;
                $(".nodchuli").html(b);
            }
            if (data[i].isPayfor == 4) {
                c++;
                $(".isreturning").html(c);
            }
        }
    }).error(function (b) {
        console.log(JSON.stringify(b))
        //alert(JSON.stringify(b))
    })


})