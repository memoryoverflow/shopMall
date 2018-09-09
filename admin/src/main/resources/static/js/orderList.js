$(function () {
    //表格行 悬停 变色


    layui.use(['table', 'layer'], function () {
        var table = layui.table;
        var layer = layui.layer;
        var $ = layui.$;

        //加载层
        function loding() {
            //loading层
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.3
                , time: 20000
            });
            return loding;
        }

        var loading = loding();
        //方法级渲染
        var tableIns = table.render({
            elem: '#LAY_table_user'
            , url: 'mall/orderList'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true}
                , {field: 'orderId', title: '编号ID', sort: true, align: 'center', fixed: true, width: 200}
                , {field: 'userId', title: '用户编号', align: 'center', width: 200}
                , {field: 'totalPrice', title: '总价', sort: true, align: 'center', width: 200, templet: '#totalPrice'}
                , {field: 'createTime', templet: '#createTime', title: '下单时间', sort: true, align: 'center', width: 200}
                , {field: 'isPayfor', title: '状态', sort: true, align: 'center', width: 180, templet: '#payfor'}
                , {title: '操作', fixed: 'right', width: 220, align: 'center', toolbar: '#barDemo'}
            ]]
            , id: 'testReload'
            , page: true
            , height: 'full-200'
            , skin: 'nob'
            , limit: 15
            , limits: [5, 15, 20, 30, 40, 50, 100]
            , done: function (res, curr, count) { //数据加载完后的回调函数
                layer.close(loading);
            }
        });

        //表格搜索重载函数
        function tabReloadSearch(obj, loading) {
            //alert(JSON.stringify(obj));
            //执行重载

            var strDate = obj[2].value;

            var startTime, endTime;
            if (strDate.length == 0) {
                tableIns.reload({
                    url: 'mall/orderList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        orderId: obj[0].value
                        , name: obj[1].value
                        , email: obj[3].value
                        , isPayfor: obj[4].value

                    }
                    , done: function (res, curr, count) { //数据加载完后的回调函数
                        layer.close(loading);
                    }
                });
            } else {
                var startTimeStr = strDate.substring(0, 20);
                var endTimeStr = strDate.substring(21, 41);
                endTime = new Date(Date.parse(endTimeStr.replace(/-/g, "/")));
                startTime = new Date(Date.parse(startTimeStr.replace(/-/g, "/")));
                tableIns.reload({
                    url: 'mall/orderList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        orderId: obj[0].value
                        , name: obj[1].value
                        , startTime: startTime
                        , endTime: endTime
                        , email: obj[3].value
                        , isPayfor: obj[4].value

                    }
                    , done: function (res, curr, count) { //数据加载完后的回调函数
                        layer.close(loading);
                    }
                });
            }

        }

        //表格重载函数
        function tabReload(loading) {
            tableIns.reload({
                url: 'mall/orderList'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    orderId: ""
                    , name: ""
                    , email: ""
                    , isPayfor: ""
                }
                , done: function (res, curr, count) { //数据加载完后的回调函数
                    layer.close(loading);
                }
            });

        }


        //搜索分页
        $('.main_top .layui-btn').on('click', function () {
            debugger;
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.orderSerachForm.elements.length - 6; i++) {
                if (document.orderSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            var radioVal = $("input[type='radio']:checked").val();
            //如果表单有一个不为空
            if (str.length > 0) {
                var obj = $("#orderSerachForm").serializeArray();
                var loading = loding();
                tabReloadSearch(obj, loading);
            } else {
                //如果单选框 选中
                if (radioVal.length > 0) {
                    var obj = $("#orderSerachForm").serializeArray();
                    var loading = loding();
                    tabReloadSearch(obj, loading);
                } else {
                    layer.msg("请输入条件", {icon: 2, time: 1000})
                }
            }
        });


        /*---------------------------------------*/
        //刷新按钮
        $("#reloadTab").click(function () {
            var loading = loding();
            tabReload(loading);
        })
        /*---------------------------------------*/

        //顶部的删除 和添加按钮
        $('.main_content .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        var $ = layui.$, active = {
            getCheckData: function () { //添加
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                layer.alert(JSON.stringify(data));
            }
            , getCheckLength: function () { //删除
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                if (data.length == 0) {
                    layer.alert("请至少选择一条", {icon: 5})
                } else {
                    //loading层
                    layer.confirm("确定删除么？", {icon: 3}, function (index) {
                        layer.close(index);
                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.3
                        });

                        var o_Id = new Array();
                        for (var i = 0; i < data.length; i++) {
                            o_Id.push(data[i].orderId);
                        }
                        //将id 传回后端删除
                        $.ajax({
                            url: "mall/delOrdersById",
                            type: "post",
                            data: {
                                "ids": o_Id,
                            },
                            traditional: true,//这里设置为true
                            success: function (data) {
                                layer.close(loding);
                                //layer.alert(JSON.stringify(data));
                                //删除成功表格重载刷新数据
                                //tabReload();
                                layer.alert('演示模式，不允许操作', {icon: 2})
                            },
                            error: function () {
                                layer.msg("系统繁忙，稍后再试！")
                            }
                        });
                    })
                }
            }
        };

        //操作栏的 监听工具条 查看 和删除
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                layer.msg('ID：' + data.id + ' 的查看操作');
            } else if (obj.event === 'del') {
                layer.confirm('真的删除吗', {icon: 3}, function (index) {

                    $.post("mall/delOrdersById", {ids: obj.data.orderId}, function (data) {
                        //layer.alert(JSON.stringify(data));
                        //删除成功刷新数据
                        //tabReload();
                        layer.alert('演示模式，不允许操作', {icon: 2})
                    }).error(function () {
                        layer.msg("系统异常，稍后再试！")
                    })
                    layer.close(index);
                });
            } else if (obj.event === 'edit') {
                //layer.alert('编辑行：<br>' + JSON.stringify(obj))
                $("#orderId").text(obj.data.orderId);
                var pay;
                if (obj.data.isPayfor == 0) {
                    pay = '未支付';
                } else if (obj.data.isPayfor == 1) {
                    pay = '待发货'
                } else if (obj.data.isPayfor == 2) {
                    pay = '配送中'
                }
                $("#isPayfor").text(pay);
                $("#creatTime").text(dateFormat(obj.data.createTime));

                //收货认信息
                $("#recTel").text(obj.data.address.recTel);
                $("#recUser").text(obj.data.address.recUser);


                var ad = obj.data.address.address;
                if (ad.length > 12) {
                    $("#recAddress").html(ad.substring(0, 12) + "...");
                    $("#recAddress").attr("tittle", ad);
                } else {
                    $("#recAddress").html(ad);
                }


                //产品信息
                $("#productMsgTab").empty();
                $("#Price").text(obj.data.totalPrice);
                var tab = "";
                for (var i = 0; i < obj.data.userProducts.length; i++) {
                    tab += "<table class='layui-table'lay-skin='line'>\n" +
                        "                    <colgroup>\n" +
                        "                        <col width='330'>\n" +
                        "                        <col width='150'>\n" +
                        "                        <col width='150'>\n" +
                        "                        <col width='150'>\n" +
                        "                        <col>\n" +
                        "                    </colgroup>\n" +
                        "                    <tr>\n" +
                        "                        <td>\n" +
                        "                            <ul>\n" +
                        "                                <li style='float: left'>\n" +
                        "                                    <a title='点击查看商品详情'>\n" +
                        "                                       <img src=" + obj.data.userProducts[i].up_img + ">\n" +
                        "                                    </a>\n" +
                        "                                </li>\n" +
                        "                                <li style='width: 300px;padding-top: 20px;padding-left: 10px' >\n" +
                        "                                    <a>" + obj.data.userProducts[i].up_name + "</a>\n" +
                        "                                </li>\n" +
                        "                            </ul>\n" +
                        "                        </td>\n" +
                        "                        <td>\n" +
                        "                            <p>规格：默认</p>\n" +
                        "                            <p>尺寸：<span>" + obj.data.userProducts[i].up_size + "</span></p>\n" +
                        "                            <p>颜色：<span>" + obj.data.userProducts[i].up_color + "</span></p>\n" +
                        "                        </td>\n" +
                        "                        <td style='color: #e95f6e'>单价：¥&nbsp;<span>" + obj.data.userProducts[i].up_price + "</span>\n" +
                        "                        </td>\n" +
                        "                        <td><span>" + obj.data.userProducts[i].up_num + " </span>&nbsp;双</td>\n" +
                        "                        <td style='color: #e95f6e'>总价：" + obj.data.userProducts[i].up_num + " × " + obj.data.userProducts[i].up_price + "=¥&nbsp;<span>" + obj.data.userProducts[i].up_totalprice + "</span>\n" +
                        "                        </td>\n" +
                        "                        <td>\n" +
                        "                        </td>\n" +
                        "                    </tr>\n" +
                        "                </table>"
                }

                $("#productMsgTab").append(tab);
                var all = layer.open({
                    type: 1,
                    content: $("#orders"),
                    area: ['820px', '495px'],
                    maxmin: true
                    , skin: 'layuiAlert'
                    , title: '订单详情'
                });
                layer.full(all);

            } else if (obj.event === 'handle') {

                if (obj.data.isPayfor == 0) {
                    layer.alert("商家尚未付款", {icon: 3}, function (index) {
                        layer.close(index);
                    })
                }
                if (obj.data.isPayfor > 1) {
                    layer.alert("已处理", {icon: 3}, function (index) {
                        layer.close(index);
                    })
                }
                if (obj.data.isPayfor == 1) {

                    //收货认信息
                    $("#a_recUser").val(obj.data.address.recUser);
                    $("#a_recTel").val(obj.data.address.recTel);
                    $("#a_recAddress").val(obj.data.address.address);


                    layer.open({
                        type: 1
                        , area: ['900px', '495px']
                        , title: '订单处理'
                        , content: $("#orderHadle")
                    })


                    function myCheck() {
                        for (var i = 0; i < document.LogisticsForm.elements.length - 1; i++) {
                            if (document.LogisticsForm.elements[i].value == "") {
                                layer.msg("当前表单不能有空项", {icon: 2, anim: 6});
                                document.LogisticsForm.elements[i].focus();
                                return false;
                            }
                        }
                        return true;
                    }

                    $("#LogisticsSubmitBtn").off().on('click', function () {
                        if (myCheck()) {
                            var key = loding();
                            $.ajaxSettings.async = true;
                            $.post("/mall/addLogistics", $.param({'o_Id': obj.data.orderId}) + '&' + $("#LogisticsForm").serialize(), function (back) {
                                if (back.status == 200) {
                                    layer.msg("已发货", {icon: 1}, function (index) {
                                        layer.close(key);
                                        tabReload();
                                    })
                                }
                            }).error(function (e) {
                                layer.close(key);
                                layer.msg("系统繁忙，请稍后再试！");
                                console.log(JSON.stringify(e));
                            })
                            //layer.alert($("#LogisticsForm").serialize());

                        } else {

                        }
                    })
                }

            } else if (obj.event === 'return') {
                if (obj.data.isPayfor == 4) {
                    var index2;
                    var index = layer.open({
                        type: 1,
                        title: '处理退货',
                        area: ['900px', '580px'], //宽高
                        moveOut: true,
                        content: $("#returnProductDetail")
                        , success: function () {
                            var key = layer.msg('数据加载中', {
                                icon: 16
                                , shade: 0.2
                                , time: 20000
                            });
                            $.ajaxSettings.async = true;
                            $.post("/mall/returnOrderDetail", {"return_oId": obj.data.orderId}, function (back) {
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

                            }).error(function (b) {
                                layer.close(key);
                                layer.msg("系统繁忙，请稍后再试!");
                                console.log(JSON.stringify(b))
                            });

                        }
                        , btn: ['处理']
                        , yes: function (index, layero) {
                            //按钮【按钮一】的回调
                            index2=layer.open({
                                type: 1,
                                title: '处理退货',
                                area: ['500px', '340px'], //宽高
                                moveOut: true,
                                content: $("#chuliDiv")
                            })
                        }

                    })
                    $("#chuliSubmitBtn").off().on('click', function () {
                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.2
                            , time: 20000
                        });
                        $.ajaxSettings.async = true;
                        $.post("/mall/IsAgreeReturnOrder", $.param({"return_oId": obj.data.orderId})+"&"+ $("#updateReturnForm").serialize(), function (back) {
                            tabReload();
                            layer.alert("已处理", {icon: 1}, function (i) {
                                layer.close(i);
                                layer.close(key);
                                layer.close(index2);
                                layer.close(index);
                            })

                        }).error(function (i) {
                            layer.close(key);
                            layer.msg("系统繁忙，请稍后再试！");
                        })
                    })
                } else {
                    layer.alert("订单正常", {icon: 3}, function (i) {
                        layer.close(i);
                    })
                }
            }
        });


    });
})

