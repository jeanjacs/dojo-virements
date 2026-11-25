package fr.dojo.virements.exposition.config;

import org.apache.catalina.startup.Tomcat;
import org.h2.server.web.JakartaWebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
class H2ConsoleConfiguration {
    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> h2ConsoleServlet(
            @Value("${spring.h2.console.path:/h2-console}") String path) {
        return factory -> factory.addContextCustomizers(context -> {
            var servlet = Tomcat.addServlet(context, "h2-console", new JakartaWebServlet());
            servlet.setLoadOnStartup(1);
            servlet.addMapping(path + "/*");
        });
    }
}
