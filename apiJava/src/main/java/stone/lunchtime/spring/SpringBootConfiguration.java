// -#--------------------------------------
// -# ©Copyright Ferret Renaud 2019       -
// -# Email: admin@ferretrenaud.fr        -
// -# All Rights Reserved.                -
// -#--------------------------------------


package stone.lunchtime.spring;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main class for starting the application.
 */
@SpringBootApplication
@EntityScan(basePackages = { "stone.lunchtime.entity" })
@ComponentScan({ "stone.lunchtime.spring.security", "stone.lunchtime.service", "stone.lunchtime.controller",
		"stone.lunchtime.dto" })
@EnableJpaRepositories({ "stone.lunchtime.dao" })
@Import(SpringOpenApiConfiguration.class)
public class SpringBootConfiguration extends SpringBootServletInitializer {
	private static final Logger LOG = LogManager.getLogger();

	private static final String SPRING_KEY_DRIVER = "spring.datasource.driver-class-name";

	/**
	 * Allow to deploy this application inside a JEE server as a WAR file. <br>
	 *
	 * Configure the application. Normally all you would need to do is to add
	 * sources (e.g. config classes) because other settings have sensible defaults.
	 * You might choose (for instance) to add default command line arguments, or set
	 * an active Spring profile.
	 *
	 * @param application a builder for the application context
	 * @return the application builder
	 * @see SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootConfiguration.class);
	}

	/**
	 * Method that will start Spring Boot and the JEE server. <br>
	 *
	 * <ul>
	 * <li>For in memory H2 data base:
	 *
	 * <pre>
	 * java -jar -Dspring.profiles.active=h2 stone.lunchtime.war
	 * </pre>
	 *
	 * </li>
	 * <li>For MySQL standard data base:
	 *
	 * <pre>
	 * java -jar -Dspring.profiles.active=mysql stone.lunchtime.war --spring.datasource.username=yourlogin --spring.datasource.password=yourpwd
	 * </pre>
	 *
	 * </li>
	 *
	 * <li>For PostgreSQL standard data base:
	 *
	 * <pre>
	 * java -jar -Dspring.profiles.active=postgresql stone.lunchtime.war --spring.datasource.username=yourlogin --spring.datasource.password=yourpwd
	 * </pre>
	 *
	 * </li>
	 * </ul>
	 *
	 * @param args some parameters
	 */
	@SuppressWarnings("squid:S2068")
	public static void main(String[] args) {
		SpringBootConfiguration.LOG.info("-- Starting Lunch Time Project - Using Spring Security -- ");
		SpringApplication springApplication = new SpringApplication(SpringBootConfiguration.class);
		ConfigurableApplicationContext context = springApplication.run(args);
		Environment insideEnv = context.getBean(Environment.class);
		if (SpringBootConfiguration.usingH2(insideEnv)) {
			SpringBootConfiguration.LOG.info("-- Using H2 in memory DBMS so:");
			SpringBootConfiguration.LOG.info(
					"--  + Data base will be created at startup with some data (see data.sql script in src/main/resources directory)");
			SpringBootConfiguration.LOG.info(
					"--  + image64 are not handled at initialization, you will only have the imagePath set in the data base.");
			SpringBootConfiguration.LOG.info("--  + H2 console is availabe at http://localhost:{}{}{}",
					insideEnv.getProperty("server.port", "8080"),
					insideEnv.getProperty("server.servlet.context-path", "/"),
					insideEnv.getProperty("spring.h2.console.path", ""));
			String h2Url = insideEnv.getProperty("spring.datasource.url", "NotFound!");
			if (h2Url.contains(";DB_CLOSE_ON_EXIT=FALSE")) {
				h2Url = h2Url.replace(";DB_CLOSE_ON_EXIT=FALSE", "");
			}
			SpringBootConfiguration.LOG.info("--  + H2 console please use {} as JDBC URL", h2Url);
			SpringBootConfiguration.LOG.info("--  + All information added will be lost at shutdown");
		} else if (SpringBootConfiguration.usingMySQL(insideEnv)) {
			SpringBootConfiguration.LOG.info("-- Using MySQL DBMS, do not forget to verify that");
			SpringBootConfiguration.LOG.info("--  + DBMS was started");
			SpringBootConfiguration.LOG.info(
					"--  + You started the application with the right login/password (using --spring.datasource.username=yourlogin --spring.datasource.password=yourpwd)");
			SpringBootConfiguration.LOG.info(
					"--  + You have created and loaded the lunchtime data base (see dump file in /database/dump directory)");
		} else if (SpringBootConfiguration.usingPostgreSQL(insideEnv)) {
			SpringBootConfiguration.LOG.info("-- Using PostgreSQL DBMS, do not forget to verify that");
			SpringBootConfiguration.LOG.info("--  + DBMS was started");
			SpringBootConfiguration.LOG.info(
					"--  + You started the application with the right login/password (using --spring.datasource.username=yourlogin --spring.datasource.password=yourpwd)");
			SpringBootConfiguration.LOG.info(
					"--  + You have created and loaded the lunchtime data base (see dump file in /database/dump directory)");
		} else if (SpringBootConfiguration.usingSQLServer(insideEnv)) {
			SpringBootConfiguration.LOG.info("-- Using SQLServer DBMS, do not forget to verify that");
			SpringBootConfiguration.LOG.info("--  + DBMS was started");
			SpringBootConfiguration.LOG.info("--  + TCP/IP port is activated and set to 1433");
			SpringBootConfiguration.LOG.info(
					"--  + You started the application with the right login/password (using --spring.datasource.username=yourlogin --spring.datasource.password=yourpwd)");
			SpringBootConfiguration.LOG.info(
					"--  + You have created and loaded the lunchtime data base (see dump file in /database/dump directory)");
		} else {
			SpringBootConfiguration.LOG.info(
					"-- Strange, you will be using DBMS {}, did you spell mysql or h2 properly?",
					insideEnv.getProperty(SpringBootConfiguration.SPRING_KEY_DRIVER, ""));
		}
		SpringBootConfiguration.LOG.info("-- Project Lunch Time is up - Go to http://localhost:{}{} -- ",
				insideEnv.getProperty("server.port", "8080"),
				insideEnv.getProperty("server.servlet.context-path", "/"));

	}

