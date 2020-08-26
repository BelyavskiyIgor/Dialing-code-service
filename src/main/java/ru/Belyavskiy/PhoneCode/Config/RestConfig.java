package ru.Belyavskiy.PhoneCode.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestConfig {
    @Bean
    public RestTemplate restTemplate(){
        DefaultUriBuilderFactory defaultUriTemplateHandler = new DefaultUriBuilderFactory("http://country.io/");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(defaultUriTemplateHandler);

        return  restTemplate;
    }
}
