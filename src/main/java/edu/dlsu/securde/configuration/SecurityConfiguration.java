package edu.dlsu.securde.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.dlsu.securde.handler.CustomAuthenticationFailureHandler;
import edu.dlsu.securde.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Value("${spring.queries.users-query}")
	private String usersQuery;

	@Value("${spring.queries.roles-query}")
	private String rolesQuery;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().usersByUsernameQuery(usersQuery).authoritiesByUsernameQuery(rolesQuery)
				.dataSource(dataSource).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
				.antMatchers("/").permitAll()
				.antMatchers("/login").permitAll()
				.antMatchers("/forgetPassword").permitAll()
				.antMatchers("/processForgetPassword").permitAll()
				.antMatchers("/step2ProcessForgetPassword").permitAll()
				.antMatchers("/changePasswordForget").permitAll()
				.antMatchers("/registration").permitAll()
				.antMatchers("/registration-error").permitAll()
				.antMatchers("/loginLocked").permitAll()
				.antMatchers("/error").permitAll()
				.antMatchers("/user/**").hasAuthority("USER")
				.antMatchers("/admin/**").hasAuthority("ADMIN")
				.antMatchers("/manager/**").hasAuthority("MANAGER")
				.antMatchers("/staff/**").hasAuthority("STAFF")
				.antMatchers("/ViewItem").hasAnyAuthority("STAFF","MANAGER")
				.antMatchers("/editItem").hasAnyAuthority("STAFF","MANAGER")
				.antMatchers("/deleteItem").hasAnyAuthority("STAFF","MANAGER")
				.antMatchers("/addItem").hasAnyAuthority("STAFF","MANAGER")
				.anyRequest().authenticated()
//				.and().requiresChannel()
//				.antMatchers("/user/**").requiresSecure()
				
				.and().formLogin()
				.loginPage("/login")
				.failureHandler(customAuthenticationFailureHandler)
				.successHandler(customAuthenticationSuccessHandler)
//				.failureUrl("/login?error=true")
//				.defaultSuccessUrl("/user/startUp")
//				.defaultSuccessUrl("/checkPoint")
				.usernameParameter("username")
				.passwordParameter("password")
				
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/").and().exceptionHandling()
				.accessDeniedPage("/access-denied")
				
				.and()
				//prevent attacks based on MIME-type confusion..
				.headers().contentTypeOptions()
				.and()
				.xssProtection()
				.and()
				.cacheControl()
				.and()
				//protocol down grade attacks and cookie hijacking..
				.httpStrictTransportSecurity()
				.and()
				//prevent click jacking..
				.frameOptions();
				
//				.and()
//				.sessionManagement()
//				.maximumSessions(1)
//				.expiredUrl("/login")
//				.maxSessionsPreventsLogin(true);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
	}

}