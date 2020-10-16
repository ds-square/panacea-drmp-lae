package org.panacea.drmp.lae.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI ageOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("LAE REST API")
						.description("REST API for Likelyhood Analysis Engine (LAE)")
						.version("1.0.0")
						.license(new License().name("License: LGPL v3.0").url("https://www.gnu.org/licenses/lgpl-3.0.html")));

	}

}
