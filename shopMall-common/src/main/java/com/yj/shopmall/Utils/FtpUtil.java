package com.yj.shopmall.Utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FtpUtil {
    /**
     * Description: 向FTP服务器上传文件
     *
     * @param host FTP服务器ip
     * @param port FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param basePath FTP服务器基础目录,/home/ftpuser/images
     * @param filePath FTP服务器文件存放路径。例如分日期存放：/2018/05/28。文件的路径为basePath+filePath
     * @param filename 上传到FTP服务器上的文件名
     * @param input 输入流
     * @return 成功返回true，否则返回false
     */

    private static final String host = "106.14.226.138";  //FTP服务器ip
    private static final int port = 21;                    //FTP服务器端口
    private static final String username = "ftpuser";     //FTP登录账号
    private static final String password = "123";         //FTP登录密码
    private static final String basePath = "/home/ftpuser/images";   //FTP服务器基础目录,/home/ftpuser/images 图片上传到这
    private static final String filePath = "http://img.linux.com:7777"; //FTP服务器文件存放路径。例如分日期存放：/2018/05/28。文件的路径为basePath+filePath


    public static boolean uploadFile(Map<String, InputStream> FileMap) throws IOException {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        InputStream input = null;
        //如果某个上传失败，将当前集合上传成功的图片删除
        List<String> uploadS = new ArrayList<>();

        //if连接成功
        if (getConnect(ftp)) {
            //切换到上传目录
            ftp.changeWorkingDirectory(basePath);
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            //执行上传,遍历集合取值
            int i = 0;
            String fileName="";
            for (Map.Entry<String, InputStream> entry : FileMap.entrySet()) {
                input = entry.getValue(); //键
                fileName = entry.getKey();     //值
                System.out.println("上传的图片名字："+fileName);
                try {
                    //上传操作
                    result = ftp.storeFile(fileName, input);

                    //上传成功
                    if (result) {
                        i++;
                        uploadS.add(fileName);
                    }
                } catch (IOException e) {
                    deleteFile(uploadS);
                    ftp.disconnect();
                    ftp.logout();
                    input.close();
                    System.out.println("上传异常：" + e);
                    return false;
                } finally {
                    System.out.println("已成功上传： " + (i) + " 个");
                }
            }
        }
        if (ftp.isConnected()) {
            try {
                ftp.disconnect();
                ftp.logout();
            } catch (IOException ioe) {

            }
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("关闭上传连接异常");
            }
        }
        return result;
    }

    /*
     * 删除
     * */
    public static boolean deleteFile(List<String> FileName) throws IOException {
        FTPClient ftp = new FTPClient();
        boolean bol = false;
        System.out.println(getConnect(ftp));
        if (getConnect(ftp)) {
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.changeWorkingDirectory(basePath);
            int count = 0;
            for (String fileName : FileName) {
                try {
                    bol = ftp.deleteFile(fileName);
                    if (bol) {
                        count++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("删除了：" + count + "个");
                }
            }
            //登出
            ftp.logout();
            //断开连接
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return bol;
    }


    /*
    *
    * */





    /*
     * 连接vsftp服务器客户端
     * */
    public static boolean getConnect(FTPClient ftp) {
        ftp.setControlEncoding("UTF-8");
        try {
            int reply;
            ftp.connect(host, port);// 连接FTP服务器
            // 如果采用默认端口，可以使用ftp.connect(host)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.out.println("连接失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void main(String[] a) throws IOException {
    }

}
