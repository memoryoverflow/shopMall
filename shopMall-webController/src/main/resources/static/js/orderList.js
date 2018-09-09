//显示购车条数,页面加载执行
(function getShopCartMsg() {
    $.post("/shopCart/ShopCartList", {}, function (back) {
        if (back.status == 200) {

            if (back.data.length > 0) {
                $("#Cart").text("查看购物车");
                $("#cart_num").text(back.data.length);
            }
        } else if (back.status == "403") {
            layer.alert(back.msg, {icon: 4}, function (index) {
                location.href = "../../mall/toLogin";
            })
        }
    })
})();

$(function () {
    //查询
    $("#chaxun").blur(function () {
        var val = $(this).val();
        if (val.length == 0) {

        } else {
            $("#serchForm").attr("action", "orderList");
            $("#serchForm").submit();
        }
    })

    //输入多少页跳转的
    $("#pageTxt").blur(function () {
        var temp = $("#pageTxt1").text();
        var val = $(this).val();
        window.location = "orderList?currPage=" + val + "&temp=" + temp;
    })

    //分页按钮处理
    $("#page ul li a").each(function () {
        this.click = function () {
            /*alert(1);*/
            $(this).css("color", "red");
        }
    })

    //单个删除提示
    $(".a_delete").each(function (i) {
        this.onclick = function () {
            if (confirm("删除将无法恢复 ?")) {
                return true;
            } else {
                return false;
            }
        }
    })

    /*删除多个按钮*/
    $("#delBtnAll").click(function () {
        var num = checkboxStaus();
        if (confirm("删除将无法恢复 ?")) {
            $("#delorderform").submit();
        } else {
            return false;
        }
    })

    /*检查复选框的选中状态*/
    function checkboxStaus() {
        var n = 0;
        $("input[name='orderIds']").each(function () {
            if (this.checked) {
                n++;
            }
        })
        return n;
    }

    //给每个复选框绑定一个click事件
    $("input[name='orderIds']").each(function (i) {
        this.onclick = function () {
            //获取选中的个数；
            var n = checkboxStaus();
            if (n > 0) {
                // if 已有选中的复选框，解除删除按钮禁用
                $("#delBtnAll").css("cursor", "pointer");
                $("#delBtnAll").attr("disabled", false);
            } else {
                $("#delBtnAll").css("cursor", "not-allowed");
                $("#delBtnAll").attr("disabled", true);
            }
        }
    })
    //复选框的全选
    $("#checkbox_id").click(function () {
        if (this.checked) {
            var len=$("#delorderform input[name='orderIds']").length;
            if (len>0) {
                $("#delorderform input[name='orderIds']").each(function (i) {
                    this.checked = true;
                })
                // if 已有选中的复选框，解除删除按钮禁用
                $("#delBtnAll").css("cursor", "pointer");
                $("#delBtnAll").attr("disabled", false);
            }

        } else {
            $("input[name='orderIds']").each(function (i) {
                this.checked = false;
            })
            $("#delBtnAll").css("cursor", "not-allowed");
            $("#delBtnAll").attr("disabled", true);
        }
    })
})

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

//查看物流findLogistics
function findLogistics(id) {
    layui.use("layer", function () {
        var layer = layui.layer;


        var index = layer.open({
            type: 1,
            title: '确认收货地址',
            skin: '', //加上边框
            area: ['600px', '340px'], //宽高
            content: $("#Logistics")
            , success: function () {
                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.2
                    , time: 20000
                });
                $.ajaxSettings.async = true;
                $.post("/order/findLogistics/" + id, {}, function (back) {
                    layer.close(key);
                    //layer.alert(JSON.stringify(back));
                    $("#timeL").html(dateFormat(back.data.cTime));
                    $("#recAd").html("[ " + back.data.recAddress + " ]");
                    $("#contentl").html(back.data.content);
                })
            }
        });


    })
}

//退货
function ReturnGoods(id) {
    layui.use("layer", function () {
        var layer = layui.layer;
        var index1 = layer.open({
            type: 1,
            title: '确认收货地址',
            skin: '', //加上边框
            area: ['900px', '640px'], //宽高
            content: $("#returnProduct")
        });

        $("#LogisticsSubmitBtn").off().on('click', function () {
            var key = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.2
                , time: 20000
            });

            var formData = new FormData(document.getElementById("returnForm"));//表单id
            formData.append("return_oId", id);
            $.ajax({
                url: '/order/addReturnGoods',
                type: 'POST',
                data: formData,
                async: true,
                cache: false,
                contentType: false,
                processData: false,
                success: function (result) {
                    //layer.alert(JSON.stringify(result))
                    if (result.status == 200) {
                        location.href = "/order/returnOrderDetail";
                        layer.alert("已提交", {icon: 1}, {
                            yes: function (index) {
                                layer.close(index);
                                layer.close(index1);
                                layer.close(key);
                            }
                        });
                    }
                },
                error: function () {
                    layer.msg("系统繁忙，请稍后再试！", {time: 2000});
                    layer.close(key);
                    layer.close(index1);
                }
            });
        })
    })
}


//退货详情
function ReturnGoodsDetail(id) {
    layui.use("layer", function () {
        var layer = layui.layer;
        var index = layer.open({
            type: 1,
            title: '退货信息',
            skin: '', //加上边框
            area: ['900px', '640px'], //宽高
            content: $("#returnProductDetail")
            , success: function () {
                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.2
                    , time: 20000
                });
                $.ajaxSettings.async = true;
                $.post("/order/returnOrderDetail", {"return_oId": id}, function (back) {
                    layer.close(key);
                    $("#detail div:eq(0) div").html(dateFormat(back.data.returnTime));
                    $("#detail .Detailreason").html(back.data.reason);
                    $("#detail .DetailRemark").html(back.data.remark);

                    var img1 = "", img2 = "", img3 = "";


                    if (back.data.img1 != null) {
                        img1 = "<img src='" + back.data.img1 + "' width='130px' />";
                    }
                    if (back.data.img2 != null) {
                        img2 = "<img src='" + back.data.img2 + "' width='130px' />";
                    }
                    if (back.data.img3 != null) {
                        img3 = "<img src='" + back.data.img3 + "' width='130px' />";
                    }


                    $("#detail .Detailimg").html(img1 + img2 + img3);
                    if (back.data.returnContent == null) {
                        $("#detail .DetailReturnContent").html(" <span style='color: gray'>  尚未处理！</span>");
                    }else{
                        $("#detail .DetailReturnContent").html(back.data.returnContent);
                    }
                }).error(function (b) {
                    layer.close(key);
                    layer.msg("系统繁忙，请稍后再试!");
                    console.log(JSON.stringify(b))
                });

            }
        })
    })
}

