package az.azal.skyflow;

import az.azal.skyflow.auth.dto.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SkyflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkyflowApplication.class, args);
	}

}
