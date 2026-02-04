package com.mf.mp63;

import com.mf.mp63.notifications.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;


import com.morefun.mpos.sdk.constants.EnumConnectMode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.PrintStream;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class MfMp63WebApplication {

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger("SystemErrRedirect");
		System.setErr(new PrintStream(new LoggingOutputStream(logger), true));

		SpringApplicationBuilder builder = new SpringApplicationBuilder(MfMp63WebApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
		//DeviceHelper.getInstance().init(EnumConnectMode.HID, 0);
		NotificationService notificationService = context.getBean("notificationService", NotificationService.class);
		notificationService.showCustomNotification(null,"Morefun service started successfully");
	}

	@Bean
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*");
			}
		};
	}

}
