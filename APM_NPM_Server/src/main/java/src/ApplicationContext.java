package src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableConfigurationProperties
@SpringBootApplication
@EnableAspectJAutoProxy
public class ApplicationContext {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationContext.class, args);
	}
}
