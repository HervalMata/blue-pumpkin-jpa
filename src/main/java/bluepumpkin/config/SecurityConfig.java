package bluepumpkin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import bluepumpkin.service.UserService;

@EnableWebSecurity	
@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled=true)  
@ImportResource(value = "classpath:spring-security-context.xml")
public class SecurityConfig {

	  @Bean
	  public UserService userService() {
		  return new UserService();
	  }
	  
	  @Bean
	  public TokenBasedRememberMeServices rememberMeServices() {
		  return new TokenBasedRememberMeServices("remember-me-key", userService());
	  }
	  
	  @Bean
	  public PasswordEncoder passwordEncoder() {
		  return new StandardPasswordEncoder();
	  }
	
}
