package com.yj.shopmall.Utils;

import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getIPAddress {
    public static final String IP_URL = "http://ip.taobao.com/service/getIpInfo.php";

    /**
     * 获取查询结果
     *
     * @param urlStr
     * @param content
     * @param encoding
     * @return
     */
    private static String sendPost(String content, String encoding)
    {
        URL url = null;
        HttpURLConnection connection = null;
        try
        {
            url = new URL(IP_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(content);
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                buffer.append(line);
            }
            reader.close();
            return buffer.toString();
        }
        catch (IOException e)
        {
            System.out.println("温馨提醒：您的主机已经断网，请您检查主机的网络连接");
            System.out.println("根据IP获取所在位置----------错误消息：" + e.getMessage());
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
        return null;
    }

    public static String getAddress(String ip)
    {

        String address = "";
        try
        {
            address = sendPost("ip=" + ip, "UTF-8");

            JSONObject json = JSONObject.parseObject(address);
            JSONObject object = json.getObject("data", JSONObject.class);
            String region = object.getString("region");
            String city = object.getString("city");
            address = region + " " + city;
        }
        catch (Exception e)
        {
            System.out.println("根据IP获取所在位置----------错误消息：" + e.getMessage());
        }
        return address;
    }
    public static void main(String[]a){
        String realAddressByIP = getIPAddress.getAddress("14.103.239.133");
        System.out.println(realAddressByIP);
    }
}
