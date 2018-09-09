package com.yj.shopmall.Utils;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

public class getBrowser {
    public static String getBrowser(String USER_AGENT) {
        UserAgent agent = UserAgent.parseUserAgentString(USER_AGENT);
        Browser browser = agent.getBrowser();
        String name = browser.getName();
        return name;
        /*final String agent = USER_AGENT;
        String filename = "";
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = "IE 浏览器";

        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = "Firefox";
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = "Google";
        } else {
            // 其它浏览器
            filename = "其它浏览器";
        }*/
    }

}
