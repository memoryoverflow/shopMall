$(function () {
    //表格行 悬停 变色
    layui.use(['table', 'form', 'upload'], function () {
        var table = layui.table,
            form = layui.form,
            upload = layui.upload,
            $ = layui.$;

        //loading层
        function loding() {
            //loading层
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.5
                , time: 20000
            });
            return loding;
        }

        var loading = loding();
        //方法级渲染
        var tableIns = table.render({
            elem: '#LAY_table_user'
            , url: 'mall/productList'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true}
                , {field: 'productId', title: 'ID', sort: true, align: 'center', fixed: true, width: 80}
                , {field: 'name', title: '商品名', edit: 'text', event: 'pname', align: 'center', width: 240}
                , {field: 'price', title: '单价', edit: 'text', sort: true, align: 'center', width: 100}
                , {field: 'num', title: '库存', edit: 'text', sort: true, align: 'center', width: 100}
                , {field: 'describes', title: '商品描述', event: 'describes', edit: 'text', align: 'center', width: 345}
                , {field: 'isdel', title: '状态', align: 'center', width: 130, templet: '#isdelStatus'}
                , {field: 'imgMain', title: '主图片', align: 'center', event: 'editImg', width: 100, templet: '#img'}
                , {title: '操作', fixed: 'right', width: 137, align: 'center', toolbar: '#barDemo'}
            ]]
            , id: 'testReload'
            , page: true
            , height: 'full-200'
            , skin: 'nob'
            , done: function (res, curr, count) { //数据加载完后的回调函数
                layer.close(loading);
            }
        });


        //数据重载
        function tabReload() {
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.5
                , time: 10000
            });
            tableIns.reload({
                url: 'mall/productList'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {}
                , done: function (res, curr, count) { //数据加载完后的回调函数
                    layer.close(loding);
                }
            });
        }

        /*---------------------------------------*/
        //刷新按钮
        $("#reloadTab").click(function () {
            tabReload();
        })
        /*---------------------------------------*/
        //表格重载 搜索分页
        $('.main_top #searChBtn').off().on('click', function () {
            var lodingindex = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.5
                , time: 20000
            });
            var id = $("#productSerachForm #productId").val();
            var name = $("#productSerachForm #name").val();
            var isdel = $("#productSerachForm #isdel").val();
            //alert(isdel)
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.productSerachForm.elements.length - 1; i++) {
                if (document.productSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            if (str.length > 0) {
                //执行重载
                var loding = tableIns.reload({
                    url: 'mall/productList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        productId: id
                        , name: name
                        , isdel: isdel
                    }
                    , done: function (res, curr, count) { //数据加载完后的回调函数
                        layer.close(lodingindex);
                    }
                });
            } else {
                layer.msg("请输入条件", {icon: 2, time: 1000})
            }
        });


        //顶部的删除 和添加按钮
        $('.main_content .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        var $ = layui.$, active = {
            //添加操作
            getCheckData: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                //layer.alert(JSON.stringify(data));
                var addmsg = layer.open({
                    type: 1
                    , shadeClose: true //开启遮罩关闭
                    , title: '商品发布'
                    , area: ['800px', '300px']
                    , content: $("#addProduct")
                    , maxmin: true
                });
                layer.full(addmsg);

                //点击提交
                $("#formDemo").click(function () {

                    //loading层
                    var loding = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.5
                        , time: 20000
                    });

                    var str = "";
                    //实例化编辑器
                    var ue = UE.getEditor("UEditorTxt");
                    var ue2 = UE.getEditor("UEditorTxtDescribe");


                    /*开始处理表单*/


                    //将编辑器的内容写到表单中
                    $("#detail").val(ue.getContent());
                    $("#describes").val(ue2.getContent());


                    var color = 0, size = 0, img = 0;


                    //遍历除了图片和复选框的input
                    $("#addProductForm .inputV").each(function () {
                        if ($(this).val().length == 0) {
                            str += "+";
                        }
                    })
                    //遍历尺寸的复选框
                    $("#addProductForm .sizeDAXIAO input[type='checkbox']").each(function () {
                        if (this.checked == true) {
                            size++;
                        }
                    });

                    //遍历颜色的复选框
                    $("#addProductForm .ul_color input[type='checkbox']").each(function () {
                        if (this.checked == true) {
                            color++;
                        }
                    });

                    //遍历图片的input
                    $("#addProductForm .FileList input[type='file']").each(function () {
                        if ($(this).val().length > 0) {
                            img++;
                        }
                    });

                    if (str.length == 0 && color > 0 && size > 0 && img > 0) {
                        if (img >= 2) {
                            var formData = new FormData(document.getElementById("addProductForm"));//表单id
                            $.ajax({
                                url: '/mall/addProduct',
                                type: 'POST',
                                data: formData,
                                async: true,
                                cache: false,
                                contentType: false,
                                processData: false,
                                success: function (result) {
                                    //layer.alert(JSON.stringify(result))
                                    if (result.status == 200) {
                                        layer.alert("商品已发布",{icon:1}, function (index) {
                                            layer.close(loding);
                                            layer.close(addmsg);
                                            var lodin = layer.msg('数据加载中', {
                                                icon: 16
                                                , shade: 0.5
                                                , time: 10000
                                            });
                                            tabReload(lodin);
                                        });
                                        //恢复表单默认值
                                    }
                                },
                                error: function () {
                                    layer.msg("系统繁忙，稍后再试！", {time: 2000});
                                    layer.close(loding);
                                }
                            });
                            //清空当前div的表单内容
                            debugger;
                            $("#addProductForm input[type='text']").each(function () {
                                $(this).val($(this).defaultValue);

                            })
                            $("#mimgMainDiv").empty().html("<p class='layui-icon'></p>");
                            $(".minImg").empty();
                            $(".midImg").empty();
                            $(".maxImg").empty().html("<p class='layui-icon'></p>");
                        } else {
                            layer.close(loding);
                            layer.alert("至少选择两个商品展示图片", {icon: 2})
                        }
                    } else {
                        layer.close(loding);
                        layer.alert("表单不允许有空项", {icon: 2})
                    }
                })

            }
            //删除操作
            , getCheckLength: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                if (data.length == 0) {
                    layer.alert("请至少选择一条", {icon: 5})
                } else {
                    layer.confirm('真的下架么',{icon:3}, function (index) {
                        var p_Id = new Array();
                        for (var i = 0; i < data.length; i++) {
                            p_Id.push(data[i].productId);
                        }

                        /*layer.alert('演示模式，不允许操作', {icon: 2})*/
                        //将id 传回后端删除
                        $.ajax({
                            url: "mall/delProductByd",
                            type: "post",
                            data: {
                                "ids": p_Id,
                                isdel: -1
                            },
                            traditional: true,//这里设置为true
                            success: function (data) {
                                // layer.alert(JSON.stringify(data));
                                // var loding = layer.msg('数据加载中', {
                                //     icon: 16
                                //     , shade: 0.5
                                // });
                                // tabReload(loding);
                                layer.alert('演示模式，不允许操作', {icon: 2})
                            }
                            , error: function () {
                                layer.msg("系统繁忙，稍后再试！")
                            }
                        });
                        layer.close(index);
                    })
                }
            }
            //恢复按钮
            , getCheckCover: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                //layer.alert(JSON.stringify(data))
                if (data.length == 0) {
                    layer.alert("请至少选择一条", {icon: 5})
                } else {
                    layer.confirm('确定恢复销售？',{icon:3}, function (index) {
                        var p_Id = new Array();
                        for (var i = 0; i < data.length; i++) {
                            p_Id.push(data[i].productId);
                        }

                        /*layer.alert('演示模式，不允许操作', {icon: 2})*/
                        //将id 传回后端恢复
                        $.ajax({
                            url: "mall/delProductByd",
                            type: "post",
                            data: {
                                "ids": p_Id,
                                isdel: 0
                            },
                            traditional: true,//这里设置为true
                            success: function (data) {
                                //layer.alert(JSON.stringify(data));
                                // var loding = layer.msg('数据加载中', {
                                //     icon: 16
                                //     , shade: 0.5
                                // });
                                // tabReload(loding);
                                layer.alert("演示模式，不允许操作！",{icon:2});
                            }
                            , error: function () {
                                layer.msg("系统繁忙，稍后再试！")
                            }
                        });
                        layer.close(index);
                    })
                }
            }
        };

        //右侧操作栏 详情 和 删除
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {
                var lo = loding();

                $.post("mall/toDetails", {productId: data.productId}, function (result) {
                    //layer.alert(JSON.stringify(result));

                    var pid = result.data.productId;

                    //先将值清空一遍，以防累积
                    $("#updateProductDivForm input[type='text']").each(function () {
                        $(this).val($(this).defaultValue);
                    })
                    $("#updateProductDivForm input[type='checkbox']").attr("checked", false);
                    $(".updateImgList").empty();
                    $(".exhibitionImgMain").empty();


                    //将图片输出
                    var imgList = result.data.imgLLgList;
                    var imgllg4id="";
                    if (imgList.length==4) {
                        imgllg4id=imgList[3];
                    }

                    var li = "";
                    for (var i = 0; i < imgList.length; i++) {
                        li += "<li><div id='exhibition_div" + i + "' class='exhibition_div'>\n" +
                            " <p align='center'>第" + (i + 1) + "张</p>\n" +
                            " <img  src='" + imgList[i].imgllg + "' class='exhibitionImgSm'>\n" +
                            " <img src='" + imgList[i].imgllg + "' class='exhibitionImgMl'>\n" +
                            " <img id='Max" + i + "' src='" + imgList[i].imgllg + "' class='exhibitionImgMax'>\n" +
                            " </div></li>";
                    }
                    $(".updateImgList").append(li);
                    //主图片
                    var mainImg = "<img id='updateMainIMGDIV' src='" + result.data.imgMain + "'><p align='center'>主图片</p>";
                    $(".exhibitionImgMain").append(mainImg);


                    //上传操作 第一张
                    var uploadInst = upload.render({
                        elem: '#exhibition_div0'
                        , url: 'mall/updateImgMain/1'
                        , before: function (obj) {
                            //预读本地文件示例，不支持ie8
                            obj.preview(function (index, file, result) {
                                $('#demoText').empty();
                                $("#exhibition_div0").empty();
                                var p = "<p align='center'>第 1 张</p>";
                                var img1 = "<img  src='" + result + "' class='exhibitionImgSm'>";
                                var img2 = "<img src='" + result + "' class='exhibitionImgMl'>";
                                var img3 = "<img id='Max0' src='" + result + "' class='exhibitionImgMax'>";
                                $("#exhibition_div0").append(p + img1 + img2 + img3);
                            });
                        }
                        , data: {
                            productId: data.productId
                            , oldImgPath: $("#Max0").attr("src").substring(27, $("#Max0").attr("src").length)
                            ,oldImgId:imgList[0].imgllg_id
                        }
                        , done: function (res) {
                            // if (res.code > 0) {
                            //     //如果上传失败
                            //     return layer.msg('上传失败');
                            // }
                            // layer.alert("已更换",{icon:1});
                            layer.alert("演示模式，不允许操作！",{icon:2});
                        }
                        , error: function () {
                            //失败状态，并实现重传
                            $('#demoText').empty();
                            var demoText = $('#demoText');
                            demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                            demoText.find('.demo-reload').on('click', function () {
                                uploadInst.upload();
                            });
                        }
                    });
                    //上传操作 第二张
                    var uploadInst = upload.render({
                        elem: '#exhibition_div1'
                        , url: 'mall/updateImgMain/2'//
                        , before: function (obj) {
                            //预读本地文件示例，不支持ie8
                            obj.preview(function (index, upfile, result) {

                            });
                        }
                        , data: {
                            productId: data.productId
                            , oldImgPath: function () {
                                return $("#Max1").attr("src").substring(27, data.imgMain.length)
                            }
                            ,oldImgId:imgList[1].imgllg_id
                        }
                        , done: function (res) {
                            //如果上传失败
                            // if (res.code > 0) {
                            //     return layer.msg('上传失败');
                            // }
                            // //上传成功
                            // $('#demoText').empty();
                            // $("#exhibition_div1").empty();
                            // var p = "<p align='center'>第" + (i + 1) + "张</p>";
                            // var img1 = "<img  src='" + result + "' class='exhibitionImgSm'>";
                            // var img2 = "<img src='" + result + "' class='exhibitionImgMl'>";
                            // var img3 = "<img id='Max1' src='" + result + "' class='exhibitionImgMax'>";
                            // $("#exhibition_div1").append(p + img1 + img2 + img3);
                            layer.alert("演示模式，不允许操作！",{icon:2});

                        }
                        , error: function () {
                            //失败状态，并实现重传
                            $('#demoText').empty();
                            var demoText = $('#demoText');
                            demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                            demoText.find('.demo-reload').on('click', function () {
                                uploadInst.upload();
                            });
                        }
                    });
                    //上传操作 第三张
                    var uploadInst = upload.render({
                        elem: '#exhibition_div2'
                        , url: 'mall/updateImgMain/3'//
                        , before: function (obj) {
                            //预读本地文件示例，不支持ie8
                            obj.preview(function (index, upfile, result) {

                            });
                        }
                        , data: {
                            productId: data.productId
                            , oldImgPath: function () {
                                return $("#Max2").attr("src").substring(27, data.imgMain.length)
                            }
                            ,oldImgId:imgList[2].imgllg_id
                        }
                        , done: function (res) {
                            //如果上传失败
                            // if (res.code > 0) {
                            //     return layer.msg('上传失败');
                            // }
                            // $('#demoText').empty();
                            // //上传成功
                            // $("#exhibition_div2").empty();
                            // var p = "<p align='center'>第" + (i + 1) + "张</p>";
                            // var img1 = "<img  src='" + result + "' class='exhibitionImgSm'>";
                            // var img2 = "<img src='" + result + "' class='exhibitionImgMl'>";
                            // var img3 = "<img id='Max2' src='" + result + "' class='exhibitionImgMax'>";
                            // $("#exhibition_div2").append(p + img1 + img2 + img3);
                            layer.alert("演示模式，不允许操作！",{icon:2});

                        }
                        , error: function () {
                            //失败状态，并实现重传
                            $('#demoText').empty();
                            var demoText = $('#demoText');
                            demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                            demoText.find('.demo-reload').on('click', function () {
                                uploadInst.upload();
                            });
                        }
                    });
                    //上传操作 第四张
                    var uploadInst = upload.render({
                        elem: '#exhibition_div3'
                        , url: 'mall/updateImgMain/4'//
                        , before: function (obj) {
                            //预读本地文件示例，不支持ie8
                            obj.preview(function (index, upfile, result) {

                            });
                        }
                        , data: {
                            productId: data.productId
                            , oldImgPath: function () {
                                return $("#Max3").attr("src").substring(27, data.imgMain.length);
                            }
                            ,oldImgId:imgllg4id
                        }
                        , done: function (res) {
                            // $('#demoText').empty();
                            // //如果上传失败
                            // if (res.code > 0) {
                            //     return layer.msg('上传失败');
                            // }
                            // //上传成功
                            // $("#exhibition_div3").empty();
                            // var p = "<p align='center'>第" + (i + 1) + "张</p>";
                            // var img1 = "<img  src='" + result + "' class='exhibitionImgSm'>";
                            // var img2 = "<img src='" + result + "' class='exhibitionImgMl'>";
                            // var img3 = "<img id='Max0' src='" + result + "' class='exhibitionImgMax'>";
                            // $("#exhibition_div3").append(p + img1 + img2 + img3);
                            layer.alert("演示模式，不允许操作！",{icon:1});

                        }
                        , error: function () {
                            //失败状态，并实现重传
                            $('#demoText').empty();
                            var demoText = $('#demoText');
                            demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                            demoText.find('.demo-reload').on('click', function () {
                                uploadInst.upload();
                            });
                        }
                    });
                    //上传操作主图片
                    var uploadInst = upload.render({
                        elem: '#updateMainIMGDIV'
                        , url: 'mall/updateImgMain/0'//
                        , before: function (obj) {
                            //预读本地文件示例，不支持ie8
                            obj.preview(function (index, upfile, result) {

                            });
                        }
                        , data: {
                            productId: data.productId
                            , oldImgPath: function () {
                                return $("#updateMainIMGDIV").attr("src").substring(27, data.imgMain.length)
                            }
                        }
                        , done: function (res) {
                            // $('#demoText').empty();
                            // //如果上传失败
                            // if (res.code > 0) {
                            //     return layer.msg('上传失败');
                            // }
                            // //上传成功
                            // $(".exhibitionImgMain").empty();
                            // var img = "<img id='updateMainIMGDIV' src='" + result.data.imgMain + "'><p align='center'>主图片</p>";
                            // $(".exhibitionImgMain").append(img);

                            layer.alert("演示模式，不允许操作！",{icon:2});

                        }
                        , error: function () {
                            //失败状态，并实现重传
                            $('#exhibitionImgMainTxt').empty();
                            var demoText = $('#exhibitionImgMainTxt');
                            demoText.html('<span style="color: #FF5722;">上传失败</span> <a class="layui-btn layui-btn-xs demo-reload">重试</a>');
                            demoText.find('.demo-reload').on('click', function () {
                                uploadInst.upload();
                            });
                        }
                    });

                    //验证价格是否为dobble
                    $("#updateProductDivForm input[name='price']").blur(function () {
                        var val = $(this).val();
                        var reg = /^[0-9,.]*$/ //^[-\+]?\d+(\.\d+)?$/;
                        if (!reg.test(val)) {
                            layer.tips("请输入double类型的数字", this);
                            $("#updateProductDivForm .layui-btn").attr("disabled", true);
                            $("#updateProductDivForm .layui-btn").css("cursor", "not-allowed");
                        } else {
                            $(this).val(Math.round(val * 100) / 100);
                            $("#updateProductDivForm .layui-btn").attr("disabled", false);
                            $("#updateProductDivForm .layui-btn").css("cursor", "pointer");
                        }
                    })


                    //验证数量是否为 整数
                    $("#updateProductDivForm input[name='num']").blur(function () {
                        var regPos = /^\d+$/; // 非负整数
                        var val = $(this).val();
                        if (!regPos.test(val)) {
                            layer.tips("请输入正整数", this);
                            $("#updateProductDivForm .layui-btn").attr("disabled", true);
                            $("#updateProductDivForm .layui-btn").css("cursor", "not-allowed");
                        } else {
                            $("#updateProductDivForm .layui-btn").attr("disabled", false);
                            $("#updateProductDivForm .layui-btn").css("cursor", "pointer");
                        }
                    })


                    //将描述内容输出
                    UE.delEditor('UpdateUEditorDES');
                    $(".UpdateUEditor1").empty();
                    $(".UpdateUEditor1").html("<script id='UpdateUEditorDES' name='content' type='text/plain'></script>");
                    var UpdateUEditorDES = UE.getEditor('UpdateUEditorDES', {
                        toolbars: [

                            [
                                'anchor', //锚点
                                'undo', //撤销
                                'redo', //重做
                                'bold', //加粗
                                'indent', //首行缩进
                                'italic', //斜体
                                'underline', //下划线
                                'strikethrough', //删除线
                                'subscript', //下标
                                'fontborder', //字符边框
                                'superscript', //上标
                                'formatmatch', //格式刷
                                'horizontal', //分隔线
                                'removeformat', //清除格式
                                'time', //时间
                                'date', //日期
                                'insertrow', //前插入行
                                'insertcol', //前插入列
                                'mergeright', //右合并单元格
                                'mergedown', //下合并单元格
                                'deleterow', //删除行
                                'deletecol', //删除列
                                'splittorows', //拆分成行
                                'splittocols', //拆分成列
                                'splittocells', //完全拆分单元格
                                'deletecaption', //删除表格标题
                                'inserttitle', //插入标题
                                'mergecells', //合并多个单元格
                                'deletetable', //删除表格
                                'cleardoc', //清空文档
                                'insertparagraphbeforetable', //"表格前插入行"
                                'fontfamily', //字体
                                'fontsize', //字号
                                'paragraph', //段落格式
                                'edittable', //表格属性
                                'edittd', //单元格属性
                                'emotion', //表情
                                'spechars', //特殊字符
                                'searchreplace', //查询替换
                                'justifyleft', //居左对齐
                                'justifyright', //居右对齐
                                'justifycenter', //居中对齐
                                'justifyjustify', //两端对齐
                                'forecolor', //字体颜色
                                'backcolor', //背景色
                                'insertorderedlist', //有序列表
                                'insertunorderedlist', //无序列表
                                'directionalityltr', //从左向右输入
                                'directionalityrtl', //从右向左输入
                                'rowspacingtop', //段前距
                                'rowspacingbottom', //段后距
                                'imagenone', //默认
                                'imageleft', //左浮动
                                'imageright', //右浮动
                                'imagecenter', //居中
                                'wordimage', //图片转存
                                'lineheight', //行间距
                                'edittip ', //编辑提示
                                'customstyle', //自定义标题
                                'autotypeset', //自动排版
                                'touppercase', //字母大写
                                'tolowercase', //字母小写
                                'template', //模板
                                'scrawl', //涂鸦
                                'inserttable', //插入表格
                                'charts', // 图表
                            ]
                        ]
                    });

                    //设置编辑器的内容
                    UpdateUEditorDES.ready(function () {
                        UpdateUEditorDES.setContent("")
                        UpdateUEditorDES.setContent(result.data.describes);
                    })

                    //输出其它属性
                    $("#updateProductDivForm input[name='num']").val(result.data.num);
                    $("#updateProductDivForm input[name='price']").val(result.data.price);
                    $("#updateProductDivForm input[name='name']").val(result.data.name);


                    //选中已有颜色属性

                    var colorList = result.data.colorList;
                    //alert(JSON.stringify(colorList));
                    for (var i = 0; i < colorList.length; i++) {
                        $(".updateUl_color input[type='checkbox']").each(function (m) {
                            if ($(this).val() == colorList[i].color) {
                                this.checked = true;
                            }
                        })
                    }


                    //尺寸选中
                    var sizeList = result.data.productSizeList;
                    for (var i = 0; i < sizeList.length; i++) {
                        $("#updateProductDivForm .updateSizeDAXIAO input[type='checkbox']").each(function () {
                            if ($(this).val() == sizeList[i].size) {
                                this.checked = true;
                            }
                        })
                    }
                    form.render();  //更新渲染


                    //将细节展示内容输出
                    UE.delEditor('UpdateUEditorDetail');
                    $(".UpdateDetailUEditor").empty();
                    $(".UpdateDetailUEditor").html("<script id='UpdateUEditorDetail' name='content' type='text/html'></script>");
                    var UpdateUEditorDetail = UE.getEditor('UpdateUEditorDetail', {
                        toolbars: [

                            [
                                'anchor', //锚点
                                'undo', //撤销
                                'redo', //重做
                                'bold', //加粗
                                'indent', //首行缩进
                                'italic', //斜体
                                'underline', //下划线
                                'strikethrough', //删除线
                                'subscript', //下标
                                'fontborder', //字符边框
                                'superscript', //上标
                                'formatmatch', //格式刷
                                'horizontal', //分隔线
                                'removeformat', //清除格式
                                'time', //时间
                                'date', //日期
                                'insertrow', //前插入行
                                'insertcol', //前插入列
                                'mergeright', //右合并单元格
                                'mergedown', //下合并单元格
                                'deleterow', //删除行
                                'deletecol', //删除列
                                'splittorows', //拆分成行
                                'splittocols', //拆分成列
                                'splittocells', //完全拆分单元格
                                'deletecaption', //删除表格标题
                                'inserttitle', //插入标题
                                'mergecells', //合并多个单元格
                                'deletetable', //删除表格
                                'cleardoc', //清空文档
                                'insertparagraphbeforetable', //"表格前插入行"
                                'fontfamily', //字体
                                'fontsize', //字号
                                'paragraph', //段落格式
                                'edittable', //表格属性
                                'edittd', //单元格属性
                                'emotion', //表情
                                'spechars', //特殊字符
                                'searchreplace', //查询替换
                                'justifyleft', //居左对齐
                                'justifyright', //居右对齐
                                'justifycenter', //居中对齐
                                'justifyjustify', //两端对齐
                                'forecolor', //字体颜色
                                'backcolor', //背景色
                                'insertorderedlist', //有序列表
                                'insertunorderedlist', //无序列表
                                'directionalityltr', //从左向右输入
                                'directionalityrtl', //从右向左输入
                                'rowspacingtop', //段前距
                                'rowspacingbottom', //段后距
                                'imagenone', //默认
                                'imageleft', //左浮动
                                'imageright', //右浮动
                                'imagecenter', //居中
                                'wordimage', //图片转存
                                'lineheight', //行间距
                                'edittip ', //编辑提示
                                'customstyle', //自定义标题
                                'autotypeset', //自动排版
                                'touppercase', //字母大写
                                'tolowercase', //字母小写
                                'template', //模板
                                'scrawl', //涂鸦
                                'inserttable', //插入表格
                                'charts', // 图表
                            ]
                        ]
                    });
                    UpdateUEditorDetail.ready(function () {
                        //设置编辑器的内容
                        UpdateUEditorDetail.setContent("")
                        UpdateUEditorDetail.setContent(result.data.detail);
                    })

                    //弹出表单div
                    var addmsg = layer.open({
                        type: 1
                        , shadeClose: true //开启遮罩关闭
                        , title: '修改商品信息'
                        , area: ['800px', '300px']
                        , content: $("#updateProductDiv")
                        , maxmin: true
                        , success: function () {
                            layer.close(lo);
                        }
                        , cancel: function () {
                            //tabReload(loading);
                            //location.reload();
                        }
                    });
                    layer.full(addmsg);


                    //表单提交操作


                    $("#updateProductDivForm #updateBtn").click(function () {


                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.5
                            , time: 20000
                        });

                        UpdateUEditorDetail.ready(function () {
                            //设置编辑器的内容
                            $("#updateProductDivForm #detail").val(UpdateUEditorDetail.getContent());
                        })

                        UpdateUEditorDES.ready(function () {
                            $("#updateProductDivForm #describes").val(UpdateUEditorDES.getContent());
                        })

                        $.post("mall/updateProductById", $.param({'productId': pid}) + '&' + $("#updateProductDivForm").serialize(), function (data) {
                            layer.close(loading);
                            //layer.alert(JSON.stringify(data));
                            layer.alert("已修改",{icon:1}, function (index) {
                                // layer.close(index);
                                // location.reload();
                                layer.alert("演示模式,不允许操作！");
                            })
                        }).error(function () {
                            layer.close(loading);
                            layer.msg("系统异常，请稍后再试！");
                        })
                    });


                }).error(function () {
                    layer.close(lo);
                    layer.msg("系统异常，请稍后再试！");
                })


                //    删除操作
            } else if (obj.event === 'del') { //删除
                layer.confirm('真的下架么',{icon:3}, function (index) {
                    //layer.alert('演示模式，不允许操作', {icon: 2})
                    $.post("mall/delProductByd", {ids: obj.data.productId, isdel: 1}, function (data) {
                        //layer.alert(JSON.stringify(data));
                        // var loding = layer.msg('数据加载中', {
                        //     icon: 16
                        //     , shade: 0.5
                        // });
                        // tabReload(loding);
                        layer.alert('演示模式，不允许操作', {icon: 2})
                    })
                    layer.close(index);
                });

            } else if (obj.event === 'describes') {
                //修改描述
                //只实例化一次，为了将值写到内容中，实例化一次就删掉然后在实例，否则无法动态写值；
                UE.delEditor('UEditorDescribe');
                $("#updateDes div:nth-child(1)").empty();
                $("#updateDes div:nth-child(1)").html("<script id='UEditorDescribe' type='text/plain'></script>")
                var uDe = UE.getEditor('UEditorDescribe', {
                    toolbars: [

                        [
                            'anchor', //锚点
                            'undo', //撤销
                            'redo', //重做
                            'bold', //加粗
                            'indent', //首行缩进
                            'italic', //斜体
                            'underline', //下划线
                            'strikethrough', //删除线
                            'subscript', //下标
                            'fontborder', //字符边框
                            'superscript', //上标
                            'formatmatch', //格式刷
                            'horizontal', //分隔线
                            'removeformat', //清除格式
                            'time', //时间
                            'date', //日期
                            'insertrow', //前插入行
                            'insertcol', //前插入列
                            'mergeright', //右合并单元格
                            'mergedown', //下合并单元格
                            'deleterow', //删除行
                            'deletecol', //删除列
                            'splittorows', //拆分成行
                            'splittocols', //拆分成列
                            'splittocells', //完全拆分单元格
                            'deletecaption', //删除表格标题
                            'inserttitle', //插入标题
                            'mergecells', //合并多个单元格
                            'deletetable', //删除表格
                            'cleardoc', //清空文档
                            'insertparagraphbeforetable', //"表格前插入行"
                            'fontfamily', //字体
                            'fontsize', //字号
                            'paragraph', //段落格式
                            'edittable', //表格属性
                            'edittd', //单元格属性
                            'emotion', //表情
                            'spechars', //特殊字符
                            'searchreplace', //查询替换
                            'justifyleft', //居左对齐
                            'justifyright', //居右对齐
                            'justifycenter', //居中对齐
                            'justifyjustify', //两端对齐
                            'forecolor', //字体颜色
                            'backcolor', //背景色
                            'insertorderedlist', //有序列表
                            'insertunorderedlist', //无序列表
                            'directionalityltr', //从左向右输入
                            'directionalityrtl', //从右向左输入
                            'rowspacingtop', //段前距
                            'rowspacingbottom', //段后距
                            'imagenone', //默认
                            'imageleft', //左浮动
                            'imageright', //右浮动
                            'imagecenter', //居中
                            'wordimage', //图片转存
                            'lineheight', //行间距
                            'edittip ', //编辑提示
                            'customstyle', //自定义标题
                            'autotypeset', //自动排版
                            'touppercase', //字母大写
                            'tolowercase', //字母小写
                            'template', //模板
                            'scrawl', //涂鸦
                            'inserttable', //插入表格
                            'charts', // 图表
                        ]
                    ]
                });
                uDe.ready(function () {
                    //设置编辑器的内容
                    uDe.setContent("")
                    uDe.setContent(data.describes);
                })
                var frame = layer.open({
                    type: 1
                    , skin: 'demo-class'
                    , title: '修改商品描述'
                    , area: ['800px', '550px']
                    , content: $("#updateDes")
                    , btn: ['提交修改', '取消'] //可以无限个按钮
                    , yes: function (index, layero) {
                        //按钮【按钮1】的回调
                        //layer.alert(uDe.getContent());
                        //执行修改数据
                        $.post("mall/updateProductById", {
                            describes: uDe.getContent(),
                            productId: obj.data.productId
                        }, function (data) {
                            // layer.alert("已修改", function (index) {
                            //     layer.close(frame);
                            //     var loding = layer.msg('数据加载中', {
                            //         icon: 16
                            //         , shade: 0.5
                            //     });
                            //     tabReload(loding);
                            // });
                            layer.alert("演示模式，不允许操作！",{icon:2});
                        }).error(function () {
                            layer.msg("系统繁忙，稍后再试！");
                        })
                    }
                    , btn2: function (index, layero) {
                        //按钮【按钮二】的回调
                        layer.close(index);
                    }
                })

            } else if (obj.event === 'editImg') {
                var data = obj.data;
                $("#mainDa").html("<img width='300' height='300' src=" + data.imgMain + ">")
                $("#mainSm").html("<img width='200' height='200' src=" + data.imgMain + ">")
                //如果表单为空
                var addmsg = layer.open({
                    type: 1
                    , shadeClose: true //开启遮罩关闭
                    , title: '更改主图片'
                    , area: ['600px', '500px']
                    , content: $("#updateMainImg")
                    , maxmin: true
                    , resize: false
                    , shadeClose: false
                });
                $("#updateImgMainBtn").off().on('click', function () {
                    if ($("#imgMain").val().length == 0) {
                        layer.alert("请更换图片后上传", {icon: 5});
                    } else {
                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.5
                        });
                        var formdata = new FormData(document.getElementById("updateMainImgForm"));
                        formdata.append("oldImgPath", data.imgMain.substring(27, data.imgMain.length));
                        formdata.append("productId", data.productId);
                        formdata.append("temp", 5);
                        $.ajax({
                            url: 'mall/updateImgMain/0',//
                            type: 'POST',
                            data: formdata,
                            async: true,
                            cache: false,
                            contentType: false,
                            processData: false,
                            success: function (result) {
                                // if (result.msg == 'OK') {
                                //     layer.close(addmsg);//关掉当前 修改图片层
                                //     layer.close(loading);//关闭加载层
                                //     layer.alert("更换成功", {icon:1},function (index) {
                                //         var loding = layer.msg('数据加载中', {
                                //             icon: 16
                                //             , shade: 0.5
                                //         });
                                //         layer.close(index);
                                //         tabReload(loding);
                                //     });
                                // } else {
                                //     layer.alert("上传图片失败，请重新上传！", {icon: 5});
                                //     layer.close(loading);
                                // }

                                layer.alert("演示模式，不允许操作！",{icon:2});
                            },
                            error: function (result) {
                                layer.msg("系统繁忙，稍后再试！");
                                layer.close(loding);
                            }
                        });
                    }
                })
            } else if (obj.event === 'pname') {
                layer.prompt({
                    formType: 2,
                    value: data.name,
                    title: '请输入值',
                    area: ['500px', '150px'] //自定义文本域宽高
                }, function (value, index, elem) {

                    var loding = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.5
                        , time: 20000
                    });

                    $.post("mall/updateProductById", {name: value, productId: data.productId}, function (data) {
                        layer.close(loding);
                        // layer.alert("已修改",{icon:1}, function (index) {
                        //     tabReload();
                        //     layer.close(index);
                        // });
                        layer.alert("演示模式，不允许操作！",{icon:2});
                    }).error(function () {
                        layer.msg("系统繁忙，请稍后再试！");
                    })
                    // alert(value); //得到value
                    layer.close(index);
                });
            }
        });

        //监听单元格编辑
        table.on('edit(user)', function (obj) {
            var value = obj.value //得到修改后的值
                , data = obj.data //得到所在行所有键值
                , field = obj.field //得到字段
                , id = data.productId
                , productId = 'productId'
                , Product = {};
            //layer.msg('[ID: ' + data.id + '] ' + field + ' 字段更改为：' + value);
            Product[productId] = id;
            Product[field] = value;//.toFixed(2);

            var isOk = true;

            //验证价格是否为dobble
            if (field == 'price') {
                var reg = /^[0-9,.]*$/ //^[-\+]?\d+(\.\d+)?$/;
                if (!reg.test(value)) {
                    layer.tips("请输入double类型的数字", this);
                    isOk = false;
                } else {
                    Product[field] = Math.round(value * 100) / 100;
                }
            }
            //验证数量是否为 整数
            if (field == 'num') {
                var regPos = /^\d+$/; // 非负整数
                if (!regPos.test(value)) {
                    layer.tips("请输入正整数", this);
                    isOk = false;
                }
            }

            if (isOk == true) {
                $.post("mall/updateProductById", {productStr: JSON.stringify(Product)}, function (data) {
                    // layer.alert("已修改",{icon:1});
                    layer.alert("演示模式，不允许操作！",{icon:2});
                }).error(function () {
                    layer.msg("系统繁忙，请稍后再试！");
                })
            }
        });


    });
})