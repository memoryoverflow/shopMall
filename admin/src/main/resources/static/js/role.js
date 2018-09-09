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
            , url: 'mall/getRoles'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true}
                , {field: 'id', title: '角色编号', sort: true, align: 'center', fixed: true, width: 150}
                , {field: 'roleChar', title: '字符', align: 'center', width: 200}
                , {field: 'roleName', title: '角色名', sort: true, align: 'center', width: 200}
                , {field: 'remark', title: '备注',  sort: true, align: 'center', width: 200}
                , {field: 'creatTime', title: '创建时间', align: 'center', width: 280, templet: '#createTime'}
                , {title: '操作', fixed: 'right', width: 200, align: 'center', toolbar: '#barDemo'}
            ]]
            , id: 'testReload'
            , page: true
            , height: 'full-200'
            , skin: 'nob'
            , done: function (res, curr, count) { //数据加载完后的回调函数
                layer.close(loading);
            }
        });

        function addRoleAndUpdateRole(data,url,msg,addmsg) {
            var str = "";
            for (var i = 0; i < document.addroleForm.elements.length - 1; i++) {
                if (document.addroleForm.elements[i].value == "") {
                    str += ";";
                }
            }
            if (str.length > 0) {
                layer.msg("表单不允许有空", {icon: 5, anim: 6})
            } else {
                //loading层
                var loding = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.3
                    , time: 20000
                });
                $.ajaxSettings.async = true;
                $.post(url, $.param({'id':data})+'&'+$("#addroleForm").serialize(), function (back) {
                    layer.close(loding);
                    // layer.alert(msg, {icon: 1}, function (index) {
                    //     layer.close(addmsg);
                    //     tabReload();
                    //     layer.close(index);
                    // })
                    layer.alert("演示模式，不允许操作！",{icon:2});

                    //添加成功恢复默认
                    $("#addroleForm input[type='text']").each(function () {
                        $(this).val($(this).defaultValue);
                    })
                    $("#addroleForm textarea").val($(this).defaultValue);


                }).error(function (b) {
                    console.log(JSON.stringify(b));
                    layer.msg("系统繁忙，请稍后再试!");
                })
            }
        }



        //数据重载
        function tabReload() {
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.5
                , time: 10000
            });
            tableIns.reload({
                url: 'mall/getRoles'
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
                , shade: 0.3
                , time: 20000
            });
            var roleChar = $("#roleSerachForm #roleChar").val();
            var roleName = $("#roleSerachForm #roleName").val();
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.roleSerachForm.elements.length - 1; i++) {
                if (document.roleSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            if (str.length > 0) {
                //执行重载
                var loding = tableIns.reload({
                    url: 'mall/getRoles'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        roleChar: roleChar
                        , roleName: roleName
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
                    , title: '添加角色'
                    , area: ['800px', '500px']
                    , content: $("#roleAddAndUpdateDiv")
                    , maxmin: true
                });
                //点击提交
                $("#addRoleBtn").off().on('click',function () {
                    addRoleAndUpdateRole(0,"/mall/addRole","添加成功",addmsg);
                })

            }
            //删除操作
            , getCheckLength: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                if (data.length == 0) {
                    layer.alert("请至少选择一条", {icon: 5})
                } else {
                    layer.confirm('确定删除么', {icon: 3}, function (index) {
                        var p_Id = new Array();
                        for (var i = 0; i < data.length; i++) {
                            p_Id.push(data[i].id);
                        }

                        /*layer.alert('演示模式，不允许操作', {icon: 2})*/
                        //将id 传回后端删除

                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.3
                        });
                        $.ajax({
                            url: "mall/delRole",
                            type: "post",
                            data: {
                                "ids": p_Id
                            },
                            traditional: true//这里设置为true
                            , async: true
                            , success: function (data) {
                                //layer.alert(JSON.stringify(data));
                                // layer.alert("已删除", {icon: 1}, function (i) {
                                //   tabReload();
                                //     layer.close(i);
                                //     layer.close(loding);
                                // })
                                layer.alert("演示模式,不允许操作！",{icon:2})
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

        //右侧操作栏 编辑 和 删除
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {

                $("#addroleForm #roleChar").val(data.roleChar);
                $("#addroleForm #roleName").val(data.roleName);
                $("#addroleForm #remark").val(data.remark);

                var addmsg = layer.open({
                    type: 1
                    , shadeClose: true //开启遮罩关闭
                    , title: '添加角色'
                    , area: ['800px', '500px']
                    , content: $("#roleAddAndUpdateDiv")
                    , maxmin: true
                });

                //点击提交
                $("#addRoleBtn").off().on('click',function () {
                    addRoleAndUpdateRole(data.id,"/mall/updateRole","修改成功",addmsg);
                })

                //删除操作
            } else if (obj.event === 'del') { //删除
                layer.confirm('真的下删除么', {icon: 3}, function (index) {
                    var key=loding();
                    $.ajaxSettings.async=true;
                    $.post("mall/delRole", {ids: obj.data.id}, function (data) {
                        // layer.alert("已删除", {icon: 1}, function (i) {
                        //     tabReload();
                        //     layer.close(i);
                        //     layer.close(key);
                        // })
                        layer.alert("演示模式,不允许操作！",{icon:2})
                    })
                    layer.close(index);
                });
            }
        });
    });
})