package com.capol.amis.config;


import com.capol.amis.filter.RepeatableFilter;
import com.capol.amis.interceptor.RepeatSubmitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 防重复提交配置
 */
@Slf4j
@Configuration
public class RepeatSubmitConfig implements WebMvcConfigurer {

    @Autowired
    private RepeatSubmitInterceptor repeatSubmitInterceptor;

    /**
     * 添加防重复提交拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("-->WebMvcConfigurer配置启动，注册防止重复提交拦截器, 并对Swagger-UI资源放行。");

        //排除 swagger 访问的路径配置
        String[] swaggerExcludes = new String[]{
                "/swagger-resources/**",
                "/webjars/**",
                "/v3/**",
                "/doc.html",
                "/swagger-ui/**"
        };

        //注册防止重复提交拦截器, 并对Swagger-UI资源放行
        registry.addInterceptor(repeatSubmitInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(swaggerExcludes);
    }

    /**
     * 静态资源
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * 生成防重复提交过滤器（重写request）
     *
     * @return FilterRegistrationBean
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean<?> createRepeatableFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}
