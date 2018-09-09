$(function () {


    layui.use(['table', 'form', 'layer'], function () {
        var table = layui.table
            , form = layui.form;
        var $ = layui.$;
        var layer = layui.layer;

        //动态写出权限值
        $.post("/mall/getRoles", {}, function (back) {
            if (back.data != null) {
                var roles = back.data;
                var input = "";
                var updateinput = "";
                for (var i = 0; i < roles.length; i++) {
                    if (roles[i].roleChar == "common") {
                        input += "<input type='radio' name='role.id' value='" + roles[i].id + "' title='" + roles[i].roleName + "' checked>";
                    } else {
                        input += "<input type='radio' name='role.id' value='" + roles[i].id + "' title='" + roles[i].roleName + "' disabled>";
                    }
                    updateinput += "<input type='radio' name='m_rId' value='" + roles[i].id + "' title='" + roles[i].roleName + "'>";
                }
                $(".roleText").html(input);
                $(".updateRolestext").html(updateinput);
                form.render();
            }
        })


        //加载层
        function loding() {
            //loading层
            var loding = layer.msg('数据加载中', {
                icon: 16
                , shade: 0.3
                ,time:20000
            });
            return loding;
        }

        var loading = loding();
        //方法级渲染
        var tableIns = table.render({
            elem: '#LAY_table_user'
            , url: 'mall/userList'
            , cellMinWidth: 80 //全局定义常规单元格的最小宽度，layui 2.2.1 新增
            , cols: [[
                {checkbox: true, fixed: true, width: 30}
                , {field: 'uId', title: '用户编号', sort: true, align: 'center', fixed: true, width: 130}
                , {field: 'name', title: '用户姓名', align: 'center', width: 100, edit: 'text'}
                , {
                    field: 'sex',
                    style: 'padding-bottom:1px',
                    title: '性别',
                    sort: true,
                    align: 'center',
                    width: 100,
                    templet: '#sex',
                    unresize: true
                }
                , {field: 'email', title: '登录邮箱', sort: true, align: 'center', width: 165}
                , {field: 'tel', title: '电话', sort: true, align: 'center', width: 150, edit: 'text'}
                , {field: 'createTime', title: '创建时间', sort: true, align: 'center', width: 150, templet: '#createTime'}
                , {
                    field: 'isActivated',
                    title: '激活状态',
                    sort: true,
                    align: 'center',
                    width: 120,
                    templet: '#isActivated'
                }
                , {
                    field: 'isFrozen',
                    title: '是否冻结',
                    sort: true,
                    align: 'center',
                    width: 120,
                    templet: '#isFrozen',
                    unresize: true
                }
                , {title: '操作', fixed: 'right', width: 200, align: 'center', toolbar: '#barDemo'}
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


        //监听单元格编辑
        //校验数据是否唯一
        function checkDataIsOnlyOne(obj, url) {
            var msg = '';
            //设为同步请求
            $.ajaxSettings.async = false;
            $.post(url, {msg: obj}, function (back) {
                //alert(back);
                if (back == "ok") {
                    //用户名可用
                    msg = 'ok';
                } else {
                    msg = 'false';
                }
            });

            return msg;
        }

        //验证手机号码格式
        function isPoneAvailable(value) {
            var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
            if (!myreg.test(value)) {
                return false;
            } else {
                return true;
            }
        }

        function isEmailAvailable(str) {
            //对电子邮件的验证
            var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
            if (!myreg.test(str)) {
                return false;
            } else {
                return true;
            }
        }

        //校验表单所有的数据是否正确
        function checkIsAllTrue() {
            var str = "";
            $(".iStr").each(function (i) {
                if ($(this).text().length != 0) {
                    str += ".";
                }
            })
            return str;
        }

        function checkFormIsNull() {
            var str = "";
            var a = "";
            for (var i = 0; i < document.addUserForm.elements.length - 1; i++) {
                if (document.addUserForm.elements[i].value == "") {
                    str += ";";
                } else {
                    a += document.addUserForm.elements[i].value + ",";
                }
            }
            return str;
        }


        /*单元格编辑修改*/
        table.on('edit(user)', function (obj) {
            var loading = loding();
            var value = obj.value //得到修改后的值
                , datas = obj.data //得到所在行所有键值
                , field = obj.field; //得到字段
            if (field == 'name') {
                var str = checkDataIsOnlyOne(value, 'mall/nameIsOnly.do');
                if (str == 'ok') {
                    $.post("mall/updateUserById", {name: value, uId: datas.uId}, function (data) {
                        //layer.alert(JSON.stringify(data));
                        //layer.msg('[ID: ' + datas.uId + '] ' + field + ' 字段更改为：' + value);
                        layer.alert("演示模式,不允许操作！",{icon:2});
                    }).error(function (data) {
                        layer.msg("系统繁忙，稍后再试")
                    })
                    layer.close(loading);
                } else {
                    tabReload();
                    layer.msg("用户名已存在！")
                }
            } else {
                if (isPoneAvailable(value)) {
                    var str = checkDataIsOnlyOne(value, 'mall/phoneIsOnly.do');
                    if (str == 'ok') {
                        $.post("mall/updateUserById", {tel: value, uId: datas.uId}, function (data) {
                            //layer.alert(JSON.stringify(data));
                            //layer.msg('[ID: ' + datas.uId + '] ' + field + ' 字段更改为：' + value);
                            layer.alert("演示模式,不允许操作！",{icon:2});
                        }).error(function (data) {
                            tabReload();
                            layer.msg("系统繁忙，稍后再试")
                        })
                    } else {
                        tabReload();
                        layer.msg("电话已存在！")
                    }
                } else {
                    tabReload();
                    layer.msg("请输入正确的手机号码！")
                }
            }

        });


        //监听性别操作
        form.on('switch(sexDemo)', function (obj) {
            var sex;
            if (obj.elem.checked == true) {
                sex = '女'
            } else {
                sex = '男'
            }
            $.post("mall/updateUserById", {sex: sex, uId: this.value}, function (data) {
                //layer.alert(JSON.stringify(data));
                //layer.msg('已修改 ');
                tabReload();
                layer.alert("演示模式,不允许操作！",{icon:2});
            }).error(function (data) {
                layer.msg("系统繁忙，稍后再试")
            })
        });
        //监听激活操作
        form.on('checkbox(lockIsActivated)', function (obj) {
            var IsActivated;
            if (obj.elem.checked == true) {
                IsActivated = 1
            } else {
                IsActivated = -1
            }
            // layer.alert('演示模式，不允许操作', {icon: 2}, function (index) {
            //     layer.close(index);
            //     var loading = loding();
            //     tabReload(loading);
            // })
            $.post("mall/updateUserById", {isActivated: IsActivated, uId: this.value}, function (data) {
                //layer.alert(JSON.stringify(data));
                 tabReload(loading);
                // layer.msg('已修改 ');
                layer.alert("演示模式,不允许操作！",{icon:2});
            }).error(function (data) {
                tabReload();
                layer.msg("系统繁忙，稍后再试")
            })
        });
        //监听冻结锁定操作
        form.on('checkbox(lockDemo)', function (obj) {

            var isFrozen;
            if (obj.elem.checked == true) {
                isFrozen = -1
            } else {
                isFrozen = 1
            }

            $.post("mall/updateUserById", {isFrozen: isFrozen, uId: this.value}, function (data) {
                //layer.alert(JSON.stringify(data));
                tabReload(loading);
                // layer.msg('已修改 ');
                layer.alert("演示模式,不允许操作！",{icon:2});
            }).error(function (data) {
                layer.msg("系统繁忙，稍后再试")
            })
        });


        /*--------------开始------------------------*/
        //表格重载 搜索分页
        //表格搜索重载
        function tabReloadSearch(obj, loading) {
            //执行重载
            var strDate = obj[2].value;

            var startTime, endTime;
            if (strDate.length == 0) {
                //debugger;
                tableIns.reload({
                    url: 'mall/userList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        uId: obj[0].value
                        , email: obj[1].value
                        , name: obj[3].value
                        , isActivated: obj[4].value
                        , isFrozen: obj[5].value
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
                    url: 'mall/userList'
                    , page: {
                        curr: 1 //重新从第 1 页开始
                    }
                    , where: {
                        orderId: obj[0].value
                        , email: obj[1].value
                        , startTime: startTime
                        , endTime: endTime
                        , name: obj[3].value
                        , isActivated: obj[4].value
                        , isFrozen: obj[5].value

                    }
                    , done: function (res, curr, count) { //数据加载完后的回调函数
                        layer.close(loading);
                    }
                });
            }

        }

        $('.main_top .layui-btn').on('click', function () {
            //如果表单为空
            var str = "";
            for (var i = 0; i < document.userSerachForm.elements.length - 1; i++) {
                if (document.userSerachForm.elements[i].value != "") {
                    str += ";";
                }
            }
            if (str.length > 0) {
                var obj = $("#userSerachForm").serializeArray();
                var loading = loding();
                tabReloadSearch(obj, loading);
            } else {
                layer.msg("请输入条件", {icon: 2, time: 1000})
            }
        });
        /*------------------结束--------------------*/


        //表格重载
        function tabReload(msg) {
            tableIns.reload({
                url: 'mall/userList'
                , page: {
                    curr: 1 //重新从第 1 页开始
                }
                , where: {
                    uId: ""
                    , email: ""
                    , name: ""
                    , isActivated: ""
                    , isFrozen: ""
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
        /*-------------------结束--------------------*/


        //顶部的删除 和添加按钮
        $('.main_content .layui-btn').on('click', function () {
            var type = $(this).data('type');
            active[type] ? active[type].call(this) : '';
        });
        var $ = layui.$, active = {
            //添加用户
            getCheckData: function () {
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                //layer.alert(JSON.stringify(data));
                var addUserIndex = layer.open({
                    type: 1
                    , anim: 2
                    , shadeClose: true //开启遮罩关闭
                    , title: '添加用户'
                    , area: ['800px', '600px']
                    , content: $("#addUSerDiv")
                })


                var isTrue = new Array();
                var urlList = ["/mall/nameIsOnly.do", "/mall/emailIsOnly.do", "/mall/phoneIsOnly.do"];
                var tips = ["*用户名被注册", "*邮箱已被注册", "*电话已被注册"];
                $("#addUserForm input[type='text']").each(function (i) {
                    this.onblur = function () {
                        var val = $(this).val();
                        if (val.length == 0) {
                            layer.tips("请填写", this);
                        } else {
                            //alert(i+","+val);
                            if (i == 2) {
                                var b = isPoneAvailable(val);
                                if (b) {
                                    var isOk = checkDataIsOnlyOne(val, urlList[i]);
                                    if (isOk != "ok") {
                                        //layer.tips(tips[i], this);
                                        $(".iStr:eq(" + i + ")").text(tips[i]);
                                    } else {
                                        $(".iStr:eq(" + i + ")").text("");
                                    }
                                } else {
                                    //layer.tips("", this);
                                    $(".iStr:eq(" + i + ")").text("*手机格式不正确");
                                }
                            } else if (i == 1) {
                                var b = isEmailAvailable(val);
                                if (b) {
                                    var isOk = checkDataIsOnlyOne(val, urlList[i]);
                                    if (isOk != "ok") {
                                        //layer.tips(tips[i], this);
                                        $(".iStr:eq(" + i + ")").text(tips[i]);
                                    } else {
                                        $(".iStr:eq(" + i + ")").text("");
                                    }
                                } else {
                                    //layer.tips("", this);
                                    $(".iStr:eq(" + i + ")").text("*邮箱格式不对");
                                }
                            } else {
                                var isOk = checkDataIsOnlyOne(val, urlList[i]);
                                if (isOk != "ok") {
                                    //layer.tips(tips[i], this);
                                    $(".iStr:eq(" + i + ")").text(tips[i]);
                                } else {
                                    $(".iStr:eq(" + i + ")").text("");
                                }
                            }
                        }
                    }
                })
                //点击提交表单
                $("#addUserForm #addUserBtn").off().on('click', function () {
                    if (checkFormIsNull().length == 0 && checkIsAllTrue().length == 0) {

                        var key = layer.msg("正在注册", {icon: 16, shade: 0.3, time: 20000})


                        $.ajaxSettings.async = true;
                        $.post("/mall/register.do", $("#addUserForm").serialize(), function (bake) {
                            //alert(JSON.stringify(bake));
                            if (bake.status == 200) {
                                layer.alert("添加成功", {icon: 1}, function (i) {
                                    tabReload(i)
                                    layer.close(key);
                                    layer.close(i);
                                    layer.close(addUserIndex);
                                })
                            }
                        }).error(function () {
                            layer.close(key);
                            layer.msg("系统繁忙，稍后再试！")
                        })
                    } else {
                        layer.alert("数据校验未通过", {icon: 2}, function (e) {
                            layer.close(e);
                        })
                    }
                })
            }
            , getCheckLength: function () { //删除
                var checkStatus = table.checkStatus('testReload')
                    , data = checkStatus.data;
                if (data.length == 0) {
                    layer.alert("请至少选择一条", {icon: 5})
                } else {
                    layer.confirm('真的删除行么', {icon: 3}, function (index) {
                        //loading层
                        var loding = layer.msg('数据加载中', {
                            icon: 16
                            , shade: 0.3
                        });

                        var u_Id = new Array();
                        for (var i = 0; i < data.length; i++) {
                            u_Id.push(data[i].uId);
                        }

                        //layer.alert('演示模式，不允许操作', {icon: 2})
                        //将id 传回后端删除
                        $.ajax({
                            url: "mall/delUserById",
                            type: "post",
                            data: {
                                "ids": u_Id,
                            },
                            traditional: true,//这里设置为true
                            success: function (data) {
                                // layer.msg("已删除");
                                // //删除成功表格重载刷新数据
                                // tabReload();
                                layer.alert("演示模式,不允许操作！", {icon: 2});
                            }
                        }).error(function () {
                            layer.msg("系统繁忙，稍后再试！")
                        });
                    })
                }
            }
        };


        //操作栏的 查看 删除 修改密码
        table.on('tool(user)', function (obj) {
            var data = obj.data;
            // alert(JSON.stringify(data))
            if (obj.event === 'detail') {

                //alert(JSON.stringify(obj))

                /*用户地址列表渲染*/
                var addressList = obj.data.uAddressList;
                var tR = "";
                for (var i = 0; i < addressList.length; i++) {
                    if (addressList[i].isDefault == 1) {
                        tR += "<tr><td>" + addressList[i].address + "</td><td>" + addressList[i].recUser + "</td><td>" + addressList[i].recTel + "</td><td>是</td></tr>";
                    } else {
                        tR += "<tr><td>" + addressList[i].address + "</td><td>" + addressList[i].recUser + "</td><td>" + addressList[i].recTel + "</td><td>否</td></tr>";
                    }
                }
                $("#userAddressId tbody").empty();
                $("#userAddressId tbody").append(tR);

                //用户基本信息
                if (data.img.length > 0) {
                    $("#uImg img").attr("src", data.img);
                }
                $("#userId").html(data.uId);
                $("#createTimeM").text(dateFormat(data.createTime));
                $("#name").html(data.name);
                $("#sexM").html(data.sex);
                $("#emailM").html(data.email);
                $("#tel").html(data.tel);
                $("#password").html(data.password);
                $("#role").html(data.role.roleName);
                if (data.isActivated == -1) {
                    $("#isActivatedM").html("是");
                } else {
                    $("#isActivatedM").html("否");
                }

                if (data.isFrozen == -1) {
                    $("#isFrozenM").html("是");
                } else {
                    $("#isFrozenM").html("否");
                }
                var umsg = layer.open({
                    type: 1
                    , anim: 2
                    , skin: 'layuiAlert'
                    , shadeClose: true //开启遮罩关闭
                    , title: '密码重置'
                    , area: ['800px', '300px']
                    , content: $("#userMsg")
                    , maxmin: true
                });
                layer.full(umsg);

            } else if (obj.event === 'del') {
                layer.confirm('真的删除行么', {icon: 3}, function (index) {


                    $.post("mall/delUserById", {ids: obj.data.uId}, function (data) {
                        // layer.alert("已删除", {icon: 1}, function (index) {
                        //     //删除成功刷新数据
                        //     tabReload(index);
                        // })
                        layer.alert('演示模式，不允许操作', {icon: 2})
                    })
                });
            } else if (obj.event === 'edit') {
                //layer.alert('编辑行：<br>' + JSON.stringify(obj))
                $("#email").val(obj.data.email);
                $("#password").val(obj.data.password);

                var up = layer.open({
                    type: 1
                    , anim: 2
                    , shadeClose: true //开启遮罩关闭
                    , title: '密码重置'
                    , area: ['800px', '300px']
                    , content: $("#updatePwd")
                });

                $("#submit").off().on('click', function () {
                    if ($("#password").val().length == 0) {
                        layer.msg("请输入密码", {icon: 2})
                    } else {
                        //layer.alert('演示模式，不允许操作', {icon: 2})
                        $.post("mall/updateUserById", {
                            uId: obj.data.uId,
                            email: $("#email").val(),
                            password: $("#password").val()
                        }, function (data) {
                            // if (data.status == 200) {
                            //     layer.alert("已修改", {icon: 1});
                            //     layer.close(up);
                            // }
                            layer.alert("演示模式，不允许操作！",{icon:2})
                        }).error(function (data) {
                            layer.msg("系统繁忙，稍后再试")
                        })
                    }
                })
            } else if (obj.event === 'role') {
                //layer.alert(JSON.stringify(data));
                $(".updateRolestext input").each(function (i) {
                    if ($(this).val() == data.role.id) {
                        this.checked = true;
                    }
                })
                form.render();
                var updateUserRoleIndex = layer.open({
                    type: 1
                    , anim: 2
                    , shadeClose: true //开启遮罩关闭
                    , title: '添加用户'
                    , area: ['500px', '300px']
                    , content: $("#updateUserRole")
                    , btn: ['修改']
                    , yes: function (index) {
                        var key=loding();
                        $.ajaxSettings.async=true;
                         $.post("/mall/updateUserRole",$.param({'m_uId': data.uId}) + '&' + $("#updateUserRoleForm").serialize(),function (back) {
                             layer.close(key);
                             // layer.alert("已修改",{icon:1},function (index) {
                             //     layer.close(index)
                             // });
                             layer.alert("演示模式，不允许操作！",{icon:2})
                         }).error(function (b) {
                             layer.close(key);
                             console.log(JSON.stringify(b));
                             layer.msg("系统异常，稍后再试！");

                         })
                    }
                })

            }
        });
    });

    /* 时间戳转化开始 */
    function dateFormat(value) {

        var date = new Date(value);//如果date为13位不需要乘1000
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
        var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + ' ';
        var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
        var m = (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) + ':';
        var s = (date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds());
        return Y + M + D + h + m + s;
    }
})