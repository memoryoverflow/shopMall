$(function () {
    //表格行 悬停 变色
    layui.use('table', function () {
        var table = layui.table;
        var $ = layui.$;

        //加载层
        function loding() {
            //loading层
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.5
            });
            return loding;
        }

        var loading = loding();
        //方法级渲染
        var tableIns = table.render({
            elem: '#LAY_table_user'
            , url: 'mall/OnlineUserList'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true, width: 30}
                ,{field: 'opId', title: '序号', align: 'center', fixed: true, width: 65}
                ,{field: 'id', title: '会话ID', align: 'center',  width: 100}
                , {field: 'name', title: '登录账号', align: 'center', width: 170}
                , {field: 'ip', title: '主机', sort: true, align: 'center', width: 130}
                , {field: 'address', title: '登录地址', align: 'center', width: 170}
                , {field: 'browser', title: '浏览器', sort: true, align: 'center', width: 80}
                , {field: 'system', title: '操作系统', sort: true, align: 'center', width: 120}
                , {field: 'time', title: '登录时间', sort: true, align: 'center', width: 170, templet: '#createTime'}
                , {title: '状态', width: 80, align: 'center', templet: '#status'}
                , {title: '操作', fixed: 'right', width: 80, align: 'center', templet: '#barDemo'}
            ]]
            , id: 'testReload'
            , page: true
            , height: 'full-200'
            , skin: 'nob'
            , limit: 5
            , limits: [5, 10, 20, 30, 40, 50, 100]
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
                    url: 'mall/OnlineUserList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        name: obj[0].value
                        , address: obj[1].value
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
                    url: 'mall/OnlineUserList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        name: obj[0].value
                        , address: obj[1].value
                        , startTime: startTime
                        , endTime: endTime
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
                url: 'mall/OnlineUserList'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    name: ""
                    , address: ""
                }
                , done: function (res, curr, count) { //数据加载完后的回调函数
                    layer.close(loading);
                }
            });

        }


        //搜索分页
        $('.main_top .layui-btn').on('click', function () {
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.OnlineUserSerachForm.elements.length; i++) {
                if (document.OnlineUserSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            //如果表单有一个不为空
            if (str.length > 0) {
                var obj = $("#OnlineUserSerachForm").serializeArray();
                var loading = loding();
                tabReloadSearch(obj, loading);
            } else {
                //如果单选框 选中
                layer.msg("请输入条件", {icon: 2, time: 1000})
            }
        });


        /*---------------------------------------*/
        //刷新按钮
        $("#reloadTab").click(function () {
            var loading = loding();
            tabReload(loading);
        })
        /*---------------------------------------*/

        //强退
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'out') {

                layer.confirm("确定强退吗？",{icon:3},function () {
                    $.post("/mall/logoutCore.do",{ids:data.id},function (data) {
                        // layer.alert("已强退",{icon:1},function (index) {
                        //     tabReload(index);
                        // });
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }).error(function (data) {
                        //layer.msg(JSON.stringify(data),{time:10000});
                        layer.msg("系统繁忙，请稍后再试！");

                    })
                })
            }
        })

        //顶部的删除 和添加按钮
        $('.main_content .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        var $ = layui.$, active = {
            getIdData: function () { //
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                //layer.alert(JSON.stringify(data));
                    layer.confirm("确定强退吗？",{icon:3},function () {
                        delBtn(data,"/mall/logoutCore.do");
                    })
            }
        }
        //删除
        function delBtn(data, url) {
            if (data.length == 0) {
                layer.alert("请至少选择一条", {icon: 5})
            } else {
                debugger;
                //loading层
                var loding = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.5
                });

                //将Id 存数组传后台，进行批量删除
                var log_Id = new Array();
                for (var i = 0; i < data.length; i++) {
                    log_Id.push(data[i].id);
                }

                //将id 传回后端删除
                $.ajax({
                    url: url,
                    type: "post",
                    data: {
                        "ids": log_Id,
                    },
                    traditional: true,//这里设置为true
                    success: function (data) {
                        //layer.msg("已删除");
                        //删除成功表格重载刷新数据
                        //tabReload(loding);
                        //layer.alert('演示模式，不允许操作', {icon: 2})
                        //layer.alert(data.msg, {icon: 1})

                        // layer.alert("已强退",{icon:1},function (index) {
                        //     tabReload(index);
                        // })
                        layer.alert("演示模式，不允许操作！",{icon:2})
                    }
                }).error(function () {
                    layer.msg("系统繁忙，稍后再试！")
                });
            }
        }
    });
})