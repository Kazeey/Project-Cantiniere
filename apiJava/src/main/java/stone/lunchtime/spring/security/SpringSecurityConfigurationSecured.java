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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
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
import stone.lunchtime.spring.security.filter.JwtAuthorizationFilter;
import stone.lunchtime.spring.security.filter.SecurityConstants;

/**
 * Security configuration. <br>
 *
 * This is default configuration. <br> Use 'unsecured' profile if you do not want to be in secured mode.
 *
 * Handle ONLY Spring Security
 */
@SpringBootConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@ConditionalOnMissingBean(SpringSecurityConfigurationUnsecured.class)
public class SpringSecurityConfigurationSecured extends WebSecurityConfigurerAdapter {
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
		SpringSecurityConfigurationSecured.LOG
				.debug("SpringSecurityConfigurationSecured - Link with our Authentication provider");
		// Our Authentication Manager
		auth.authenticationProvider(this.customAuthenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		SpringSecurityConfigurationSecured.LOG.debug("SpringSecurityConfigurationSecured - Apply rules");

		// Keep cors enable here, otherwise configuration of it is not applied
		http.csrf().disable().cors();

		http.authorizeRequests().antMatchers("/", // Root
				"/favicon.ico*", //
				"/index.jsp*", //
				"/csrf/**", //
				"/v3/api-docs/**", // Swagger
				"/v3/api-docs**", //
				"/configuration/**", //
				"/swagger-ui.html", //
				"/swagger-resources/**", //
				"/swagger*/**", //
				"/webjars/**", //
				"/h2/**", // h2 + frameOptions
				"/forgotpassword", // Lunchtime API
				"/constraint/findall", //
				"/constraint/find/**", //
				"/ingredient/find/**", //
				"/ingredient/findimg/**", //
				"/meal/find/**", //
				"/meal/findimg/**", //
				"/meal/findallavailableforweek/**", //
				"/meal/findallavailablefortoday", //
				"/menu/findallavailablefortoday", //
				"/menu/findallavailableforweek/**", //
				"/menu/find/**", //
				"/menu/findimg/**", //
				"/user/register", //
				"/css/**", // Resources
				"/fonts/**", //
				"/img/**", //
				"/javadoc/**", //
				"/js/**", //
				"/error", //
				"license.txt").anonymous();

		// For H2
		http.headers().frameOptions().disable();

		// The JWT filter
		// We add the two filters and set session policy to Stateless
		http.authorizeRequests().and()
				.addFilterBefore(new JwtAuthenticationFilter(this.authenticationManager(), this.env),
						UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthorizationFilter(this.authenticationManager(), this.env),
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

		// Other constraint are handled at the controller level with annotations
		http.authorizeRequests().anyRequest().authenticated();
	}

}
