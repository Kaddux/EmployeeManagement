package com.pm.employeeservice.config;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class ApiVersionConfig implements WebMvcConfigurer {
    private static final String BASE_PATH = "/api/v1/";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer){
        configurer.addPathPrefix(BASE_PATH,
                clazz -> clazz.isAnnotationPresent(RestController.class));
    }

}
