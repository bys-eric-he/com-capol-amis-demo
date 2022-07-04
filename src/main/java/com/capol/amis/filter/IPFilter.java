package com.capol.amis.filter;

import com.capol.amis.response.CommonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * IP黑名单
 */
@Slf4j
@WebFilter(filterName = "ipFilter", urlPatterns = "/*")
public class IPFilter extends OncePerRequestFilter {

    private List<String> forbiddenIpList = new ArrayList<>();

    public IPFilter() {
        this.forbiddenIpList.add("10.1.8.111");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        String ip = request.getRemoteAddr();
        log.info("--------- 请求IP: {} ------------", ip);
        try {
            if (this.forbiddenIpList.contains(ip)) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                ObjectMapper objectMapper = new ObjectMapper();
                out.write(objectMapper.writeValueAsString(CommonResult.failed(400, "禁止访问的IP!!!")));
                out.flush();
            } else {
                filterChain.doFilter(request, response);
                System.out.println(response);
            }
        } catch (Exception exception) {
            log.error("IP黑名单过滤异常,异常信息:" + exception.getMessage());
        }
    }
}
