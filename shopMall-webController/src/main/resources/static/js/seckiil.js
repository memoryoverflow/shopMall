$(function () {
    layui.use(['layer'], function () {
        var layer = layui.layer;
        //初始化秒杀时间
        $.post("/seckill/time/now", {}, function (data) {
            if (data.status == 200) {
                //获取开始时间
                var startT = new Date($("#startTime").text());
                var startTime = startT.getTime();//获得是时间戳

                //获取结束时间
                var endT = new Date($("#endTime").text());
                var endTime = endT.getTime();

                var productId = data.msg;
                //时间判断
                var nowTime = data.data;
                btn.countDown(productId, nowTime, startTime, endTime);

            } else if (data.status == "403") {
                layer.alert(data.msg, {icon: 4}, function (index) {
                    location.href = "../../mall/toLogin";
                })
            } else {
                console.log('data:' + data);
            }
        }).error(function () {
            console.log('data:' + data);
        })

        var defalutAddressId = "";

        var btn = {
            getUserMsg: function () {
                //将图片输出
                var id = "";
                $.ajaxSettings.async = true;
                $.post("/user/isLogin", {}, function (data) {
                    if (data.status == 200) {

                        id = data.data.uId;
                        $("#userHeadImgg").html("<img style='border-radius:" +
                            " 100px;border:1px solid white;' " +
                            "width='25' height='25' src=" + data.data.img + ">");
                    } else if (data.status == "403") {
                        layer.alert(data.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                }).error(function (ba) {
                    console.log(JSON.stringify(ba));
                })
                return id;
            }
            //判断是否开始
            , isStart: function (productId) {
                return "/seckill/isStart/" + productId;
            }
            //执行秒杀
            , isKilling: function (productId, node) {
                node.html("<input type='button' id='killingBtn' class='layui-btn  layui-btn-danger' value='立即抢购'/>").hide();
                //先判断 秒杀 是否 开启
                $.post(btn.isStart(productId), {}, function (data) {
                    if (data.status == 200) {

                        //alert(JSON.stringify(data))
                        if (data.msg == 1) {//1 代表秒杀开启
                            node.show();
                            //秒杀按钮绑定事件
                            $("#killingBtn").click(function () {
                                //只能点击一次秒杀
                                $(this).attr("disabled", true);
                                layui.use(['layer'], function () {
                                    var layer = layui.layer;

                                    //loading层
                                    var loding = layer.msg("正在抢购", {
                                        icon: 16
                                        , shade: 0.2
                                        , time: 20000
                                    });

                                    //执行秒杀，发送请求
                                    $.ajaxSettings.async = true;
                                    $.post("/seckill/seckilling", $("#orderForm").serialize(), function (data) {
                                        //alert(JSON.stringify(data));
                                        var state = data.status;
                                        var killResult = data.msg;
                                        var orderId = data.data;
                                        if (state == 200) {
                                            //alert(JSON.stringify(data))
                                            location.href = "../../payFor/seckillMoney/" + orderId;
                                        } else {
                                            layer.close(loding);
                                            //显示秒杀结果
                                            node.html("<lable class='label label-success'>" + killResult + "</lable>");
                                        }
                                    }).error(function (back) {
                                        layer.close(loding);
                                        layer.msg(back.responseJSON.message);

                                    })
                                })
                            });

                        } else if (data.msg == -1) {
                            //未开启秒杀；主要防止长时间的等待，出现的时间误差；
                            var now = data.NowTime;
                            var start = data.startTime;
                            var end = data.endTime;
                            //重新计算时间
                            btn.countDown(productId, now, start, end);
                        } else { // 0
                            $("#seckill_box").html("<lable class='label label-danger'>秒杀结束！</lable>");
                        }
                    } else if (data.status == "403") {
                        layer.alert(data.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                }).error(function (b) {
                    console.log(JSON.stringify(b))
                })
            }
            //秒杀倒计时
            , countDown: function (productId, nowTime, startTime, endTime) {
                var seckillBox = $("#seckill_box");
                var seckillStatus = $("#seckillStatus");

                //秒杀时间未到 倒计时
                if (nowTime > endTime) {
                    $("#seckillStatus").html("<lable class='label label-danger'>抢购已结束！</lable>");
                } else if (nowTime < startTime) {

                    var killTime = new Date(startTime + 1000);
                    seckillStatus.countdown(killTime, function (event) {
                        var format = event.strftime("倒计时：%D天 %H时 %M分 %S秒");
                        seckillStatus.html(format);

                    }).on("finish.countdown", function () {
                        seckillStatus.html("抢购进行中");
                        btn.isKilling(productId, seckillBox);
                    });
                } else {
                    seckillStatus.html("抢购进行中");
                    btn.isKilling(productId, seckillBox);
                }
            }
            //获取用户收货地址
            , getUserAddress: function () {
                //获取用户Id,根据id 拿到 用户的 收货地址
                //获取用户信息 动态写出

                //loading层
                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.2
                    , time: 20000
                });

                $.ajaxSettings.async = true;
                $.post("/user/getUserAddressMsg", function (back) {
                    if (back.status == 200) {

                        var uMsg = "";
                        var u = JSON.stringify(back);
                        for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                            if (back.data[0].uAddressList[i].isDefault == 1) {
                                defalutAddressId = back.data[0].uAddressList[i].addressId;
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'checked/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updatedizhi(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            } else {
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updatedizhi(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            }
                        }
                        $("#userMsgUL").empty();
                        $("#userMsgUL").append(uMsg);
                        layer.close(key);
                    } else if (back.status == "403") {
                        layer.close(key);
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                }).error(function (b) {
                    console.log(JSON.stringify(b))
                })
            }
            , alertUserAdderss: function () {
                //弹出收货地址

                var index = layer.open({
                    type: 1,
                    title: '确认收货地址',
                    skin: '', //加上边框
                    area: ['1000px', '540px'], //宽高
                    content: $("#detail")
                    , success: function () {
                        btn.getUserAddress();
                    }
                });


                //选择收货地址的时候，点击确定按钮，将收货地址写到抢购页面
                $("#submit_order").click(function () {
                    //点击确定按钮
                    $("#userMsgUL input").each(function (i) {
                        if (this.checked) {
                            var msg = $("#userMsgUL li:eq(" + i + ")").text().substring(0, $("#userMsgUL li:eq(" + i + ")").text().length - 6);

                            //将地址id 写到 订单中input中
                            var addressId = $("#userMsgUL li input:eq(" + i + ")").val();

                            $.cookie('addressId', addressId, {expires: 7});
                            $("input[name='address_Id']").val($.cookie('addressId'));


                            //定义一个cookie 放用户收货地址
                            $.cookie('userAddressGetGoods', msg, {expires: 7});
                            var str = $.cookie('userAddressGetGoods');
                            if ($.cookie('userAddressGetGoods') == null) {
                                $("#addAddressText").html("商品发送到:[ " + msg + " <<a style='cursor:pointer;color:red' onclick='alertUserAdderss()'>修改</a>>]");
                            } else {
                                $("#addAddressText").html("商品发送到:[ " + str + " <<a style='cursor:pointer;color:red' onclick='alertUserAdderss()'>修改</a>>]");
                            }
                        }
                    })
                    layer.close(index);
                })

                //点击新新增地址按钮
                $("#addAddress_btn").click(function () {
                    //先清空一遍原来的信息
                    $("input[name='recUser'").val("");
                    $("input[name='recTel'").val("");
                    $("input[name='address'").val("");
                    $("input[name='addressId'").val("");


                    //弹出一个层

                    //layer.close(layer.index);
                    var indexNum = layer.open({
                        type: 1,
                        title: '添加收货地址',
                        skin: '', //加上边框
                        move: false, //不允许当前用户拖动当前窗口
                        area: ['1000px', '540px'], //宽高
                        content: $("#addAdds_Div")

                    });


                    //在填写表单之前禁用提交按钮
                    $("#btn_addAddress").attr("disabled", "disabled");
                    $("#btn_addAddress").css("cursor", "not-allowed");
                    $("#btn_addAddress").css("background-color", "gray");
                    //给input 框绑定失焦事件
                    $(".isEmpty").each(function (i) {
                        this.onblur = function () {
                            var arr = checkInputIsNull(i);
                            if (arr[1] == 1) {
                                $(this).css("border", "1px solid red");
                            } else {
                                $(this).css("border", "");
                            }
                            //alert(arr[0]);
                            if (arr[0].length == 0) {
                                $("#btn_addAddress").attr("disabled", false);
                                $("#btn_addAddress").css("cursor", "");
                                $("#btn_addAddress").css("background-color", "");
                            }
                        }
                    })

                    function checkInputIsNull(i) {
                        var arr = [];
                        var str = "";
                        var m = 1;
                        $(".isEmpty").each(function (n) {
                            if ($(this).val().length == 0) {
                                str += ".";
                            }
                        })
                        if ($(".isEmpty:eq(" + i + ")").val().length == 0) {
                            m = 1;
                        } else {
                            m = -1;
                        }
                        arr.push(str, m);
                        return arr;
                    }


                    //获取新增地址表单信息,并且持久化地址
                    $("#btn_addAddress").off().on('click', function () {

                        //loading层
                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.2
                            , time: 20000
                        });


                        var recUser = '';
                        var recTel = '';
                        var address = '';
                        recUser = $("input[name='recUser'").val();
                        recTel = $("input[name='recTel'").val();
                        address = $("input[name='address'").val();
                        //去掉空格
                        var ad = address.replace(/\s|\xA0/g, "");


                        //地址持久化
                        $.ajaxSettings.async = true;
                        $.post("/user/addAddress", {
                            recUser: recUser,
                            address: ad,
                            recTel: recTel
                        }, function (back) {

                            if (back.status == 200) {
                                layer.alert("添加成功！", function (index) {
                                    //舒心地址列表
                                    //关闭加载层
                                    layer.close(key);
                                    //关闭当前层
                                    layer.close(index);
                                    layer.close(indexNum)
                                    btn.getUserAddress();
                                })
                            } else if (back.status == "403") {
                                layer.close(key);
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            } else {
                                //关闭加载层
                                layer.close(key);
                                layer.msg("添加失败！");
                            }
                        });
                    })
                })

            }
        }

        var uId = btn.getUserMsg();
        btn.getUserAddress();

        //点击收货地址
        $("#ad").click(function () {
            btn.alertUserAdderss();
        })


// ------------这一块是操作 cookie 选中商品颜色和收货地址，防止抢购时刷新页面 得从新选择，避免错失时机
        //收货地址显示
        var str = $.cookie('userAddressGetGoods');
        if (str == null) {
            $("#addAddressText").html("");
        } else {

            $("#addAddressText").html("商品发送到:[ " + str + " <<a style='cursor:pointer;color:red' onclick='alertUserAdderss()'>修改</a>>]");
        }
        //创建一个cookie 保存商品属性 option
        $("select[name='userProducts[0].up_size']").change(function () {
            var val = $("select[name='userProducts[0].up_size'] option:selected").val();
            $.cookie("size", val, {expires: 7});
            if ($.cookie("size") != null) {
                $("select[name='userProducts[0].up_size'] option").each(function () {
                    if ($(this).val() == $.cookie("size")) {
                        //this.selected=selected;
                        $(this).attr("selected", "selected");
                    }
                })
            }
        })
        if ($.cookie("size") != null) {
            $("select[name='userProducts[0].up_size'] option").each(function () {
                if ($(this).val() == $.cookie("size")) {
                    //this.selected=selected;
                    $(this).attr("selected", "selected");
                }
            })
        }

        $("select[name='userProducts[0].up_color']").change(function () {
            var val = $("select[name='userProducts[0].up_color'] option:selected").val();
            $.cookie("color", val, {expires: 7});
            if ($.cookie("color") != null) {
                $("select[name='userProducts[0].up_size'] option").each(function () {
                    if ($(this).val() == $.cookie("color")) {
                        //this.selected=selected;
                        $(this).attr("selected", "selected");
                    }
                })
            }
        })

        if ($.cookie("color") != null) {
            $("select[name='userProducts[0].up_color'] option").each(function () {
                if ($(this).val() == $.cookie("color")) {
                    //this.selected=selected;
                    $(this).attr("selected", "selected");
                }
            })
        }
        if ($.cookie('addressId') != null) {
            $("input[name='address_Id']").val($.cookie('addressId'));
        } else {
            //如果没有选择收货地址默认选择当前用户的第一个收货地址
            $("input[name='address_Id']").val();
        }
//-----------------------------------------------
    })
})


//下面是对收货地址的操作


//修改用户收货地址的按钮 弹出层
function updatedizhi(msg) {
    //将当前条的数据 写到输入框中
    $("input[name='recUser'").val(msg.recUser);
    $("input[name='recTel'").val(msg.recTel);
    $("input[name='address'").val(msg.address);

    //修改原来 添加按钮 改为修改
    $("#btn_addAddress").val("修改");

    //将值写到框后 弹出修改div框
    layui.use(['layer'], function () {
        var layer = layui.layer;
        var indexNum = layer.open({
            type: 1,
            title: '下单信息',
            skin: '', //加上边框
            shade: false, //禁止遮罩层
            move: false, //不允许当前用户拖动当前窗口
            area: ['1000px', '540px'], //宽高
            content: $("#addAdds_Div"),
            cancel: function (index) {
                layer.close(index);
            }
        });

        //执行修改的按钮
        $("#btn_addAddress").off().on('click', function () {


            //loading层
            var key = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.2
                , time: 20000
            });


            //获取新增地址表单信息,并且持久化地址
            var recUser = '';
            var recTel = '';
            var address = '';
            var addressId = '';
            recUser = $("input[name='recUser'").val();
            recTel = $("input[name='recTel'").val();
            address = $("input[name='address'").val();
            addressId = $("input[name='addressId'").val();
            //删掉空格
            var ad = address.replace(/\s|\xA0/g, "");


            //ajax 与后端交互 数据持久化
            $.ajaxSettings.async = true;
            $.post("/user/updateAddress", {
                recUser: recUser,
                address: ad,
                recTel: recTel,
                addressId: msg.addressId,
                isDefault: msg.isDefault
            }, function (back) {
                //alert(JSON.stringify(back));
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    if (back.status == 200) {
                        layer.alert("添加成功！", function (index) {

                            //关掉加载层
                            layer.close(key);
                            layer.msg("修改成功！");
                            //关闭自己当前层
                            layer.close(index);
                            layer.close(indexNum);
                            getAddress();
                        })
                    } else if (back.status == "403") {
                        layer.close(key);
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    } else {
                        layer.close(key);
                        layer.msg("修改失败！");
                    }
                })

            }).error(function (back) {
                layer.msg("系统繁忙，请稍后再试");
            })
        })
    })
}

function getAddress() {
    $.ajaxSettings.async = true;
    $.post("/user/getUserAddressMsg", function (back) {
        if (back.status == 200) {
            var uMsg = "";
            for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                if (back.data[0].uAddressList[i].isDefault == 1) {
                    uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "' checked/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updatedizhi(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                } else {
                    uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updatedizhi(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                }
            }
            $("#userMsgUL").empty();
            $("#userMsgUL").append(uMsg);
        } else if (back.status == "403") {
            layer.alert(back.msg, {icon: 4}, function (index) {
                location.href = "../../mall/toLogin";
            })
        }
    })
}


