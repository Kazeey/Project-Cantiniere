// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019 -
// -# Email: admin@ferretrenaud.fr -
// -# All Rights Reserved. -
// -#--------------------------------------

package stone.lunchtime.spring.security.filter.unsecured;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import stone.lunchtime.dto.out.UserDtoOut;
import stone.lunchtime.entity.EntityStatus;
import stone.lunchtime.entity.RoleLabel;
import stone.lunchtime.entity.Sex;
import stone.lunchtime.spring.security.filter.JwtAuthorizationFilter;

/**
 * Used when asking for a secured information (but used here for unsecured).
 */
public class JwtAuthorizationFilterUnsecured extends JwtAuthorizationFilter {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Constructor of the object.
	 *
	 * @param pAuthenticationManager the authentication manager
	 * @param pEnv environment information
	 */
	public JwtAuthorizationFilterUnsecured(AuthenticationManager pAuthenticationManager, Environment pEnv) {
		super(pAuthenticationManager, pEnv);
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
		JwtAuthorizationFilterUnsecured.LOG.debug(
				"[{}] <-- JwtAuthorizationFilterUnsecured.doFilterInternal - {} - JWT token is 'we do not care'",
				remoteIP, url);

		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(RoleLabel.ROLE_LUNCHLADY.toString()));
		authorities.add(new SimpleGrantedAuthority(RoleLabel.ROLE_USER.toString()));

		JwtAuthorizationFilterUnsecured.LOG.warn(
				"[{}] --- JwtAuthorizationFilterUnsecured.doFilterInternal - {} - You will always be the same user when unsecured profile is used",
				remoteIP, url);
		UsernamePasswordAuthenticationToken resu = new UsernamePasswordAuthenticationToken("toto@gmail.com", null,
				authorities);
		UserDtoOut userDtoOut = new UserDtoOut();
		userDtoOut.setEmail("toto@gmail.com");
		userDtoOut.setFirstname("Durant");
		userDtoOut.setId(Integer.valueOf(1));
		userDtoOut.setIsLunchLady(Boolean.TRUE);
		userDtoOut.setName("Albert");
		userDtoOut.setSex(Sex.MAN.getValue());
		userDtoOut.setStatus(EntityStatus.ENABLED.getValue());
		userDtoOut.setWallet(BigDecimal.valueOf(500D));
		resu.setDetails(userDtoOut);

		SecurityContextHolder.getContext().setAuthentication(resu);
		JwtAuthorizationFilterUnsecured.LOG.debug(
				"[{}] <-- JwtAuthorizationFilterUnsecured.doFilterInternal - {} - OK - Set authentication back",
				remoteIP, url);
		filterChain.doFilter(request, response);
	}

}
