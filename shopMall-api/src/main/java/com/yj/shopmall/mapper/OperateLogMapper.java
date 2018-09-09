package com.yj.shopmall.mapper;

import com.yj.shopmall.pojo.OperateLog;

import java.util.List;

public interface OperateLogMapper {

    //添加
    int addOperateLog(OperateLog operateLog);

    //更改
    int updateOperateLog(OperateLog operateLog);


    //删除
    int delOperateLog(String[] ids);

    //查找
    List<OperateLog> findOperateLog(OperateLog operateLog);

    //总数
    int findOperateLogCount(OperateLog operateLog);

}
