package org.hl7.davinci.atr.server.configuration;

import java.util.Properties;

import javax.sql.DataSource;


import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The Class HibernateConfiguration.
 */
@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:application.properties"})
public class HibernateConfiguration {

	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(HibernateConfiguration.class);



	/** The environment. */
	@Autowired
	private Environment environment;

	/**
	 * Session factory.
	 *
	 * @return the local session factory bean
	 */
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LOGGER.info("Entry - session factory Method in HibernateConfiguration ");

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[]{environment.getRequiredProperty("entitymanager.packagesToScan")});
		sessionFactory.setHibernateProperties(hibernateProperties());
		LOGGER.info("Exit - session factory Method in HibernateConfiguration ");
		return sessionFactory;
	}

	/**
	 * Data source.
	 *
	 * @return the data source
	 */
	@Bean
	public DataSource dataSource() {
		LOGGER.info("Entry - datasource Method in HibernateConfiguration ");

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
		dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
		dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
		dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
		LOGGER.info("Exit - dataSource Method in HibernateConfiguration ");
		return dataSource;
	}

	/**
	 * Setting Hibernate properties.
	 *
	 * @return the properties
	 */
	private Properties hibernateProperties() {
		LOGGER.info("Entry - hibernateProperties Method in HibernateConfiguration ");

		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		properties.put("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));
		LOGGER.info("Exit - hibernateProperties Method in HibernateConfiguration ");

		return properties;
	}

	/**
	 * Transaction manager.
	 *
	 * @param s the s
	 * @return the hibernate transaction manager
	 */
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		LOGGER.info("Entry - transactionManager Method in HibernateConfiguration ");

		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		LOGGER.info("Exit - transactionManager Method in HibernateConfiguration ");

		return txManager;
	}
}