layui.use('layer', function () {
    var layer = layui.layer;

    var defaultID = "";//默认地址的Id
    var AdressList;
    var btn = {
        //设为默认默认地址逻辑
        updateDefault: function (oldId, oldDefault, currId, currDefault, i) {
            var key = layer.msg("数据正在加载", {
                icon: 16
                , shade: 0.1 //0.1透明度的白色背景
                , time: 20000
            });
            //ajax异步
            $.ajaxSettings.async = true;
            var isOk = "";
            //先将当前默认的地址设为不默认 然后将选中默认
            $.post("../../user/updateAddress", {
                addressId: oldId,
                isDefault: oldDefault
            }, function (data) {
                if (data.status == 200) {
                    $.post("../../user/updateAddress", {addressId: currId, isDefault: currDefault}, function (data) {
                        if (data.status == 200) {
                            layer.close(key);
                            btn.ininAddress();
                        }
                    })
                } else if (data.status == "403") {
                    layer.alert(data.msg, {icon: 4}, function (index) {
                        location.href = "../../mall/toLogin";
                    })
                } else {
                    layer.close(key);
                    layer.msg("稍后再试");
                }
            }).error(function (back) {
                layer.close(key);
                layer.msg("系统繁忙，请稍后再试");
            });
        }
        //修改当前前收货地址
        , updateAddress: function (msg) {
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
                    area: ['800px', '500px'], //宽高
                    content: $("#addAdds_Div"),
                });

                //执行修改的按钮
                $("#btn_addAddress").off().on('click', function () {
                    //获取新增地址表单信息,并且持久化地址

                    var addressId = msg.addressId;
                    var recUser = $("input[name='recUser'").val();
                    var recTel = $("input[name='recTel'").val();
                    var address = $("input[name='address'").val();

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
                            layer.alert("修改成功！", {icon: 1}, function (index) {
                                layer.close(indexNum);
                                layer.close(index);
                                layer.close(key);
                                btn.ininAddress();
                            })
                        } else if (back.status == "403") {
                            layer.alert(back.msg, {icon: 4}, function (index) {
                                location.href = "../../mall/toLogin";
                            })
                        } else {
                            layer.msg("修改失败！");
                        }

                    }).error(function (back) {

                        layer.msg("系统繁忙，请稍后再试！");
                    })

                })
            })
        }
        //添加地址
        , addAddress: function () {
            //先清空一遍原来的信息
            $("#div_Address input[type='text']").each(function () {
                $(this).val($(this).defaultValue);
            })
            var indexNum = layer.open({
                type: 1,
                title: '添加地址',
                skin: '', //加上边框
                shade: false, //禁止遮罩层
                move: false, //不允许当前用户拖动当前窗口
                area: ['800px', '500px'], //宽高
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
                var recUser = $("input[name='recUser'").val();
                var recTel = $("input[name='recTel'").val();
                var address = $("input[name='address'").val();

                var ad = address.replace(/\s|\xA0/g, "");

                var key = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.1
                    , time: 20000
                });

                $.ajaxSettings.async = true;
                $.post("/user/addAddress", {
                    recUser: recUser,
                    address: ad,
                    recTel: recTel
                }, function (back) {
                    if (back.status == 200) {
                        layer.alert("添加成功！", {icon: 1}, function (index) {
                            layer.close(indexNum);
                            layer.close(index);
                            layer.close(key);
                            btn.ininAddress();
                        })
                    } else {
                        layer.close(key);
                        layer.msg("添加失败！");
                    }
                }).error(function (back) {
                    layer.msg("系统繁忙，请稍后再试");
                })
            })
        }
        //点击删除
        , deleteaddres: function (IsId, isdfault) {
            layui.use('layer', function () {
                var layer = layui.layer;
                if (isdfault == 1 || IsId == defaultID) {
                    layer.msg("不能删除默认地址");
                } else {
                    layer.confirm("确定删除么？", {icon: 3}, function () {

                        var key = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.1
                            , time: 20000
                        });
                        $.ajaxSettings.async = true;
                        $.post("/user/delAddresById", {addressId: IsId}, function (back) {
                            if (back.status == 200) {
                                btn.ininAddress();
                                layer.close(key);
                                btn.ininAddress();
                                //layer.msg("已删除");
                            } else if (back.status == "403") {
                                layer.alert(back.msg, {icon: 4}, function (index) {
                                    location.href = "../../mall/toLogin";
                                })
                            } else {
                                layer.msg("稍后再试");
                            }
                        }).error(function (back) {
                            layer.close(key);
                            layer.msg("系统繁忙，请稍后再试！");
                            console.log(back.responseJSON.message)
                        })
                    })
                }
            })
        }
        //初始化地址信息
        , ininAddress: function () {

            var key = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.1
                , time: 20000
            });

            $.ajaxSettings.async = true;//开启异步
            $.post("/user/getUserAddressMsg", function (back) {
                if (back.status == 200) {


                    AdressList = back.data[0].uAddressList;
                    var div = "";
                    //onclick='updateAdd(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' onclick='deleteaddres(this," + back.data[0].uAddressList[i].addressId + "," + back.data[0].uAddressList[i].isDefault + ")'
                    for (var i = 0; i < back.data[0].uAddressList.length; i++) {
                        if (back.data[0].uAddressList[i].isDefault == 1) {

                            defaultID = back.data[0].uAddressList[i].addressId;

                            div += "<div class='layui-col-md3 addressmsg'>" +
                                "<div class='divOne'>" +
                                "<div class='divTwo'style='width: 250px;height: 17px'>" +
                                "<text class='default' style='background-color: #E45D5D' class='isdefault'>默认</text>" +
                                "<text><a href='javascript:;' class='updateBtn' ><i class='layui-icon layui-icon-edit'></i></a>&nbsp;&nbsp;" +
                                "<a href='javascript:;'class='delBtn' ><i class='layui-icon layui-icon-delete'></i></a></text>" +
                                "</text></div><p><span>收货地址：</span>" + back.data[0].uAddressList[i].address + "</p>" +
                                "<p><span>收件人</span>：" + back.data[0].uAddressList[i].recUser + "</p><p>" +
                                "<span>联系电话:</span>" + back.data[0].uAddressList[i].recTel + "</p></div>" +
                                "</div>";
                        } else {
                            div += "<div class='layui-col-md3 addressmsg'>" +
                                "<div class='divOne'>" +
                                "<div class='divTwo' style='width: 250px;height: 17px'>" +
                                "<text class='default'>默认</text><text><a href='javascript:;' class='updateBtn'><i class='layui-icon layui-icon-edit'></i></a>&nbsp;&nbsp;" +
                                "<a href='javascript:;' class='delBtn'><i class='layui-icon layui-icon-delete'></i></a></text>" +
                                "</text></div><p><span>收货地址：</span>" + back.data[0].uAddressList[i].address + "</p><p>" +
                                "<span>收件人</span>：" + back.data[0].uAddressList[i].recUser + "</p><p>" +
                                "<span>联系电话:</span>" + back.data[0].uAddressList[i].recTel + "</p></div>" +
                                "</div>";
                        }
                        //uMsg += "<li style='padding: 5px 0'><label><input type='radio' style='cursor: pointer' name='addressId' value='" + back.data[0].uAddressList[i].addressId + "'/><span style='padding: 0 15px'>" + back.data[0].uAddressList[i].address + "</span><span>（" + back.data[0].uAddressList[i].recUser + " 收）</span><span>" + back.data[0].uAddressList[i].recTel + "</span><span style='margin-left: 20px'><a class='updateAddress' onclick='updateAddress(" + JSON.stringify(back.data[0].uAddressList[i]) + ")' href='javascript:;'>[ 修改 ]</a></span></label></li>";
                    }
                    //最后一个添加按钮
                    var lastAdd = "<div id='lastAdd' class='layui-col-md3 addressmsg'><div style='background-color: #ebebeb'><p style='margin-top: 10%;margin-left: 26%;font-size: 100px;color: #666666;'>＋</p></div></div>"

                    $("#u").html("<div class='layui-row layui-col-space10'>" + div + lastAdd + "</div>");
                    layer.close(key);
                    // 鼠标悬停
                    $(".addressmsg").each(function (i) {
                        this.onmouseover = function () {
                            //$(this).css("padding", "0 0px 15px 0px");
                            $(".addressmsg div div text:nth-child(2):eq(" + i + ")").show();
                        }
                        this.onmouseout = function () {
                            $(this).css("padding", "0 5px 20px 5px");
                            $(".addressmsg div div text:nth-child(2):eq(" + i + ")").hide();
                        }
                    })

                    //点击设为默认
                    $(".default").each(function (i) {

                        this.onmouseover = function () {
                            $(this).css("border", "1px solid #E45D5D");
                        }
                        this.onmouseout = function () {
                            $(this).css("border", "none");
                        }


                        this.onclick = function () {
                            //将当前点击的设为默认
                            //defaultID:当前默认地址，0：设为不默认； back.data[0].uAddressList[i].addressId：当前点击地址Id;1设为默认
                            btn.updateDefault(defaultID, 0, back.data[0].uAddressList[i].addressId, 1, i);
                        }
                    })

                    //添加按钮
                    $("#lastAdd").click(function () {
                        btn.addAddress();
                    })


                    //点击修改
                    $(".updateBtn").each(function (i) {
                        this.onclick = function () {
                            //alert(AdressList[i].addressId+"，"+AdressList[i].isDefault);
                            btn.updateAddress(AdressList[i]);
                        }
                    })


                    //点击删除
                    $(".delBtn").each(function (i) {
                        this.onclick = function () {
                            //alert(AdressList[i].addressId+"，"+AdressList[i].isDefault);
                            btn.deleteaddres(AdressList[i].addressId, AdressList[i].isDefault);
                        }
                    })
                } else if (back.status == "403") {
                    layer.alert(back.msg, {icon: 4}, function (index) {
                        location.href = "../../mall/toLogin";
                    })
                }
            }).error(function () {
                layer.msg("系统繁忙，请稍后再试");
            })
        }
    }
    btn.ininAddress();
})
