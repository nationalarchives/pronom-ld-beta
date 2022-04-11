package com.wallscope.pronombackend;

import com.wallscope.pronombackend.utils.TemplateUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
public class PronombackendApplication {

    @Bean(name = "templateUtils")
    public TemplateUtils templateUtils() {
        return new TemplateUtils();
    }

    @Bean
    public ClassLoaderTemplateResolver xmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("xml/");
        templateResolver.setSuffix(".xml");
        templateResolver.setTemplateMode(TemplateMode.XML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(2);

        return templateResolver;
    }


    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(PronombackendApplication.class, args);
    }
}

