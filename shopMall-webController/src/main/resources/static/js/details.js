$(function () {
    $(".product-colors input[type='radio']:eq(0)").attr("checked", "true");
    //存储地址信息的；
    var addressMap = new Map();

    //存储用户信息的
    var uId;

    //校验是否登录
    function isLogin() {
        var IsLogIng = "";
        $.ajaxSettings.async = false;
        $.post("/user/isLogin", {}, function (data) {
            //alert(JSON.stringify(data))
            if (data.status == "403") {
                IsLogIng = "false";
            } else {
                IsLogIng = "true";
                $("#userHeadImgg").html("" +
                    "<img style='border-radius: 100px;border:1px solid white;" +
                    "position: relative;top: 10px' width='25' height='25' " +
                    "src=" + data.data.img+ ">");
                $(".top-header-right ul").css("margin-top","-10px");

                uId = data.data.uId;
                $(".out").show();
            }
        })
        return IsLogIng;
    }

    var bol = isLogin();
    var btn = {

        getShopCartMsg: function () {
            $.ajaxSettings.async=true;
            $.post("/shopCart/ShopCartList", {}, function (back) {
                if (back.status==200) {
                    if (back.data.length > 0) {
                        $("#Cart").text("查看购物车");
                        $("#cart_num").text(back.data.length);
                    }
                }else if(back.status=="403") {
                    layer.alert(back.msg, {icon: 4}, function (index) {
                        location.href = "../../mall/toLogin";
                    })
                }
            }).error(function (data) {
                console.log(JSON.stringify(data));
            })
        },
        checkNum: function () {
            if ($(".sum").val() == 1) {
                $(".reduce").css("pointer-events", "none");
            } else {
                $(".reduce").css("pointer-events", "");
            }
        },
        getUserAddress: function () {
            layui.use(['layer', 'jquery'], function () {
                var layer = layui.layer;
                var $ = layui.jquery;
                //获取用户Id,根据id 拿到 用户的 收货地址
                //获取用户信息 动态写出

                //loading层
                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });

                $.ajaxSettings.async = true;
                $.post("/user/getUserAddressMsg", function (back) {
                    var uMsg = "";
                    var u = JSON.stringify(back);
                    if (back.status == 200) {
                        for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                            addressMap.set(back.data[0].uAddressList[i].addressId, back.data[0].uAddressList[i]);
                            if (back.data[0].uAddressList[i].isDefault == 1) {
                                $("input[name='isDefault']").val(1);
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'checked/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress'  href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            } else {
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            }
                        }

                            $("#userMsgUL").empty();
                            $("#userMsgUL").append(uMsg);
                        $(".updateAddress").each(function () {
                            this.onclick = function () {
                                var addressid = $(this).parent().siblings("input").val();
                                var addressMsg = addressMap.get(addressid);
                                btn.updateAddress(addressMsg);

                            }
                        })
                    }else if(back.status=="403") {
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                    layer.close(key);

                }).error(function () {
                    layer.close(key);
                })
            })
        },
        buyShop: function () {
            layui.use(['layer'], function () {
                var layer = layui.layer;
                //取出要购买的商品参数
                var name = $("input[name='up_name']").val();
                var productId = $("input[name='up_productId']").val();
                var img = $("input[name='up_img']").val();
                var price = $("input[name='up_price']").val();
                var totalPrice = $("input[name='up_totalprice']").val();
                var color = $("input[name='up_color']").val();
                var size = $("select[name='up_size']").val();
                var num = $("input[name='up_num']").val();

                //写入到 订单填写处
                $("input[name='userProducts[0].up_name']").val(name);
                $(".up_name").text(name);
                $("input[name='userProducts[0].up_productId']").val(productId);
                $(".up_img").attr("src", img);
                $("input[name='userProducts[0].up_price']").val(price);
                $("input[name='userProducts[0].up_img").val(img);
                $("input[name='userProducts[0].up_totalprice']").val(totalPrice);
                $("input[name='userProducts[0].up_color']").val(color);
                $("input[name='userProducts[0].up_size']").val(size);
                $("input[name='userProducts[0].up_num']").val(num);
                $("input[name='totalPrice']").val(totalPrice);

                var index1 = layer.open({
                    type: 1,
                    title: '下单信息',
                    skin: '', //加上边框
                    area: ['1000px', '540px'], //宽高
                    content: $("#detail"),
                    success: function () {
                        btn.getUserAddress();
                    }
                })


                //确认订单提交按钮 BuySureBtn

                $("#BuySureBtn").off().on('click', function () {
                    $("#BuySureBtn").attr('disabled', true)
                    $("#userMsgUL li input").each(function () {
                        if (this.checked) {
                            $("input[name='address_Id']").val($(this).val());
                            //var val = $(this).val();
                        }
                    })


                    //loading层
                    var key = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.1
                        , time: 20000
                    });

                    //提交表单
                    $.ajaxSettings.async = true;
                    $.post("/order/creatOrder", $("#orderForm").serialize(), function (back) {
                        //alert(JSON.stringify(back));
                        if (back.status == 200) {
                            location.href = "/payFor/money/" + back.data;
                        }else if(back.status=="403") {
                            layer.alert(back.msg, {icon: 4}, function (index) {
                                location.href = "../../mall/toLogin";
                            })
                        }else{
                            layer.msg("库存不足！")
                        }
                    }).error(function (back) {
                        //JSON.stringify(back);
                        layer.close(key);
                        $("#BuySureBtn").attr('disabled', false);
                        layer.msg("系统繁忙，请稍后再试");
                    })
                })
            });

            //添加地址按钮
            $("#addAddressBtn").off().on('click', function () {

                //先清空一遍原来的信息
                $("input[name='recUser'").val("");
                $("input[name='recTel'").val("");
                $("input[name='address'").val("");
                $("input[name='addressId'").val("");
                layui.use(['layer'], function () {
                    var layer = layui.layer;
                    var indexNum = layer.open({
                        type: 1,
                        title: '下单信息',
                        skin: '', //加上边框
                        shade: false, //禁止遮罩层
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

                    //与后端交互
                    $("#btn_addAddress").off().on('click', function () {

                        //获取新增地址表单信息,并且持久化地址
                        var recUser = '';
                        var recTel = '';
                        var address = '';
                        recUser = $("input[name='recUser'").val();
                        recTel = $("input[name='recTel'").val();
                        address = $("input[name='address'").val();

                        var ad = address.replace(/\s|\xA0/g, "");

                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.1
                            , time: 20000
                        });

                        $.ajaxSettings.async = true;
                        $.post("/user/addAddress", {
                            user_id: uId,
                            recUser: recUser,
                            address: ad,
                            recTel: recTel
                        }, function (back) {
                            //alert(JSON.stringify(back));

                            if (back.status == 200) {

                                layer.alert("添加成功！", {icon:1},function (index) {
                                    layer.close(indexNum);
                                    layer.close(index);
                                    layer.close(key);
                                    btn.getUserAddress();
                                })
                            } else if(back.status=="403") {
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            }else {
                                layer.msg("添加失败！");
                            }
                        }).error(function (back) {
                            //alert(JSON.stringify(back));
                            layer.msg("系统繁忙，请稍后再试");
                        })
                    })

                })
            })
        },
        reduce: function (i) {
            var temp = Number($(".sum").val());
            //数量
            var num = (temp - 1);
            $(".sum").val(num);

            //单价
            var price = Number($("#up_price").val());
            //总价钱
            var totalPrice = num * price;
            var flag = new Boolean();
            flag = Number.isInteger(totalPrice);
            if (flag) {
                totalPrice = totalPrice.toString() + ".00";
            } else {
                totalPrice = totalPrice.toFixed(2);
            }

            $("#TotalPrice").val(totalPrice);
            $("#up_totalprice").val(totalPrice);

            btn.checkNum();
        },
        plus: function () {
            var temp = Number($(".sum").val());
            //数量
            var num = (temp + 1);
            $(".sum").val(num);
            //单价
            var price = $("#up_price").val();
            //总价钱
            var totalPrice = num * price;
            var flag = new Boolean();
            flag = Number.isInteger(totalPrice);
            if (flag) {
                totalPrice = totalPrice.toString() + ".00";
            } else {
                totalPrice = totalPrice.toFixed(2);
            }

            //订单总价
            $("#TotalPrice").val(totalPrice);
            //商品总价
            $("#up_totalprice").val(totalPrice);
            btn.checkNum();
        },
        updateAddress: function (msg) {
            //将当前条的数据 写到输入框中
            $("input[name='recUser'").val(msg.recUser);
            $("input[name='recTel'").val(msg.recTel);
            $("input[name='address'").val(msg.address);
            $("input[name='addressId'").val(msg.addressId);
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
                });

                //执行修改的按钮
                $("#btn_addAddress").off().on('click', function () {
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
                    var key = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.1
                        , time: 20000
                    });
                    $.ajaxSettings.async = true;
                    $.post("/user/updateAddress", {
                        recUser: recUser,
                        address: ad,
                        recTel: recTel,
                        addressId: addressId,
                        isDefault: msg.isDefault
                    }, function (back) {
                        if (back.status == 200) {
                            layer.alert("添加成功！",{icon:1}, function (index) {
                                layer.close(indexNum);
                                layer.close(index);
                                layer.close(key);
                                btn.getUserAddress();
                            })
                        } else if(back.status=="403") {
                            layer.alert(back.msg, {icon: 4}, function (index) {
                                location.href = "../../mall/toLogin";
                            })
                        }else {
                            layer.msg("修改失败！");
                        }

                    }).error(function (back) {

                        layer.msg("系统繁忙，请稍后再试！");
                    })

                })
            })
        }
    }