	/**
	 * Indicates if we are using H2 DBMS.
	 *
	 * @param pEnv environment
	 * @return true if we are using H2 DBMS
	 */
	public static final boolean usingH2(Environment pEnv) {
		return pEnv.getProperty(SpringBootConfiguration.SPRING_KEY_DRIVER, "").contains("h2");
	}

	/**
	 * Indicates if we are using MySQL DBMS.
	 *
	 * @param pEnv environment
	 * @return true if we are using MySQL DBMS
	 */
	public static final boolean usingMySQL(Environment pEnv) {
		return pEnv.getProperty(SpringBootConfiguration.SPRING_KEY_DRIVER, "").contains("mysql");
	}

	/**
	 * Indicates if we are using PostgreSQL DBMS.
	 *
	 * @param pEnv environment
	 * @return true if we are using PostgreSQL DBMS
	 */
	public static final boolean usingPostgreSQL(Environment pEnv) {
		return pEnv.getProperty(SpringBootConfiguration.SPRING_KEY_DRIVER, "").contains("postgresql");
	}

	/**
	 * Indicates if we are using sqlserver DBMS.
	 *
	 * @param pEnv environment
	 * @return true if we are using sqlserver DBMS
	 */
	public static final boolean usingSQLServer(Environment pEnv) {
		return pEnv.getProperty(SpringBootConfiguration.SPRING_KEY_DRIVER, "").contains("sqlserver");
	}
}
