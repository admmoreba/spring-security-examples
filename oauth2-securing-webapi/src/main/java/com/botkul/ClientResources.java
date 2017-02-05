package com.botkul;

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

/**
 * @author Balaji More
 * @created Feb 5, 2017
 * @email balaji.botkul@gmail.com
 */
public class ClientResources {
	
	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client=new AuthorizationCodeResourceDetails();
	
	@NestedConfigurationProperty
	private ResourceServerProperties resource=new ResourceServerProperties();

	public AuthorizationCodeResourceDetails getClient() {
		return client;
	}

	public ResourceServerProperties getResource() {
		return resource;
	}
}
