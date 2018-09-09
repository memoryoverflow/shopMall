package com.yj.shopmall.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.yj.shopmall.Utils.UploadFile;
import com.yj.shopmall.conf.ueditor.UEditorJson;
import com.yj.shopmall.service.*;
import com.yj.shopmall.pojo.Ueditor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class UeditorController {

    @Reference
    ProductServerce productServerce;


    @RequestMapping("/ueditor.action")
    public String ueditor(@RequestParam("action") String param, MultipartFile upfile, HttpServletRequest request) {
        Ueditor ueditor = new Ueditor();
        //初始化请求
        if (param != null && param.equals("config")) {
            return UEditorJson.UEDITOR_CONFIG;
        } else if (param != null && param.equals("uploadimage") || param.equals("uploadscrawl")) {
            if (upfile != null) {
                //{state：”数据状态信息”，url：”图片回显路径”，title：”文件title”，original：”文件名称”，···}
                try {
                    //图片上传
                    return JSON.toJSONString(UploadFile.uploadImgUEditor(upfile,null, request));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    ueditor.setState("出现异常");
                    return JSON.toJSONString(ueditor);
                }
            } else {
                ueditor.setState("文件为空");
                return JSON.toJSONString(ueditor);
            }
        }else {
            ueditor.setState("不支持该操作");
            return JSON.toJSONString(ueditor);
        }
    }

    @RequestMapping(value = "/imgUpload")
    public Ueditor imgUpload(@RequestParam("action") String param, MultipartFile upfile, HttpServletRequest request) {
        Ueditor ueditor = new Ueditor();
        return ueditor;
    }



}
