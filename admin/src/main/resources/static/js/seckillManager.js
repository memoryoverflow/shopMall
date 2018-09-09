$(function () {
    $.post("/seckill/getSeckillExplainMsg", {}, function (data) {
        $("#test1 img:eq(0)").attr("src", data.data.img1);
        $("#test1 img:eq(1)").attr("src", data.data.img2);
        $("#test1 img:eq(2)").attr("src", data.data.img3);
        $(".miaoshu #name").text(data.data.name);
        $(".miaoshu #explain1").text(data.data.explain1);
        $(".miaoshu #explain2").text(data.data.explain2);
        $(".miaoshu #explain3").text(data.data.explain3);
    }).error(function (data) {
        layer.msg("系统异常，图片加载失败！");
        //console.log(JSON.stringify(data));
    })
})
// layui方法
layui.use(['table', 'layer', 'upload', 'form'], function () {
    // 操作对象
    var table = layui.table,
        upload = layui.upload
        , layer = layui.layer
        , form = layui.form
        , $ = layui.jquery;

    var btn = {
        addSeckillExplain: function () {
            var full = layer.open({
                type: 1
                , anim: 5
                , shadeClose: true //开启遮罩关闭
                , title: '发布活动商品'
                , area: ['800px', '300px']
                , content: $("#addSeckillExplain")
                , success: function () {
                    $.post("/mall/getProductAllId", {}, function (data) {
                        // var option = "";
                        // var optionOne = "<option value=''></option>";
                        //
                        // for (var i = 0; i < data.data.length; i++) {
                        //     option += "<option value=" + data.data[i].productId + ">" + data.data[i].productId + "</option>";
                        // }
                        // $("#productIds").append(optionOne + option);
                        // form.render();//需重新渲染
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }).error(function () {
                        layer.msg("获取商品Id失败！");
                    })
                }
            })
            layer.full(full);
            //验证表单非空提交
            $("#addSeckillExplain #submit").click(function () {
                var node = $("#addSeckillForm");
                var isNull = btn.checkFormIsEmpty(node);
                if (isNull.length > 0) {
                    //表单存在空项
                    layer.msg("表单不允许有空！", {icon: 2, anim: 6})
                } else {
                    var index = btn.loading();
                    var formData = new FormData(document.getElementById("addSeckillForm"));//表单id
                    $.ajax({
                        url: '/seckill/addSeckillExplain',
                        type: 'POST',
                        data: formData,
                        async: true,
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (result) {
                            //layer.alert(JSON.stringify(result))
                            if (result.status == 200) {
                                // layer.close(index);
                                // layer.alert("已发布", {
                                //     yes: function (index) {
                                //         layer.close(index);
                                //         layer.close(full);
                                //         location.reload();
                                //     }
                                // });
                                layer.alert("演示模式，不允许操作！",{icon:2})
                            } else {
                                if (result.msg == 'false') {
                                    layer.alert("您已开启一个活动产品，如要更换直接修改即可");
                                } else {
                                    layer.alert("图片上传失败，请重新发布！");
                                }
                            }
                        },
                        error: function () {
                            layer.msg("系统繁忙，请稍后再试！", {time: 2000});
                            layer.close(index);
                        }
                    });
                }
            })


        }
        //更换logo
        , changLogo: function () {
            //更换单张
            //$.post("/seckill/getSeckillExplainMsg", {}, function (data) {})
            var img1 = $("#test1 img:eq(0)").attr("src");
            var img2 = $("#test1 img:eq(1)").attr("src");
            var img3 = $("#test1 img:eq(2)").attr("src");
            var id = $(".pruductIdTxt").text();
            var listImg = img1.substring(27, img1.length) + "," + img2.substring(27, img2.length) + "," + img3.substring(27, img3.length);

            var full = layer.open({
                type: 1
                , anim: 5
                , shadeClose: true //开启遮罩关闭
                , title: '密码重置'
                , area: ['800px', '500px']
                , content: $('#changLOGODIV')
            })
            layer.full(full);

            //更换logo 更换全部
            $("#uapdateSeckillImgBtn").click(function ()  {
                var isNull = "";
                $("#updateSeckillImgForm input").each(function () {
                    if ($(this).val().length == 0) {
                        isNull += "=";
                    }
                })
                if (isNull.length > 0) {
                    //layer.alert(isNull)
                    layer.msg("请选择三张图片", {icon: 2, anim: 6})
                } else {
                    var key = btn.loading();
                    var formData = new FormData(document.getElementById("updateSeckillImgForm"));//表单id
                    formData.append("id", id);
                    formData.append("imgOldName", listImg);
                    $.ajax({
                        url: '/seckill/changeLogo',
                        type: 'POST',
                        data: formData,
                        async: true,
                        cache: false,
                        contentType: false,
                        processData: false,
                        success: function (result) {
                            //layer.alert(JSON.stringify(result))
                            if (result.status == 200) {
                                // layer.close(key);
                                // layer.alert("更换成功", {
                                //     yes: function (index) {
                                //         location.reload();
                                //     }
                                // })
                                layer.alert("演示模式，不允许操作！",{icon:2})
                            } else {
                                layer.close(key);
                                layer.alert("图片上传失败，请重新发布！");
                            }
                        },
                        error: function () {
                            layer.msg("系统繁忙，请稍后再试！", {time: 2000});
                            layer.close(index);
                        }
                    });
                }
            })


            //校验是否选购三个图片


            //选完文件后不自动上传
            upload.render({
                elem: '#one'
                , url: '/seckill/changeLogo'
                , auto: false
                //,multiple: true
                , bindAction: '#one1'
                ,accept:'images'
                ,acceptMime: 'image/*'
                , choose: function (obj) {
                    //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
                    obj.preview(function (index, file, result) {
                        console.log(index); //得到文件索引
                        console.log(file); //得到文件对象
                        console.log(result); //得到文件base64编码，比如图片
                        $(".one").html('<img src=' + result + '>');
                    });
                }
                , data: {imgOldName: img1.substring(27, img1.length), id: id}
                , done: function (res) {
                    // layer.alert("更换成功", {
                    //     yes: function (index) {
                    //         location.reload();
                    //     }
                    // })
                    // console.log(res)
                    layer.alert("演示模式，不允许操作！",{icon:2})
                }
            });
            upload.render({
                elem: '#two'
                , url: '/seckill/changeLogo'
                , auto: false
                ,accept:'images'
                ,acceptMime: 'image/*'
                //,multiple: true
                , bindAction: '#two2'
                , choose: function (obj) {
                    //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
                    obj.preview(function (index, file, result) {
                        console.log(index); //得到文件索引
                        console.log(file); //得到文件对象
                        console.log(result); //得到文件base64编码，比如图片
                        $(".one").html('<img src=' + result + '>');
                    });
                }
                , data: {imgOldName: img2.substring(27, img2.length), id: id}
                , done: function (res) {
                    // layer.alert("更换成功", {
                    //     yes: function (index) {
                    //         location.reload();
                    //     }
                    // })
                    // console.log(res)
                    layer.alert("演示模式，不允许操作！",{icon:2})
                }
            });
            upload.render({
                elem: '#three'
                , url: '/seckill/changeLogo'
                , auto: false
                ,accept:'images'
                ,acceptMime: 'image/*'
                , bindAction: '#three3'
                , choose: function (obj) {
                    //预读本地文件，如果是多文件，则会遍历。(不支持ie8/9)
                    obj.preview(function (index, file, result) {
                        console.log(index); //得到文件索引
                        console.log(file); //得到文件对象
                        console.log(result); //得到文件base64编码，比如图片
                        $(".one").html('<img src=' + result + '>');
                    });
                }
                , data: {imgOldName: img1.substring(27, img3.length), id: id}
                , done: function (res) {
                    // layer.alert("更换成功", {
                    //     yes: function (index) {
                    //         location.reload();
                    //     }
                    // })
                    // console.log(res)
                    layer.alert("演示模式，不允许操作！",{icon:2})
                }

            });

        }
        , changeDetail: function () {
            var name = $(".miaoshu #name").text();
            var e1 = $(".miaoshu #explain1").text();
            var e2 = $(".miaoshu #explain2").text();
            var e3 = $(".miaoshu #explain3").text();
            var id = $(".pruductIdTxt").text();
            var arr = [name, e1, e2, e3];
            layer.open({
                type: 1
                , anim: 5
                , shadeClose: true //开启遮罩关闭
                , title: '修改封面描述文字'
                , area: ['700px', '400px']
                , content: $("#updateExplainMsgDiv")
                , success: function (index) {
                    $("#updateExplainForm input:lt(4)").each(function (i) {
                        $(this).val(arr[i]);
                    })
                }
            })
            $("#updateExplainForm #submit").click(function () {
                var isNull = ""
                $("#updateExplainForm input:lt(4)").each(function (i) {
                    if ($(this).val().length == 0) {
                        isNull += "=";
                    }
                })
                if (isNull.length > 3) {
                    layer.msg("至少修改一个", {icon: 2, anim: 6});
                } else {
                    var key=btn.loading();
                    var data =$("#updateExplainForm").serialize();
                    $.post("/seckill/updateSeckillExplain", data, function (data) {
                        // layer.close(key);
                        // layer.alert("已保存", function (index) {
                        //     location.reload();
                        // });
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    })
                }
            })
        }
        , settingStock: function () {

            layer.prompt({
                formType: 2,
                value: $("#stcok").val(),
                title: '请输入值',
                area: ['100px', '50px'] //自定义文本域宽高
            }, function (value, index, elem) {

                var id = $(".pruductIdTxt").text();

                var regPos = /^\d+$/; // 非负整数
                if (!regPos.test(value)) {
                    layer.msg("请输入正整数", {icon: 2, anim: 6, time: 500});
                } else {
                    var loding = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.3
                        , time: 20000
                    });
                    $.post("seckill/updateSeckillProductStock", {num: value, id: id}, function (data) {
                        // $("#stcok").val(value);
                        // layer.close(loding);
                        // layer.msg("已保存");
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }).error(function () {
                        layer.msg("系统繁忙，请稍后再试！");
                    })
                }
            });
        }
        , changeProductBtn: function () {
            $("#p_Ids").empty();

            var id = $(".pruductIdTxt").text();
            layer.open({
                type: 1
                , anim: 5
                , shadeClose: true //开启遮罩关闭
                , title: '密码重置'
                , area: ['800px', '500px']
                , content: $("#changeSeckillProductDIV")
                , success: function () {
                    var key = btn.loading();
                    $.post("/mall/getProductAllId", {}, function (data) {
                        // var option = "";
                        // var optionOne = "<option value=''></option>";
                        // for (var i = 0; i < data.data.length; i++) {
                        //     if (id == data.data[i].productId) {
                        //         option += "<option  selected value=" + data.data[i].productId + ">" + data.data[i].productId + "</option>";
                        //     } else {
                        //         option += "<option value=" + data.data[i].productId + ">" + data.data[i].productId + "</option>";
                        //     }
                        // }
                        // $("#p_Ids").append(optionOne + option);
                        // form.render();//需重新渲染
                        // layer.close(key);
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }).error(function () {
                        layer.msg("获取商品Id失败！");
                    })
                }
                , btn: ['确定']
                , yes: function (index, layero) {
                    //按钮【确定】的回调'
                    var val = $("#p_Ids  option:selected").text();
                    var key = btn.loading();
                    $.post("/seckill/updateSeckillProductId", {id: val}, function (data) {
                        // layer.close(key);
                        // layer.alert("已保存", function (index) {
                        //     location.reload();
                        // });
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }).error(function () {
                        layer.msg("系统繁忙，请稍后再试！");
                    })
                }
            })


        }
        , settingQuart: function () {
        }
        , loading: function () {
            var key = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.3
                , time: 20000
            });
            return key;
        }
        , checkFormIsEmpty: function (node) {
            var isNull = "";
            var formName = node.attr("name");
            for (var i = 0; i < document.addSeckillForm.elements.length - 1; i++) {
                if (document.addSeckillForm.elements[i].value.length == 0) {
                    isNull += "=";
                }
            }
            return isNull;
        }
    }


    $("#addSeckillExplainBtn").click(function () {
        btn.addSeckillExplain();
    })
    $("#changLogoBtn").click(function () {
        btn.changLogo();
    })

    $("#changDetailBtn").click(function () {
        btn.changeDetail();
    })

    $("#settingStockBtn").click(function () {
        btn.settingStock();
    })

    $("#changeProductBtn").click(function () {
        btn.changeProductBtn();
    })

    $("#quartBtn").click(function () {
        btn.settingQuart();
    })


});
