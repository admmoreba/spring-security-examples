package com.botkul;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class Oauth2ClientApplication {

	@RequestMapping("/")
	public String home(Principal user){
		return "Hello "+ user.getName();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Oauth2ClientApplication.class, args);
		
	}
}
