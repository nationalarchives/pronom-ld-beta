package com.wallscope.pronombackend;

import com.wallscope.pronombackend.utils.TemplateUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class PronombackendApplication {

    @Bean(name = "templateUtils")
    public TemplateUtils templateUtils() {
        return new TemplateUtils();
    }

    @Bean
    public ClassLoaderTemplateResolver htmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        templateResolver.setCheckExistence(true);

        return templateResolver;
    }

    @Bean
    public ClassLoaderTemplateResolver xmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("xml/");
        templateResolver.setSuffix(".xml");
        templateResolver.setTemplateMode(TemplateMode.XML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(2);
        templateResolver.setCheckExistence(true);

        return templateResolver;
    }

    @Bean
    public Marshaller jaxbMarshaller() {
        Map<String, Object> props = new HashMap<>();
        props.put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        Jaxb2Marshaller m = new Jaxb2Marshaller();
        m.setMarshallerProperties(props);
        m.setPackagesToScan("com.wallscope.pronombackend", "uk.gov.nationalarchives.pronom.signaturefile");
        return m;
    }

    private static final String usageStmt = "Usage: java -jar /path/to/pronombackend.jar convert-container-signatures /path/to/containerfile.xml /path/to/puidmap.csv /path/to/outFile.ttl";

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("convert-container-signatures")) {
            if ((args.length > 1 && args[1].equals("-h")) || args.length < 4) {
                System.out.println(usageStmt);
                System.exit(0);
            }
            CliKt.convert(args[1], args[2], args[3]);
            System.exit(0);
        } else {
            ApplicationContext ctx = SpringApplication.run(PronombackendApplication.class, args);
        }
    }
}

