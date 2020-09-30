// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.spring.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import stone.lunchtime.spring.security.filter.JwtAuthenticationFilter;
import stone.lunchtime.spring.security.filter.SecurityConstants;
import stone.lunchtime.spring.security.filter.unsecured.JwtAuthorizationFilterUnsecured;

/**
 * Security configuration when you do not want it. <br>
 *
 * This configuration can be used when the profile 'unsecured' is activated.
 *
 * When used, you will never need to authenticate, and will always be a lunch lady.
 */
@SpringBootConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Profile("unsecured")
public class SpringSecurityConfigurationUnsecured extends WebSecurityConfigurerAdapter {
	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	protected Environment env;
	@Autowired
	protected AuthenticationProvider customAuthenticationProvider;

	/**
	 * Global CORS configuration.
	 *
	 * @return global cors configuration for Spring Security.
	 */
	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		config.addExposedHeader("WWW-Authenticate");
		config.addExposedHeader("Access-Control-Allow-Origin");
		config.addExposedHeader("Access-Control-Allow-Headers");
		// In order to see the token for Angular
		config.addExposedHeader(SecurityConstants.TOKEN_HEADER);

		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) {
		SpringSecurityConfigurationUnsecured.LOG
				.debug("SpringSecurityConfigurationUnsecured - Link with our Authentication provider");
		// Our Authentication Manager
		auth.authenticationProvider(this.customAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		SpringSecurityConfigurationUnsecured.LOG.debug("SpringSecurityConfigurationUnsecured - Apply rules");

		// Keep cors enable here, otherwise configuration of it is not applied
		http.csrf().disable().cors();

		// For H2
		http.headers().frameOptions().disable();

		// The JWT filter that will always say that you are lunchlady
		// We add the two filters and set session policy to Stateless
		http.authorizeRequests().and()
				.addFilterBefore(new JwtAuthenticationFilter(this.authenticationManager(), this.env),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthorizationFilterUnsecured(this.authenticationManager(), this.env),
						UsernamePasswordAuthenticationFilter.class)
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		ExceptionHandlingConfigurer<HttpSecurity> exceptionHandling = http.authorizeRequests().and()
				.exceptionHandling();
		exceptionHandling.authenticationEntryPoint(new RestAuthenticationEntryPoint());
		exceptionHandling.accessDeniedHandler(new AccesDeniedHandler());

		// For logout, simply send 200
		http.authorizeRequests().and().logout().clearAuthentication(true)
				.logoutSuccessHandler((pRequest, pResponse, pAuthentication) -> pResponse.setStatus(200));

		// No login, and no logout
		http.authorizeRequests().and().formLogin().disable().httpBasic().disable();

		http.authorizeRequests().anyRequest().permitAll();
	}

}
