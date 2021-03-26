package io.penguinstats.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import io.penguinstats.constant.Constant;

@WebFilter(urlPatterns = {"/api/v2/*"})
public class CompatibleVersionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse)response;
        res.setHeader(Constant.CustomHeader.X_PENGUIN_COMPATIBLE, Constant.CompatibleVersion.FRONTEND_V2_3_4_0);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

}
