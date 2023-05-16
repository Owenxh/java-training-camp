package io.github.owenxh.irina;

import io.github.owenxh.irina.properties.IrinaServerProperties;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Irina server
 *
 * @author Owen.Yuan
 * @since 1.0
 */
@EnableConfigurationProperties(IrinaServerProperties.class)
@SpringBootApplication
public class IrinaServer {

    @Bean
    public MapperFacade benaMapperFacade() {
        return new DefaultMapperFactory.Builder().build().getMapperFacade();
    }

    public static void main(String[] args) {
        SpringApplication.run(IrinaServer.class, args);
    }
}
