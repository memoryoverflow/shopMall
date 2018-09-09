
// layui方法
    layui.use(['tree', 'table', 'layer'], function () {

        // 操作对象
        var table = layui.table
            , layer = layui.layer
            , $ = layui.jquery;

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
        // 表格渲染
        var tableIns = table.render({
            elem: '#dateTable'
            , url: 'mall/logOperateList'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true, width: 30}
                /*, {field: 'id', title: '日志编号', sort: true, align: 'center', fixed: true, width: 150}*/
                , {field: 'operateType', title: '操作模块', align: 'center', width: 100}
                , {field: 'host', title: 'Ip', align: 'center', width: 100}
                , {field: 'address', title: '操作地点', align: 'center', width: 100}
                , {field: 'url', title: 'Url', align: 'center', width: 100}
                /*, {field: 'method', title: '请求方式', align: 'center', width: 100}*/
                /*, {field: 'params', title: '请求参数', align: 'center', width: 100}*/
                , {field: 'logtype', title: '状态', align: 'center', width: 80, templet: '#logtype'}
                , {field: 'exception', title: '异常信息', align: 'center', width: 120, templet: '#exception'}
                , {field: 'time', title: '请求时间', align: 'center', sort: true, width: 140, templet: '#time'}
                , {field: 'user', title: '用户', align: 'center', width: 100}
                , {title: '操作', fixed: 'right', width: 167, align: 'center', toolbar: '#barDemo'}
            ]]
            , id: 'testReload'
            , page: true
            , height: 'full-200'
            , skin: 'nob'
            , limit: 10
            , limits: [5, 10, 20, 30, 40, 50, 100]
            , done: function (res, curr, count) { //数据加载完后的回调函数
                layer.close(loading);
            }
        });

        //表格搜索重载
        function tabReloadSearch(obj, loading) {
            //执行重载
            var strDate = obj[2].value;
            //layer.alert(JSON.stringify(obj))
            var startTime, endTime;
            if (strDate.length == 0) {
                //debugger;
                tableIns.reload({
                    url: 'mall/logOperateList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        operateType: obj[0].value
                        , user: obj[1].value
                        , logtype: obj[3].value
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
                    url: 'mall/logOperateList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        operateType: obj[0].value
                        , user: obj[1].value
                        , startTime: startTime
                        , endTime: endTime
                        , logtype: obj[3].value
                    }
                    , done: function (res, curr, count) { //数据加载完后的回调函数
                        layer.close(loading);
                    }
                });
            }

        }

        //刷新
        function tabReload(msg) {
            tableIns.reload({
                url: 'mall/logOperateList'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    operateType: ""
                    , user: ""
                    , logtype: ""
                }
                , done: function (res, curr, count) { //数据加载完后的回调函数
                    layer.close(msg);
                }
            });

        }

        //刷新按钮
        $("#reloadTab").click(function () {
            var loading = loding();
            tabReload(loading);
        })


        /*搜索表格重载*/
        $('.main_top #logSerachForm .layui-btn').on('click', function () {
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.logSerachForm.elements.length - 1; i++) {
                if (document.logSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            debugger;
            if (str.length > 0) {
                var obj = $("#logSerachForm").serializeArray();
                var loading = loding();
                tabReloadSearch(obj, loading);
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
            getCheckData: function () { //添加
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                //layer.alert(JSON.stringify(data));

            }
            //删除操作日志
            , getCheckLength: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                var url = "mall/delLogOperateList";
                layer.confirm("确定删除么？", {icon: 3}, function (index) {
                    // delBtn(data, url);
                    // tabReload(index)
                    layer.alert("演示模式，不允许操作！", {icon: 2})
                })
            }
            //删除登录日志
            , getChecklogin: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                var url = "mall/delLoginMSG";
                layer.confirm("确定删除么？", {icon: 3}, function (index) {
                    // delBtn(data, url);
                    // tabReload(index);
                    layer.alert("演示模式，不允许操作！", {icon: 2})
                })
            }
        };

        //删除日志函数
        function delBtn(data, url) {
            if (data.length == 0) {
                layer.alert("请至少选择一条", {icon: 5})
            } else {
                debugger;
                //loading层
                var loding = layer.msg('数据加载中', {
                    icon: 16
                    , shade: 0.3
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
                        layer.alert("已删除", {icon: 1}, function () {
                            tabReload();
                        })
                    }
                }).error(function () {
                    layer.msg("系统繁忙，稍后再试！")
                });
            }
        }


        //操作栏的 查看 删除 修改密码
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            if (obj.event === 'detail') {//详情
                //layer.alert(JSON.stringify(data));
                $("#oType").text(data.operateType);
                $("#IP").text(data.host);
                $("#url").text(data.url);
                $("#method").text(data.method);
                $("#params").text(data.params);
                $("#logType").text(data.logtype);
                $("#address").text(data.address);
                if (data.exception != null) {
                    $("#Exception").text(data.exception);
                }
                $("#Time").text(dateFormat(data.time));
                $("#user").text(data.user);

                var umsg = layer.open({
                    type: 1
                    , title: '异常信息详情'
                    , area: ['900px', '600px']
                    , content: $("#logMsg")
                });

            } else if (obj.event === 'del') {
                layer.confirm('确定删除么', function (index) {
                    //layer.alert('演示模式，不允许操作', {icon: 2})
                    //layer.alert(obj.data.id);
                    $.post("mall/delLogOperateList", {ids: obj.data.id}, function (data) {
                        layer.alert("已删除", {icon: 1}, function (index) {
                            tabReload(index);
                        });
                        //删除成功刷新数据
                        //layer.alert('演示模式，不允许操作', {icon: 2})
                    })

                });
            }
        });


        // 树        更多操作请查看 http://www.layui.com/demo/tree.html
        layui.tree({
            elem: '#tree' //传入元素选择器
            , click: function (item) { //点击节点回调
                layer.msg('当前节名称：' + item.name);
                // 加载中...
                var loadIndex = layer.load(2, {shade: false});
                // 关闭加载
                layer.close(loadIndex);
                // 刷新表格
                tableIns.reload();
            }
            , nodes: [{ //节点
                name: '日志管理'
                , spread: true
                , children: [
                    {
                        name: '操作日志'
                        , id: "operaLog"
                    }
                    , {
                        name: '登陆日志'
                        , id: "loginLog"
                    }
                ]
            }]
            , click: function (node) {

                if (node.id == "loginLog") {

                    $("#operateDiv").hide();//隐藏操作日志的
                    $("#relaodOpreateDIV").hide();//隐藏操作日志的刷新按钮
                    $("#addOpreateDIV").hide();////隐藏操作日志的添加删除按钮

                    $("#LoginDiv").show();
                    $("#relaodLoginLogDIV").show();
                    $("#addLoginLogDIV").show();

                    function login_loding() {
                        //loading层
                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.3
                            , time: 20000
                        });
                        return loding;
                    }

                    var logingIndex = login_loding();


                    var tableLogin = table.render({
                        elem: '#dateTable'
                        , url: 'mall/LoginMSGList'
                        , cellMinWidth: 30 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                        , cols: [[
                            {checkbox: true, fixed: true, width: 30}
                            , {field: 'id', title: '序号', align: 'center', fixed: true, width: 80}
                            , {field: 'name', title: '账号', align: 'center', width: 150}
                            , {field: 'ip', title: '主机', align: 'center', width: 100}
                            , {field: 'address', title: '登录地址', align: 'center', width: 120}
                            , {field: 'browser', title: '浏览器', align: 'center', width: 100}
                            , {field: 'system', title: '系统', align: 'center', width: 80}
                            , {field: 'status', title: "状态", width: 80, align: 'center', templet: '#status'}
                            , {field: 'msg', title: '登录信息', align: 'center', width: 120}
                            , {field: 'time', title: '时间', sort: true, align: 'center', width: 180, templet: '#time'}
                        ]]
                        , id: 'testReload'
                        , page: true
                        , height: 'full-200'
                        , skin: 'nob'
                        , limit: 10
                        , limits: [5, 10, 20, 30, 40, 50, 100]
                        , done: function (res, curr, count) { //数据加载完后的回调函数
                            layer.close(logingIndex);
                        }
                    });

                    //表格搜索重载
                    function tabReloadSearchLogin(obj, loading) {
                        //执行重载
                        var strDate = obj[2].value;
                        var startTime, endTime;
                        if (strDate.length == 0) {
                            //debugger;
                            tableLogin.reload({
                                url: 'mall/LoginMSGList'
                                , page: {
                                    curr: 1 //重新从第 1 页开始
                                }
                                , where: {
                                    address: obj[0].value
                                    , name: obj[1].value
                                    , status: obj[3].value
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
                            tableLogin.reload({
                                url: 'mall/LoginMSGList'
                                , page: {
                                    curr: 1 //重新从第 1 页开始
                                }
                                , where: {
                                    address: obj[0].value
                                    , startTime: startTime
                                    , endTime: endTime
                                    , name: obj[1].value
                                    , status: obj[3].value
                                }
                                , done: function (res, curr, count) { //数据加载完后的回调函数
                                    layer.close(loading);
                                }
                            });
                        }

                    }

                    /*搜索表格重载*/
                    $('.main_top #LoginlogSerachForm .layui-btn').off().on('click', function () {
                        //如果表单为空
                        var str = "";
                        for (var i = 0; i < document.LoginlogSerachForm.elements.length - 1; i++) {
                            if (document.LoginlogSerachForm.elements[i].value != "") {
                                str += ";";
                            }
                        }
                        if (str.length > 0) {
                            var obj = $("#LoginlogSerachForm").serializeArray();
                            var loading = login_loding();
                            tabReloadSearchLogin(obj, loading);
                        } else {
                            layer.msg("请输入条件", {icon: 2, time: 1000})
                        }
                    });

                    //刷新
                    function tabReloadLogin(msg) {
                        tableLogin.reload({
                            url: 'mall/LoginMSGList'
                            , page: {
                                curr: 1 //重新从第 1 页开始
                            }
                            , where: {
                                name: ""
                                , address: ""
                                , status: ""
                            }
                            , done: function (res, curr, count) { //数据加载完后的回调函数
                                layer.close(msg);
                            }
                        });

                    }

                    //刷新按钮
                    $("#reloadTablogin").click(function () {
                        var loading = login_loding();
                        tabReloadLogin(loading);
                    })


                } else {
                    var loding = layer.msg('数据加载中', {
                        icon: 16
                        , shade: 0.3
                        , time: 20000
                    });
                    $("#operateDiv").show();
                    $("#relaodOpreateDIV").show();
                    $("#addOpreateDIV").show();

                    $("#LoginDiv").hide();
                    $("#relaodLoginLogDIV").hide();
                    $("#addLoginLogDIV").hide();

                    var tableIns = table.render({
                        elem: '#dateTable'
                        , url: 'mall/logOperateList'
                        , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
                        , cols: [[
                            {checkbox: true, fixed: true, width: 30}
                            , {field: 'operateType', title: '操作模块', align: 'center', width: 100}
                            , {field: 'host', title: 'Ip', align: 'center', width: 100}
                            , {field: 'address', title: '操作地点', align: 'center', width: 100}
                            , {field: 'url', title: 'Url', align: 'center', width: 100}
                            , {field: 'logtype', title: '状态', align: 'center', width: 80, templet: '#logtype'}
                            , {field: 'exception', title: '异常信息', align: 'center', width: 120, templet: '#exception'}
                            , {field: 'time', title: '请求时间', align: 'center', sort: true, width: 140, templet: '#time'}
                            , {field: 'user', title: '用户', align: 'center', width: 100}
                            , {title: '操作', fixed: 'right', width: 167, align: 'center', toolbar: '#barDemo'}
                        ]]
                        , id: 'testReload'
                        , page: true
                        , height: 'full-200'
                        , skin: 'nob'
                        , limit: 10
                        , limits: [5, 10, 20, 30, 40, 50, 100]
                        , done: function (res, curr, count) { //数据加载完后的回调函数
                            layer.close(loding);
                        }
                    });
                }
            }
        });

    })
