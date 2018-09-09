package com.yj.shopmall.Utils;

import com.yj.shopmall.pojo.Ueditor;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class UploadFile {
    static final String basepath = "http://106.14.226.138:7777/";

    //发布商品多个图片上传的
    public static List<String> uploadImg(List<MultipartFile> uploadFile, String p_Id,
                                         HttpServletRequest request) throws IOException {
        String isOk = "";
        String fileName = ""; //文件名
        Ueditor ueditor = new Ueditor();
        MultipartFile file = null;
        String fileType = "";
        String ct = "";
        InputStream input = null;
        //存取图片名字 返回到 server
        List<String> imgLLgList = new ArrayList<>();

        Map<String, InputStream> fileMap = new HashMap<>();

        int n = -1;
        int i = -1;

        String[] cha = {"A-", "B-", "C-", "D-"};
        //图片随机名字
        for (MultipartFile multipartFile : uploadFile) {
            i++;
            file = (MultipartFile) uploadFile.get(i);
            ct = file.getContentType();
            if (ct.indexOf("/") > 0) {
                fileType = ct.substring(ct.indexOf("/") + 1);
            }

            if (i == uploadFile.size() - 1) {//主图片
                //生成图片名
                fileName = p_Id + "-MAIN" + "." + fileType; //主图片名字
                imgLLgList.add(basepath + fileName);
            } else {
                //前三张展示图
                n++;
                fileName = cha[n] + p_Id + "-" + i + "." + fileType;
                imgLLgList.add(basepath + fileName);
            }

            //获得文件流
            input = file.getInputStream();
            //获得文件名字
            String name = fileName;
            fileMap.put(name, input);

        }

        //调用方法执行上传
        boolean b = FtpUtil.uploadFile(fileMap);
        if (!b) {
            for (String s : imgLLgList) {
                System.out.println(s);
            }
            return new ArrayList();
        }
        return imgLLgList;
    }


    //商品活动封面图片上传的
    public static List<String> SeckillImgUpload(List<MultipartFile> files, String pid, String[] imgNameList) throws IOException, InterruptedException {
        int i = 0;
        Map<String, InputStream> fileMap = new HashMap<>();
        List<String> imgPathList = new ArrayList<>();
        InputStream input = null;
        MultipartFile file = null;
        String fileType = "";
        String fileName = "";
        String ct = "";
        if (imgNameList == null) {

            for (MultipartFile multipartFile : files) {
                i++;
                file = (MultipartFile) files.get(i - 1);
                ct = file.getContentType();

                if (ct.indexOf("/") > 0) {
                    fileType = ct.substring(ct.indexOf("/") + 1);
                }
                if (i == 1) {//主图片
                    //生成图片名
                    fileName = "A-" + pid + "-SECKILLIMG-1" + "." + fileType; //主图片名字
                } else if (i == 2) {
                    //前三张展示图
                    fileName = "B-" + pid + "-SECKILLIMG-2" + "." + fileType; //主图片名字
                } else if (i == 3) {
                    fileName = "C-" + pid + "-SECKILLIMG-3" + "." + fileType; //主图片名字
                }
                imgPathList.add(basepath + fileName);

                //获得文件流
                input = file.getInputStream();
                //获得文件名字
                fileMap.put(fileName, input);
            }
        } else {
            //更换图片 不改原来的名字
            for (MultipartFile multipartFile : files) {
                i++;
                file = (MultipartFile) files.get(i - 1);
                if (i == 1) {//主图片
                    //生成图片名
                    fileName = imgNameList[0]; //主图片名字
                } else if (i == 2) {
                    //前三张展示图
                    fileName = imgNameList[1];
                    ; //主图片名字
                } else if (i == 3) {
                    fileName = imgNameList[2];
                    ; //主图片名字
                }
                imgPathList.add(basepath + fileName);
                //获得文件流
                input = file.getInputStream();
                //获得文件名字
                fileMap.put(fileName, input);
            }

        }
        //调用方法执行上传
        boolean b = FtpUtil.uploadFile(fileMap);
        if (!b) {

            return new ArrayList();
        }
        return imgPathList;
    }


    /*
     * 是 单个 ueditor 上传图片的
     * */
    public static Ueditor uploadImgUEditor(MultipartFile uploadFile, String name,
                                           HttpServletRequest request) throws IOException {
        String isOk = "";
        String fileName = ""; //文件名
        Ueditor ueditor = new Ueditor();
        if (name == null || name.equals("")) {
            String ct = uploadFile.getContentType();
            String fileType = "";
            fileType = ct.substring(ct.indexOf("/") + 1);
            fileName = UUID.randomUUID() + "." + fileType;
        } else {
            fileName = name;
        }
        //获得文件流
        InputStream input = uploadFile.getInputStream();
        //调用方法执行上传
        Map<String, InputStream> fileMap = new HashMap<>();
        fileMap.put(fileName, input);
        boolean b = FtpUtil.uploadFile(fileMap);

        if (b) {
            ueditor.setState("SUCCESS");
            ueditor.setTitle(fileName);
            ueditor.setOriginal(fileName);
            ueditor.setUrl(basepath + fileName);
            return ueditor;
        } else {
            Ueditor ueditor1 = new Ueditor();
            ueditor.setState("fasle");
            return ueditor1;
        }
    }


    /*
     * 其它图片上传
     * */
    public static List<String> uploadOrtherImg(List<MultipartFile> fileList) throws IOException {
        //存取图片名字 返回到 server
        List<String> imgLLgList = new ArrayList<>();
        Map<String, InputStream> fileMap = new HashMap<>();
        MultipartFile file = null;
        String ct = "";
        String fileType = "";
        String fileName = "";
        final String fiex = "PINGZHENG:";
        InputStream input = null;

        //图片随机名字
        for (MultipartFile multipartFile : fileList) {
            file = multipartFile;
            ct = file.getContentType();

            if (ct.indexOf("bnp") >= 0 || ct.indexOf("png") >= 0 ||
            ct.indexOf("gif") >= 0 || ct.indexOf("jpg") >= 0 || ct.indexOf("jpeg") >= 0){

                if (ct.indexOf("/") > 0) {
                    fileType = ct.substring(ct.indexOf("/") + 1);
                }

                fileName = fiex + UUID.randomUUID().toString() + "." + fileType;
                imgLLgList.add(basepath + fileName);
                //获得文件流
                input = file.getInputStream();
                //获得文件名字
                fileMap.put(fileName, input);
            }
        }

        //调用方法执行上传
        boolean b = FtpUtil.uploadFile(fileMap);
        if (!b) {
            for (String s : imgLLgList) {
                System.out.println(s);
            }
            return new ArrayList();
        }
        input.close();
        return imgLLgList;
    }

}
