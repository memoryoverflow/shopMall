//动态写值
$(function () {
    //1.初始化页面 获取购物车信息
    layui.use(['layer'], function () {
        var layer = layui.layer;
        var btn = {
            reloadCart: function () {
                $.ajaxSettings.async = true;
                $.post("/shopCart/ShopCartList", {}, function (back) {
                    if (back.data.length > 0) {
                        $("#Cart").text("查看购物车");
                        $("#cart_num").text(back.data.length);
                    }

                    var ul = "";
                    for (var i = 0; i < back.data.length; i++) {
                        var size = back.data[i].up_size;
                        var sizeflag = new Boolean();
                        sizeflag = Number.isInteger(size);
                        var price = back.data[i].up_price;
                        if (sizeflag) {
                            size = size.toString() + ".0";
                        }

                        var priceflag = new Boolean();
                        priceflag = Number.isInteger(price);
                        if (priceflag) {
                            price = price.toString() + ".0";
                        }

                        var totalprice = back.data[i].up_totalprice;
                        var totalpriceflag = new Boolean();
                        totalpriceflag = Number.isInteger(totalprice);
                        if (totalpriceflag) {
                            totalprice = totalprice.toString() + ".0";
                        }

                        //alert(back.data[i].up_size+" "+back.data[i].up_price+" "+back.data[i].up_totalprice);
                        ul += "<ul class='order_lists' > <li class='list_chk'> <input class='ids' type='checkbox' name='product' value='" + JSON.stringify(back.data[i]) + "'></li> <li class='list_con'><div class='list_img'><a href='javascript:;'><img style='border: 1px solid #ebebeb' src=" + back.data[i].up_img + "></a></div> <div class='list_text'><a href='javascript:;' >" + back.data[i].up_name + "</a></div></li>  <li class='list_info'>  <p>规格：默认</p> <p>尺寸：<span>" + size + "</span></p><p>颜色：<span>" + back.data[i].up_color + "</span></p> </li> <li class='list_price'> <p class='price'>" + price + "</p>  </li> <li class='list_amount'> <div class='amount_box'><a href='javascript:;' class='reduce reSty'>-</a><input  type='text' readonly='readonly'  class='sum' value=" + back.data[i].up_num + "><span hidden='hidden' class='JsonInput'>" + JSON.stringify(back.data[i]) + "</span><a href='javascript:;' class='plus' >+</a></div>   </li>  <li class='list_sum'><p class='sum_price'>" + totalprice + " </p></li> <li class='list_op'> <p class='del'><a href='javaScript:;'class='delBtn' onclick='delOneCart(" + JSON.stringify(back.data[i]) + ")'>移除商品</a> </p>   </li> </ul>";
                    }
                    $(".order_content").html(ul);
                })
            }
            , loading: function () {
                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });
                return key;
            }
            //初始化购物车信息
            , initCartMsg: function () {
                $.ajaxSettings.async = true;
                $.post("/shopCart/ShopCartList", {}, function (back) {
                    if (back.data.length > 0) {
                        $("#Cart").text("查看购物车");
                        $("#cart_num").text(back.data.length);
                    }

                    var ul = "";
                    for (var i = 0; i < back.data.length; i++) {
                        var size = back.data[i].up_size;
                        var sizeflag = new Boolean();
                        sizeflag = Number.isInteger(size);
                        var price = back.data[i].up_price;
                        if (sizeflag) {
                            size = size.toString() + ".0";
                        }

                        var priceflag = new Boolean();
                        priceflag = Number.isInteger(price);
                        if (priceflag) {
                            price = price.toString() + ".0";
                        }

                        var totalprice = back.data[i].up_totalprice;
                        var totalpriceflag = new Boolean();
                        totalpriceflag = Number.isInteger(totalprice);
                        if (totalpriceflag) {
                            totalprice = totalprice.toString() + ".0";
                        }

                        //alert(back.data[i].up_size+" "+back.data[i].up_price+" "+back.data[i].up_totalprice);
                        ul += "<ul class='order_lists' > <li class='list_chk'> <input class='ids' type='checkbox' name='product' value='" + JSON.stringify(back.data[i]) + "'></li> <li class='list_con'><div class='list_img'><a href='javascript:;'><img style='border: 1px solid #ebebeb' src=" + back.data[i].up_img + "></a></div> <div class='list_text'><a href='javascript:;' >" + back.data[i].up_name + "</a></div></li>  <li class='list_info'>  <p>规格：默认</p> <p>尺寸：<span>" + size + "</span></p><p>颜色：<span>" + back.data[i].up_color + "</span></p> </li> <li class='list_price'> <p class='price'>" + price + "</p>  </li> <li class='list_amount'> <div class='amount_box'><a href='javascript:;' class='reduce reSty'>-</a><input  type='text' readonly='readonly'  class='sum' value=" + back.data[i].up_num + "><span hidden='hidden' class='JsonInput'>" + JSON.stringify(back.data[i]) + "</span><a href='javascript:;' class='plus' >+</a></div>   </li>  <li class='list_sum'><p class='sum_price'>" + totalprice + " </p></li> <li class='list_op'> <p class='del'><a href='javaScript:;'class='delBtn' onclick='delOneCart(" + JSON.stringify(back.data[i]) + ")'>移除商品</a> </p>   </li> </ul>";
                    }
                    $(".order_content").html(ul);
                    //检验是否为1
                    btn.checkNum();

                    //3.购物数量加1 reduce
                    $(".amount_box .plus").each(function (i) {
                        this.onclick = function () {
                            var key = btn.loading();

                            var n = i;
                            //------------------------------//下面是增加商品数量
                            var temp = Number($(".sum:eq(" + n + ")").val());
                            var num = (temp + 1);
                            $(".sum:eq(" + n + ")").val(num);
                            //------------------------------// 计算总价格逻辑
                            //单价
                            var price = $(".price:eq(" + n + ")").text();

                            //总价格 sum_price
                            var sumPrice1 = parseFloat($(".sum_price:eq(" + n + ")").text()) + parseFloat(price);
                            //var sumPrice = sumPrice1.toFixed(2);
                            var sumPrice = Math.round(sumPrice1 * 100) / 100;
                            var sumPriceflag = new Boolean();
                            $(".sum_price:eq(" + n + ")").text(sumPrice);
                            //-----------------------------// 下面是 发送数据到 后端 修改购物车信息
                            var obj = $(".JsonInput:eq(" + n + ")").text();
                            $.ajaxSettings.async = true;
                            $.post("/shopCart/addOneShopCart", {
                                jsonMsgProduct: obj,
                                temp: 1,
                                nums: num,
                                sumPrice: sumPrice
                            }, function (back) {
                                if (back.status == 200) {
                                    btn.initCartMsg();
                                    layer.close(key);
                                    layer.msg("plus + 1")
                                } else if(back.status=="403") {
                                    layer.alert(back.msg, {icon: 4}, function (index) {
                                        location.href = "../../mall/toLogin";
                                    })
                                }else {
                                    layer.close(key);
                                    layer.msg("系统繁忙，请稍后再试！");
                                }
                            }).error(function () {
                                layer.msg("系统繁忙，请稍后再试！")
                                location.reload();
                            })
                        }
                    })
                    //3.购物数量减一 1 reduce
                    $(".reduce").each(function (i) {
                        this.onclick = function () {
                            var key = btn.loading();
                            var n = i;
                            //------------------------------//下面是减商品数量
                            var temp = Number($(".sum:eq(" + n + ")").val());
                            temp--;
                            if (temp == 1) {
                                $(".reduce:eq(" + n + ")").css("pointer-events", "none");
                            }

                            $(".sum:eq(" + n + ")").val(temp);
                            //------------------------------// 计算总价格逻辑
                            //单价
                            var price = $(".price:eq(" + n + ")").text();
                            //总价格 sum_price
                            var sumPrice1 = parseFloat($(".sum_price:eq(" + n + ")").text()) - parseFloat(price);
                            //四舍五入保留两位小数点
                            var sumPrice = sumPrice1.toFixed(2);

                            var sumPriceflag = new Boolean();

                            sumPriceflag = Number.isInteger(sumPrice);
                            if (sumPriceflag) {
                                sumPrice = sumPrice.toString() + ".0";
                            }
                            $(".sum_price:eq(" + n + ")").text(sumPrice);
                            //-----------------------------// 下面是 发送数据到 后端 修改购物车信息
                            var obj = $(".JsonInput:eq(" + n + ")").text();
                            $.post("/shopCart/addOneShopCart", {
                                jsonMsgProduct: obj,
                                temp: -1,
                                nums: temp,
                                sumPrice: sumPrice
                            }, function (back, xml) {
                                if (back.status == 200) {
                                    btn.initCartMsg();
                                    layer.close(key);
                                    layer.msg("reduce - 1");
                                }else if(back.status=="403") {
                                    layer.alert(back.msg, {icon: 4}, function (index) {
                                        location.href = "../../mall/toLogin";
                                    })
                                } else {
                                    layer.close(key);
                                    layer.msg("系统繁忙，稍后再试");
                                }
                            }).error(function () {
                                layer.close(key);
                                layer.msg("系统繁忙，稍后再试")
                            })
                        }
                    })


                    /*
                    * 购物车的结算，全选结算
                    * */
                    //复选框的全选
                    $("#checkbox_id").click(function () {
                        var n = 0;
                        if (this.checked) {
                            //全选
                            $(".order_content ul input[type='checkbox']").each(function (i) {
                                this.checked = true;
                                n++;
                            })

                            //结算商品个数
                            $(".piece_num").text(n);
                            //循环相加多个商品的总价
                            var counts = 0;
                            $(".sum_price").each(function (i) {
                                counts += parseFloat($(this).text());
                            })

                            //修改结算按钮颜色
                            if (n != 0) {
                                $(".calBtn input").css("background-color", "red");
                                $(".calBtn input").css("cursor", "pointer");
                                //解除按钮禁用
                                $(".calBtn input").attr("disabled", false);
                                //给表单添加属性url
                                $("#delShopForm").attr("action", "/shopCart/clearCart");

                                // if 已有选中的复选框，解除删除按钮禁用
                                $("#delShop").css("cursor", "pointer");
                                $("#delShop").attr("disabled", false);


                                //将总价写
                                $(".total_text").text(Math.round(counts * 100) / 100);
                            }
                        } else {
                            //反选
                            $(".order_content ul input[type='checkbox']").each(function (i) {
                                this.checked = false;
                            })

                            //修改结算按钮颜色
                            $(".calBtn input").css("background-color", "#B0B0B0");
                            $(".calBtn input").css("cursor", "not-allowed");
                            $(".calBtn input").attr("disabled", true);


                            //不给form表单url
                            $("#delShopForm").attr("action", "javascript:;");

                            // if 已有选中的复选框，解除删除按钮禁用
                            $("#delShop").css("cursor", "not-allowed");
                            $("#delShop").attr("disabled", true);


                            $(".total_text").text(0);
                            $(".piece_num").text(n);
                        }
                    })

                    //给每个复选框绑定一个click事件
                    $(".list_chk .ids").each(function (i) {
                        this.onclick = function () {
                            //获取选中的个数；
                            var n = btn.checkCheckbox();
                            $(".piece_num").text(n[0]);
                            $(".total_text").text(n[1]);

                            if (n[0] > 0) {
                                // if 已有选中的复选框，解除删除按钮禁用
                                $("#delShop").css("cursor", "pointer");
                                $("#delShop").attr("disabled", false);
                            } else {
                                // if 已有选中的复选框，解除删除按钮禁用
                                $("#delShop").css("cursor", "not-allowed");
                                $("#delShop").attr("disabled", true);
                            }
                        }
                    })
                })
            }
            //获取用户信息
            , initUserMsg: function () {
                var key = layer.msg("数据正在加载", {
                    icon: 16
                    , shade: 0.1 //0.1透明度的白色背景
                    , time: 20000
                });
                var uMsg = "";
                $.ajaxSettings.async = true;
                $.post("../../user/isLogin", function (back) {
                    if (back.status==200) {

                        uMsg = back.data;
                        $("#userHeadImgg").html("<img style='border-radius:" +
                            " 100px;border:1px solid white;position: relative;top: 5px' " +
                            "width='25' height='25' src=" + back.data.img + ">");

                        $("#userHeadImgg").css("pos", "15px");
                        layer.close(key);
                    }else if(back.status=="403") {
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                }).error(function () {
                    layer.close(key);
                })
                return uMsg;
            }
            //校验选中的复选框的个数 以及他的值；
            , checkCheckbox: function () {
                var n = 0;
                var counts = 0;
                var array = new Array();
                var str = "";
                $(".list_chk .ids").each(function (i) {
                    if (this.checked) {
                        //修改结算按钮颜色
                        $(".calBtn input").css("background-color", "red");
                        //解除结算按钮禁用
                        $(".calBtn input").css("cursor", "pointer");
                        $(".calBtn input").attr("disabled", false);

                        //解除删除按钮
                        $("#delShopForm").attr("action", "/shopCart/clearCartMany");
                        $("#delShop").css("cursor", "pointer");
                        //加总额数
                        counts += parseFloat($(".sum_price:eq(" + i + ")").text());
                        n++;
                        str += "-";
                    }
                    if (str.length == 0) {
                        $(".calBtn input").css("background-color", "#B0B0B0");
                        $(".calBtn input").css("cursor", "not-allowed");
                        $(".calBtn input").attr("disabled", true);

                    }
                })
                array.push(n, counts);
                return array;
            }
            //获取用户函数
            , getUserAddressMsg: function () {

                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });

                //获取用户信息 动态写出
                $.ajaxSettings.async = true;
                $.post("../../user/getUserAddressMsg", {}, function (back) {
                    if (back.status==200) {

                        var uMsg = "";
                        var u = JSON.stringify(back);
                        for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                            if (back.data[0].uAddressList[i].isDefault == 1) {
                                $("input[name='isDefault']").val(1);
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'checked/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updateAddress(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            } else {
                                uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updateAddress(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                            }
                        }
                        $("#userMsgUL").empty();
                        $("#userMsgUL").append(uMsg);
                        layer.close(key);
                    }else if(back.status=="403") {
                        layer.alert(back.msg, {icon: 4}, function (index) {
                            location.href = "../../mall/toLogin";
                        })
                    }
                })

            }
            //点击结算按钮//获取表单信息
            , Settlement: function () {

                //弹出收货地址
                var index1 = layer.open({
                    type: 1,
                    title: '确认收货地址',
                    skin: '', //加上边框
                    area: ['1000px', '540px'], //宽高
                    content: $("#detail")
                    , success: function () {
                        btn.getUserAddressMsg();
                    }
                });


                //点击新新增地址按钮
                $("#addAddress_btn").click(function () {
                    //先清空一遍原来的信息
                    $("input[name='recUser'").val("");
                    $("input[name='recTel'").val("");
                    $("input[name='address'").val("");
                    $("input[name='addressId'").val("");
                    //弹出一个层

                    //layer.close(layer.index);
                    var addIndex = layer.open({
                        type: 1,
                        title: '添加收货地址',
                        skin: '', //加上边框
                        shade: false, //禁止遮罩层
                        move: false, //不允许当前用户拖动当前窗口
                        area: ['1000px', '540px'], //宽高
                        content: $("#addAdds_Div"),
                        cancel: function (index, layero) {

                        }
                    })
                    //在填写表单之前禁用提交按钮
                    $("#btn_addAddress").attr("disabled", "disabled");
                    $("#btn_addAddress").css("cursor", "not-allowed");
                    $("#btn_addAddress").css("background-color", "gray");
                    //给input 框绑定失焦事件
                    $(".isEmpty").each(function (i) {
                        this.onblur = function () {
                            var arr = btn.checkInputIsNull(i);
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


                    //获取新增地址表单信息,并且持久化地址
                    $("#btn_addAddress").off().on('click', function () {

                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.1
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

                        $.post("/user/addAddress", {
                            recUser: recUser,
                            address: ad,
                            recTel: recTel
                        }, function (back) {
                            if (back.status == 200) {
                                layer.alert("添加成功！",{icon:1}, function (index) {
                                    //关闭当前弹出层
                                    layer.close(key);
                                    layer.close(index);
                                    layer.close(addIndex);
                                    btn.getUserAddressMsg();
                                });
                            }else if(back.status=="403") {
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            } else {
                                layer.close(key);
                                layer.msg("添加失败！");
                            }

                        });
                    })
                })


                //计算总额
                var n = 0;
                var counts = 0;
                $(".list_chk .ids").each(function (i) {
                    if (this.checked) {
                        n++;
                        //加总额数
                        counts += parseFloat($(".sum_price:eq(" + i + ")").text());
                    }
                })
                //将每个商品信息拼接一个字符窜
                if (n > 0) {
                    var str = "";
                    $(".list_chk .ids").each(function (i) {
                        if (this.checked) {
                            //字符窜拼接
                            str += $(this).val() + "===";
                        }
                    })
                    //提交订单 按钮
                    $("#submit_order").off().on('click', function () {

                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.1
                            , time: 20000
                        });

                        //点击一次禁用按钮
                        $("#submit_order").attr('disabled', true);

                        //拿到收货地址id
                        var addressId = $("#userMsgUL li input[type='radio']:checked").val();


                        $.ajaxSettings.async = true;
                        $.post("/order/creatOrder", {product: str, addressId: addressId}, function (back) {
                            //alert(JSON.stringify(back));
                            var orderId = back.data;
                            if (back.status == 200) {
                                location.href = "/payFor/money/" + orderId;
                            } else if(back.status=="403") {
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            }else {
                                //location.reload();
                                $("#submit_order").attr('disabled', false);
                                layer.msg("请检查是否有商品库存已存在不足！");
                            }
                        }).error(function (back) {
                            layer.close(key);
                            layer.msg("系统繁忙，请稍后再试！");
                            $("#submit_order").attr('disabled', false);
                            //alert(back.responseJSON.message);
                        })
                    })
                }
            }
            //校验输入框是否有空
            , checkInputIsNull: function (i) {
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
            //取消提示框
            , dialogclose: function () {
                $(".my_model").hide("2000");
            }
            //删除多个
            , delShopInput: function () {
                var n = 0;
                $(".list_chk .ids").each(function (i) {
                    if (this.checked) {
                        n++;
                    }
                })
                if (n > 0) {
                    layer.confirm('真的删除行么',{icon:3}, function (index) {
                        var key=btn.loading();
                        layer.close(index);
                        var str = "";
                        $(".list_chk .ids").each(function (i) {
                            if (this.checked) {
                                //把每条商品拼接 称一个字符窜
                                str += $(this).val() + "===";
                            }
                        })
                        $.ajaxSettings.async=true;
                        $.post("/shopCart/clearCartMany", {product: str}, function (back) {
                            if (back.status == 200) {
                                btn.initCartMsg();
                                layer.close(key);
                            }else if(back.status=="403") {
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            }else{
                                layer.close(key);
                                layer.msg("删除失败,稍后再试试！");
                            }
                        }).error(function (b) {
                            layer.msg("系统繁忙，请稍后再试");
                            console.log(JSON.stringify(b))
                        })
                    })
                } else {
                    layer.alert("请选择要删除的商品",{icon:3});
                }
            }
            //检查数量是否为一
            , checkNum: function (i) {
                $(".sum").each(function (j) {
                    if ($(this).val() == 1) {
                        $(".reduce:eq(" + j + ")").css("pointer-events", "none");
                    } else {
                        $(".reduce:eq(" + j + ")").css("pointer-events", "");
                    }
                })
            }
        }


        //初始化 加载购物车信息
        btn.initCartMsg();
        //初始化获取用户信息
        var user = btn.initUserMsg();
        //点击结算按钮
        $("#SettlementBtn").click(function () {
            btn.Settlement();
        })

        //初始化检验if 数量为 1 禁用按钮 页面加载执行
        btn.checkNum();

        //点击删除多个
        $("#delShop").click(function () {
            btn.delShopInput();
        })


    })

})

