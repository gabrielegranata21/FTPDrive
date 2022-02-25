package com.mimesi.ftpdrive.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({"com.mimesi.*"})
@EnableAutoConfiguration
@EnableScheduling
public class FtpDriveApplication {

	public static void main(String[] args) {
		SpringApplication.run(FtpDriveApplication.class, args);
	}

}
