package com.yj.shopmall.exceptionHandling;

import com.yj.shopmall.Utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ExceptionsHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionsHandler.class);
    public static final String ERROR_VIEW = "error";
    public static final String Login_VIEW = "commodity/index";
    public static final String Email_VIEW = "commodity/EmailActived";

    @ExceptionHandler(value = Exception.class)
    public Object errorHandler(HttpServletRequest reqest,
                               HttpServletResponse response, Exception e) throws Exception {

        e.printStackTrace(); //抛出异常
        if (isAjax(reqest)) {
            logger.info("ajax请求异常" + e);
            return JsonResult.errorException(e.getMessage());
        } else {
            ModelAndView mav = new ModelAndView();
            if (e.getMessage() == null || e.getMessage().equals("")) {

            }else{
                if (e.getMessage().indexOf("激活失败") >= 0) {
                    logger.info("非ajax请求异常");
                    mav.addObject("msg", "激活失败!请联系管理员 1375668614@qq.com");//返回错误信息
                    mav.addObject("url", reqest.getRequestURL());
                    mav.setViewName(Email_VIEW);
                    return mav;
                } else {
                    logger.info("非ajax请求异常");
                    mav.addObject("exception", e.getMessage());//返回错误信息
                    mav.addObject("url", reqest.getRequestURL());
                    mav.setViewName(ERROR_VIEW);
                    return mav;
                }
            }
            return null;
        }
    }

    /**
     * @Description: 判断是否是ajax请求
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals(httpRequest.getHeader("X-Requested-With").toString()));
    }
}
