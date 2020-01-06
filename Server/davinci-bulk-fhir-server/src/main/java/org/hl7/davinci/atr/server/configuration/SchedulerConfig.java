package org.hl7.davinci.atr.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** The pool size. */
	private final int POOL_SIZE = 10;

	/**
	 * Configure tasks.
	 *
	 * @param scheduledTaskRegistrar the scheduled task registrar
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		logger.info("Schudule the time, SchedulerConfig-> configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar)");
		try {
			ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
			threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
			threadPoolTaskScheduler.setThreadNamePrefix("my-scheduled-task-pool-");
			threadPoolTaskScheduler.initialize();
			scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
		} catch (Exception e) {
			logger.info("Exception in scheduling time, SchedulerConfig-> configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar)");
			e.printStackTrace();
		}
	}
}
