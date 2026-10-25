package com.fst.ms_commandes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsCommandesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCommandesApplication.class, args);
	}

}