//删除单个购物车的信息
function delOneCart(obj) {
    layui.use(['layer'], function () {
        var layer = layui.layer;
        layer.confirm('真的删除行么',{icon:3}, function (index) {
            var id = JSON.stringify(obj);
            $.ajax({
                type: "POST",
                data: {ids: id},
                url: "/shopCart/clearCart",
                dataType: "json",
                async:true,
                success: function (back) {
                    if (back.data > 0) {
                        location.reload();
                        layer.close(index)
                    } else {
                        layer.msg("系统繁忙，稍后再试");
                    }
                },
                error: function (data) {
                    layer.msg("系统繁忙，稍后再试");
                }
            });
        });

    })
}

//修改用户收货地址的按钮 弹出层
function updateAddress(msg) {
    layui.use(['layer'], function () {
        var layer = layui.layer;
        //将当前条的数据 写到输入框中
        $("input[name='recUser'").val(msg.recUser);
        $("input[name='recTel'").val(msg.recTel);
        $("input[name='address'").val(msg.address);
        $("input[name='addressId'").val(msg.addressId);
        //修改原来 添加按钮 改为修改
        $("#btn_addAddress").val("修改");

        //将值写到框后 弹出修改div框

        var indexNum = layer.open({
            type: 1,
            title: '下单信息',
            skin: '', //加上边框
            area: ['1000px', '540px'], //宽高
            content: $("#addAdds_Div"),
        });

        //执行修改的按钮
        $("#btn_addAddress").off().on('click', function () {

            var key = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.1
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
                addressId: addressId,
                isDefault: msg.isDefault
            }, function (back) {
                if (back.status == 200) {
                    layer.alert("修改成功！",{icon:1}, function (index) {
                        //舒心地址列表
                        //关闭加载层
                        layer.close(key);
                        //关闭当前层
                        layer.close(index);
                        layer.close(indexNum);
                        //添加成功后 刷新数据
                        getUserAddressMsg();
                    })
                } else if(back.status=="403") {
                    layer.alert(back.msg, {icon: 4}, function (index) {
                        location.href = "../../mall/toLogin";
                    })
                }else {
                    layer.close(key);
                    layer.msg("修改失败！");
                }

            }).error(function (back) {
                layer.msg("系统繁忙，请稍后再试");
            })
        })
    })
}

function getUserAddressMsg() {
    layui.use(['layer'], function () {
        var layer = layui.layer;

        var key = layer.msg('数据加载中', {
            icon: 16
            , shade: 0.1
            , time: 20000
        });


        //获取用户Id,根据id 拿到 用户的 收货地址
        //获取用户信息 动态写出
        $.ajaxSettings.async = true;
        $.post("../../user/getUserAddressMsg", {}, function (back) {
            if (back.status==200) {

                var uMsg = "";
                var u = JSON.stringify(back);
                for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                    if (back.data[0].uAddressList[i].isDefault == 1) {
                        $("input[name='isDefault']").val(1);
                        uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'checked/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updateAddress(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                    } else {
                        uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updateAddress(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                    }
                }
                $("#userMsgUL").empty();
                $("#userMsgUL").append(uMsg);
                layer.close(key);
            }else if(back.status=="403") {
                layer.alert(back.msg, {icon: 4}, function (index) {
                    location.href = "../../mall/toLogin";
                })
            }
        }).error(function () {
            layer.msg("系统繁忙，请稍后再试");
        })
    })
}

