package com.yj.shopmall.Utils;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

public class getOperateSys {
    public static String getSys(String USER_AGENT) {
        UserAgent agent = UserAgent.parseUserAgentString(USER_AGENT);
        OperatingSystem sys = agent.getOperatingSystem();
        String name = sys.getName();
        return name;
    }
}
