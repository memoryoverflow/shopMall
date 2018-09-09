package com.yj.shopmall.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.annotation.Mylog;
import com.yj.shopmall.pojo.OperateLog;
import com.yj.shopmall.service.LogServerce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mall")
public class OperateLogController {
    private Logger logger = LoggerFactory.getLogger(OperateLogController.class);


    @Reference
    LogServerce logServerce;


    /*
    *
    * 列表
    * */
    @RequestMapping("/logOperateList")
    @Mylog(description="查询日志列表")
    public ResultLayui<OperateLog> logOperateList(OperateLog operate){
        return logServerce.findOperateLog(operate);
    }

    /*
    * 删除
    * */
    @RequestMapping("/delLogOperateList")
    @Mylog(description="日志删除")
    public JsonResult<OperateLog> delLogOperateList(String[] ids){
        return JsonResult.ok();
//        return logServerce.delOperateLog(ids);
    }
}
