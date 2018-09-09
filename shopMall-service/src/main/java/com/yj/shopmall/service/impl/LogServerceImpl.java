package com.yj.shopmall.service.impl;

import com.yj.shopmall.Utils.JsonResult;
import com.yj.shopmall.Utils.ResultLayui;
import com.yj.shopmall.mapper.OperateLogMapper;
import com.yj.shopmall.pojo.OperateLog;
import com.yj.shopmall.service.LogServerce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@com.alibaba.dubbo.config.annotation.Service
public class LogServerceImpl implements LogServerce {

    @Autowired
    OperateLogMapper logMapper;

    @Override
    public JsonResult addOperateLog(OperateLog operateLog) {
        try {
            int i = logMapper.addOperateLog(operateLog);
        } catch (Exception e) {
            throw new RuntimeException("日志记录异常"+e);
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult updateOperateLog(OperateLog operateLog) {
        try {
             int i = logMapper.updateOperateLog(operateLog);
        } catch (Exception e) {
            throw new RuntimeException("日志更新异常"+e);
        }
        return JsonResult.ok();
    }

    @Override
    public JsonResult delOperateLog(String[] ids) {
        try {
             int i = logMapper.delOperateLog(ids);
        } catch (Exception e) {
            throw new RuntimeException("日志删除异常");
        }
        return JsonResult.ok();
    }

    @Override
    public ResultLayui findOperateLog(OperateLog operateLog) {
        int page = operateLog.getPage();
        int pageStart = (page - 1) * operateLog.getLimit();
        operateLog.setPage(pageStart);
        int operateLogCount = logMapper.findOperateLogCount(operateLog);
        return ResultLayui.jsonLayui(0,"",operateLogCount,logMapper.findOperateLog(operateLog));
    }
}
