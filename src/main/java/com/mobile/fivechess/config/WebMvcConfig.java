package com.mobile.fivechess.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Spring MVC 配置
 * @author panghai
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     *  服务器支持跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Headers",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Max-Age",
                        "X-Frame-Options")
                .allowCredentials(false)
                .maxAge(3600);
    }


    /** 注入ServerEndpointExporter，这个bean会自动注册使用了@ServerEndpoint注解
     * 要注意，如果使用独立的servlet容器,而不是直接使用springboot的内置容器
     * 就不要注入ServerEndpointExporter，因为它将由容器自己提供和管理。
     * */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}
