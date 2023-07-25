package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security .config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public UserDetailsService getUserDetailService()
	{
		
		System.out.print("step1");
		return new UserDetailsServiceImple();
	}
	
	
	@Bean
	public BCryptPasswordEncoder passencoder()
	{
		System.out.print("step2");
		

		return new BCryptPasswordEncoder();
	}
	
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider()
	{
		System.out.print("step3");

		DaoAuthenticationProvider daoAuthenticationProvider =new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
		daoAuthenticationProvider.setPasswordEncoder(passencoder());
		return daoAuthenticationProvider;
		
	}
	
	
	//configure method
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		System.out.print("step4");

		auth.authenticationProvider(authenticationProvider());
			
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		System.out.print("step5");

		
		http
		.authorizeRequests()
		.antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/user/**").hasRole("USER")
		.antMatchers("/**").permitAll()
		.and()
		.formLogin().loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
		//.failureUrl("/login-fail")
		.and()
		.csrf()
		.disable();
		
		
		
	}
	
	
	
	
	
	
	
	
	
	

}
