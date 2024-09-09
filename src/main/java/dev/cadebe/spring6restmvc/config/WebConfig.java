package dev.cadebe.spring6restmvc.config;

import dev.cadebe.spring6restmvc.controller.StringToBeerStyleConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToBeerStyleConverter());
    }
}