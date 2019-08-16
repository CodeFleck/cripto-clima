package br.com.codefleck.criptoclima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CriptoclimaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CriptoclimaApplication.class, args);

		//use code below to generate JChart
//		SpringApplicationBuilder builder = new SpringApplicationBuilder(CriptoclimaApplication.class);
//		builder.headless(false);
//		ConfigurableApplicationContext context = builder.run(args);
	}
}