//没有登录
    if (bol == "false") {
        $("#cart_num").text("0");
        //点击添加购物车 按钮 跳转登录界面
        $("#addShop_btn").click(function () {
            location.href = "/mall/toLogin";
        })

        //点击购买按钮
        $("#buyShop_btn").click(function () {
            location.href = "/mall/toLogin";
        })
    } else {
        //已经登录
        layui.use(['layer', 'jquery'], function () {
            var layer = layui.layer;
            var $ = layui.jquery;


            //显示购车条数,页面加载执行
            btn.getShopCartMsg();
            //点击加入购物车按钮
            $("#addShop_btn").click(function () {

                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });

                $.ajaxSettings.async = true;
                $.post("/shopCart/addShopCart", $("#buyForm").serialize(), function (back) {
                   // alert(JSON.stringify(back))
                    if (back.status == 200) {
                        layer.alert("已添加",{icon:1},function(index) {
                            layer.close(key)
                            layer.close(index)
                            btn.getShopCartMsg();
                        })
                    }else if(back.status=="403") {
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    } else {
                        layer.close(key)
                        layer.alert("加入购物车失败",{icon:2},function(index) {
                            layer.close(key)
                            layer.close(index)
                        })
                    }
                }).error(function (back) {
                    layer.alert("系统繁忙，请稍后再试！");
                    console.log(JSON.stringify(back));
                })
            })

            //初始化检验if 数量为 1 禁用按钮 页面加载执行
            btn.checkNum();


            //点击购买按钮
            $("#buyShop_btn").click(function () {
                btn.buyShop();
            })

            //点击加数量
            $(".plus").click(function () {
                btn.plus();
            })
            //点击减数量
            $(".reduce").click(function () {
                btn.reduce();
            })

        })

    }
})






