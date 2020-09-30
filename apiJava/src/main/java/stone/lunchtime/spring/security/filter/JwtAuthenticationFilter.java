// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.spring.security.filter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import stone.lunchtime.dto.in.LoginDtoIn;
import stone.lunchtime.dto.out.UserDtoOut;

/**
 * Used for authentication.
 *
 * https://dev.to/keysh/spring-security-with-jwt-3j76
 */
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter implements SecurityConstants {
	private static final Logger LOG = LogManager.getLogger();

	private final Environment env;

	private final AuthenticationManager authenticationManager;

	/**
	 * Constructor of the object.
	 *
	 * @param pAuthenticationManager the authentication manager
	 * @param pEnv                   environment information
	 */
	public JwtAuthenticationFilter(AuthenticationManager pAuthenticationManager, Environment pEnv) {
		this.authenticationManager = pAuthenticationManager;
		this.setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
		this.env = pEnv;
	}

	/**
	 * Can handle email,password as parameter or as JSOn in body
	 * ({"email":"xxx","password":"xxx"}).
	 *
	 * @param request  the request
	 * @param response the response
	 * @throws AuthenticationException if an error occurred
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

		final String remoteIP = request.getRemoteAddr();

		String username = request.getParameter("email");
		String password = request.getParameter("password");
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			JwtAuthenticationFilter.LOG.debug(
					"[{}] --> JwtAuthenticationFilter.attemptAuthentication(email, password) as Json in Body",
					remoteIP);
			// Look as JSon in the body
			String body = null;
			try {
				body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
				// Level is in trace but we should hide password if present in body or if in
				// production
				JwtAuthenticationFilter.LOG.trace(
						"[{}] --> JwtAuthenticationFilter.attemptAuthentication(email, password) Body={}", remoteIP,
						body);
				ObjectMapper mapper = new ObjectMapper();
				LoginDtoIn loginDtoIn = mapper.readValue(body, LoginDtoIn.class);
				username = loginDtoIn.getEmail();
				password = loginDtoIn.getPassword();
			} catch (Exception lExp) {
				JwtAuthenticationFilter.LOG.error(
						"[{}] --> JwtAuthenticationFilter.attemptAuthentication - Error, your JSon is not right!, found {}, should be something like {\"email\":\"toto@gmail.com\",\"password\":\"bonjour\"}. DO NOT use simple quote!",
						remoteIP, body, lExp);
			}
		} else {
			JwtAuthenticationFilter.LOG.debug(
					"[{}] --> JwtAuthenticationFilter.attemptAuthentication(email, password) as parameter", remoteIP);
		}

		JwtAuthenticationFilter.LOG.debug("[{}] --> JwtAuthenticationFilter.attemptAuthentication({}, [PROTECTED])",
				remoteIP, username);

		Authentication authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		Authentication result = this.authenticationManager.authenticate(authenticationToken);
		JwtAuthenticationFilter.LOG.debug(
				"[{}] --> JwtAuthenticationFilter.attemptAuthentication is ok - User id is {}", remoteIP,
				((UserDtoOut) result.getDetails()).getId());

		return result;
	}

	/**
	 * Will build the JWT Token if authentication is ok.
	 *
	 * @param request        the request
	 * @param response       the response
	 * @param filterChain    filters
	 * @param authentication login/pwd information taken from AuthenticationManager
	 * @throws AuthenticationException if an error occurred
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain, Authentication authentication) {
		final String remoteIP = request.getRemoteAddr();
		String userName = (String) authentication.getPrincipal();

		List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		UserDtoOut ue = (UserDtoOut) authentication.getDetails();
		Claims claims = new DefaultClaims();
		claims.put(SecurityConstants.TOKEN_USER, ue);
		claims.put(SecurityConstants.TOKEN_ROLES, roles);
		claims.setIssuer(SecurityConstants.TOKEN_ISSUER);
		claims.setAudience(SecurityConstants.TOKEN_AUDIENCE);
		claims.setSubject(userName);
		int val = Integer.parseInt(this.env.getProperty("configuration.jwt.expire.in.ms", "86400000"));
		claims.setExpiration(new Date(System.currentTimeMillis() + val));
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm
				.forName(this.env.getProperty("configuration.jwt.signature.algorithm", "none"));

		JwtBuilder builder = null;
		if (signatureAlgorithm == null || signatureAlgorithm == SignatureAlgorithm.NONE) {
			JwtAuthenticationFilter.LOG.warn("[{}] - No encryption for JWT token, this is good for testing ...",
					remoteIP);
			builder = Jwts.builder().setHeaderParam("typ", SecurityConstants.TOKEN_TYPE).setClaims(claims);
		} else {
			JwtAuthenticationFilter.LOG.debug(
					"Encryption for JWT token is {}, do not forget to set your key in the configuration file",
					signatureAlgorithm);
			// Sample key is for HS512
			byte[] signingKey = this.env.getProperty("configuration.jwt.key",
					"-KaPdSgVkXp2s5v8y/B?E(H+MbQeThWmZq3t6w9z$C&F)J@NcRfUjXn2r5u7x!A%").getBytes();
			builder = Jwts.builder().signWith(Keys.hmacShaKeyFor(signingKey), signatureAlgorithm)
					.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE).setClaims(claims);
		}

		String token = builder.compact();
		response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
	}
}
