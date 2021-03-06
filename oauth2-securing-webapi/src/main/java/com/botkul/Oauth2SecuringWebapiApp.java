package com.botkul;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.core.annotation.Order;

/**
 * @author Balaji More
 * @created Feb 5, 2017
 * @email balaji.botkul@gmail.com
 */

@SpringBootApplication
@EnableOAuth2Client
@Order(6)
@EnableAuthorizationServer
@RestController
public class Oauth2SecuringWebapiApp extends WebSecurityConfigurerAdapter {
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilterBefore(ssoFilter(),BasicAuthenticationFilter.class)
		.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/", "/login**", "/webjars/**").permitAll()
				.anyRequest().authenticated()
		.and().logout().logoutSuccessUrl("/").permitAll()
		.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		.and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"));
		
		
	  }
	public Filter ssoFilter() {
		CompositeFilter filter=new CompositeFilter();
		List<Filter> filters=new ArrayList<>();
		filters.add(ssoFilter(facebook(),"/login/facebook"));
		filters.add(ssoFilter(github(),"/login/github"));
		
		filter.setFilters(filters);
		return filter;
	}
	public Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter=new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate oauth2RestTemplate=new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(oauth2RestTemplate);
		
		UserInfoTokenServices userInfoTokenService=new UserInfoTokenServices(client.getResource().getUserInfoUri(),client.getClient().getClientId());
		userInfoTokenService.setRestTemplate(oauth2RestTemplate);
		filter.setTokenServices(userInfoTokenService);
		return filter;
	}
	
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter){
		FilterRegistrationBean filterRegBean=new FilterRegistrationBean();
		filterRegBean.setFilter(filter);
		filterRegBean.setOrder(-100);
		return filterRegBean;
	}

	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("github")
	public ClientResources github() {
		return new ClientResources();
	}

	@RequestMapping(value={"/user", "/me"})
	  public Map<String, String> user(Principal principal) {
		Map<String,String> map=new LinkedHashMap<>();
		map.put("name", principal.getName());
	    return map;
	  }

	public static void main(String[] args) {
		SpringApplication.run(Oauth2SecuringWebapiApp.class, args);
	}
}
