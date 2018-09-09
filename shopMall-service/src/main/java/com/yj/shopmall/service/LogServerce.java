package com.yj.shopmall.service;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.pojo.OperateLog;

public interface LogServerce {
    //添加
    JsonResult addOperateLog(OperateLog operateLog);

    //更改
    JsonResult updateOperateLog(OperateLog operateLog);


    //删除
    JsonResult delOperateLog(String[] ids);

    //查找
    ResultLayui findOperateLog(OperateLog operateLog);
}
