// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.spring.security.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import stone.lunchtime.dto.out.UserDtoOut;

/**
 * Used when asking for a secured information.
 * https://dev.to/keysh/spring-security-with-jwt-3j76
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter implements SecurityConstants {
	private static final Logger LOG = LogManager.getLogger();

	private final Environment env;

	/**
	 * Constructor of the object.
	 *
	 * @param pAuthenticationManager the authentication manager
	 * @param pEnv environment information
	 */
	public JwtAuthorizationFilter(AuthenticationManager pAuthenticationManager, Environment pEnv) {
		super(pAuthenticationManager);
		this.env = pEnv;
	}

	/**
	 * Checks for token validity.
	 * @param request the request
	 * @param response the response
	 * @param filterChain filters
	 * @throws IOException if an error occurred
	 * @throws ServletException if an error occurred
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		final String remoteIP = request.getRemoteAddr();
		final String url = request.getRequestURL().toString();
		String jwtToken = request.getHeader(SecurityConstants.TOKEN_HEADER);
		JwtAuthorizationFilter.LOG.debug("[{}] <-- JwtAuthorizationFilter.doFilterInternal - {} - JWT token is {}",
				remoteIP, url, jwtToken);
		if (StringUtils.isEmpty(jwtToken) || !jwtToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			JwtAuthorizationFilter.LOG
					.warn("[{}] <-- JwtAuthorizationFilter.doFilterInternal - {} - JWT token is Empty", remoteIP, url);
			filterChain.doFilter(request, response);
			return;
		}

		Authentication authentication = this.getAuthentication(jwtToken, remoteIP);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		JwtAuthorizationFilter.LOG.debug(
				"[{}] <-- JwtAuthorizationFilter.doFilterInternal - {} - OK - Set authentication back", remoteIP, url);
		filterChain.doFilter(request, response);
	}

	/**
	 * Rebuild UsernamePasswordAuthenticationToken for SpringSecurity from JWT token.
	 *
	 * @param token the JWT token
	 * @return the token for Spring Security
	 */
	private UsernamePasswordAuthenticationToken getAuthentication(String token, String remoteIp) {
		JwtAuthorizationFilter.LOG.debug("[{}] --> JwtAuthorizationFilter.getAuthentication - Token - {}", remoteIp,
				token);
		if (!StringUtils.isEmpty(token)) {
			try {
				byte[] signingKey = this.env.getProperty("configuration.jwt.key",
						"-KaPdSgVkXp2s5v8y/B?E(H+MbQeThWmZq3t6w9z$C&F)J@NcRfUjXn2r5u7x!A%").getBytes();

				Jws<Claims> parsedToken = Jwts.parserBuilder().setSigningKey(signingKey).build()
						.parseClaimsJws(token.replace(SecurityConstants.TOKEN_PREFIX, ""));

				String username = parsedToken.getBody().getSubject();

				Collection<? extends GrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
						.get(SecurityConstants.TOKEN_ROLES)).stream()
								.map(authority -> new SimpleGrantedAuthority((String) authority))
								.collect(Collectors.toList());

				if (!StringUtils.isEmpty(username)) {
					UsernamePasswordAuthenticationToken resu = new UsernamePasswordAuthenticationToken(username, null,
							authorities);
					@SuppressWarnings("unchecked")
					Map<String, ?> userDto = (Map<String, ?>) parsedToken.getBody().get(SecurityConstants.TOKEN_USER);
					JwtAuthorizationFilter.LOG.trace("val {}", userDto);
					UserDtoOut userDtoOut = new UserDtoOut(userDto);
					resu.setDetails(userDtoOut);
					JwtAuthorizationFilter.LOG.warn(
							"[{}] <-- JwtAuthorizationFilter.getAuthentication - Token was pushed into Spring Security, {}",
							remoteIp, resu);
					return resu;
				}
			} catch (Exception exception) {
				JwtAuthorizationFilter.LOG.warn("[{}] - JwtAuthorizationFilter.getAuthentication : {} failed", remoteIp,
						token, exception);
			}
		}
		JwtAuthorizationFilter.LOG.warn("[{}] <-- JwtAuthorizationFilter.getAuthentication - NO Token found", remoteIp);
		return null;
	}

}
