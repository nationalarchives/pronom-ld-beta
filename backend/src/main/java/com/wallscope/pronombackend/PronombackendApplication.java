package com.wallscope.pronombackend;

import com.wallscope.pronombackend.config.ApplicationConfig;
import com.wallscope.pronombackend.utils.TemplateUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PronombackendApplication {

    @Bean(name = "templateUtils")
    public TemplateUtils templateUtils() {
        return new TemplateUtils(ApplicationConfig.MARKDOWN_DIR);
    }


    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(PronombackendApplication.class, args);
    }
}

