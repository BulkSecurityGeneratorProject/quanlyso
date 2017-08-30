package logia.quanlyso.config;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import io.github.jhipster.config.JHipsterConstants;
import io.github.jhipster.config.liquibase.AsyncSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * The Class DatabaseConfiguration.
 *
 * @author Dai Mai
 */
@Configuration
@EnableJpaRepositories("logia.quanlyso.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

	/** The log. */
	private final Logger		log	= LoggerFactory.getLogger(DatabaseConfiguration.class);

	/** The env. */
	private final Environment	env;

	/**
	 * Instantiates a new database configuration.
	 *
	 * @param env the env
	 */
	public DatabaseConfiguration(Environment env) {
		this.env = env;
	}

	/**
	 * Liquibase.
	 *
	 * @param taskExecutor the task executor
	 * @param dataSource the data source
	 * @param liquibaseProperties the liquibase properties
	 * @return the spring liquibase
	 */
	@Bean
	public SpringLiquibase liquibase(@Qualifier("taskExecutor") TaskExecutor taskExecutor,
			DataSource dataSource, LiquibaseProperties liquibaseProperties) {

		// Use liquibase.integration.spring.SpringLiquibase if you don't want Liquibase to start
		// asynchronously
		SpringLiquibase liquibase = new AsyncSpringLiquibase(taskExecutor, this.env);
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:config/liquibase/master.xml");
		liquibase.setContexts(liquibaseProperties.getContexts());
		liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		if (this.env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_NO_LIQUIBASE)) {
			liquibase.setShouldRun(false);
		}
		else {
			liquibase.setShouldRun(liquibaseProperties.isEnabled());
			this.log.debug("Configuring Liquibase");
		}
		return liquibase;
	}

	/**
	 * Hibernate 5 module.
	 *
	 * @return the hibernate 5 module
	 */
	@Bean
	public Hibernate5Module hibernate5Module() {
		return new Hibernate5Module();
	}
}
