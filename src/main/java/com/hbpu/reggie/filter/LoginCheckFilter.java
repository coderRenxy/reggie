package com.hbpu.reggie.filter;

import com.alibaba.fastjson2.JSON;
import com.hbpu.reggie.common.BaseContext;
import com.hbpu.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
* 检查用户是否已经完成登录
* */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String[] urls = new String[]{//直接放行的url
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
        };
//        log.info("蓝到了：{}",request.getRequestURI());
        boolean check = check(urls,request.getRequestURI());//匹配结果
//        log.info("匹配结果：{}",check);
        if(check || request.getSession().getAttribute("employee") != null) {//员工登陆状态拦截
//            log.info("放行了：{}",request.getRequestURI());
            Long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return ;
        }
        if(request.getSession().getAttribute("user") != null) {//用户登录状态拦截
//            log.info("放行了：{}",request.getRequestURI());
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return ;
        }

        if(request.getSession().getAttribute("employee") == null && request.getSession().getAttribute("user") == null) {
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));//因为方法没有返回值，要通过输出流给前端提供json数据
//            log.info("未登录的：{}",request.getRequestURI());
            return ;
        }
    }
    public boolean check(String[] urls,String url){
        for (String s : urls) {
            if(PATH_MATCHER.match(s,url))//被匹配的字符串放在后面，位置反了会有问题。
                return true;
        }
        return false;
    }
}
