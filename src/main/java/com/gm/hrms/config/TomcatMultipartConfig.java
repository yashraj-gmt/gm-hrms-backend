//package com.gm.hrms.config;
//
//// ✅ Spring Boot 4.x — new package (no longer web.embedded.tomcat)
//import org.springframework.boot.tomcat.TomcatConnectorCustomizer;
//import org.apache.coyote.http11.AbstractHttp11Protocol;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatMultipartConfig {
//
//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatMultipartCustomizer() {
//        return factory -> factory.addConnectorCustomizers(connector -> {
//            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> protocol) {
//                protocol.setMaxPartCount(-1L);
//            }
//        });
//    }
//}