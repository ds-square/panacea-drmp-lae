package org.panacea.drmp.lae.configuration;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuavaConfigurator  {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer guavaModuleCustomizer() {
        return builder -> {
            builder.modules(new GuavaModule());
        };
    }
}
