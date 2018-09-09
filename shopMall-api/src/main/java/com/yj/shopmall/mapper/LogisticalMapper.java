package com.yj.shopmall.mapper;

import com.yj.shopmall.pojo.Logistics;

import java.util.List;

public interface LogisticalMapper {
    int addLogistics(Logistics logistics);
    int updateLogistics(Logistics logistics);
    int delLogistics(String[] ids);
    Logistics findOne(Logistics logistics);
    List<Logistics> findLogistics(Logistics logistics);
}
