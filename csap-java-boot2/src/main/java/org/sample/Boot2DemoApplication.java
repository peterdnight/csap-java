package org.sample;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@SpringBootApplication
@EnableAsync
public class Boot2DemoApplication {

	public static void main(String[] args) {
		
		
		try {
			  System.out.println(new Date());
			  InetAddress hostName = InetAddress.getLocalHost();
			  System.out.println(new Date());
			} catch (UnknownHostException e) {
			  e.printStackTrace();
			}
		
		SpringApplication.run(Boot2DemoApplication.class, args);
	}
	
	@Bean
	public TaskScheduler taskScheduler () {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix( Boot2DemoApplication.class.getSimpleName() + "@Scheduler" );
		scheduler.setPoolSize( 1 );
		return scheduler;
	}

	// configure @Async thread pool. Use named pools for workload segregation:
	// @Async("CsapAsync")

	final public static String ASYNC_EXECUTOR = "CsapAsynExecutor";

	@Bean ( ASYNC_EXECUTOR )
	public TaskExecutor taskExecutor () {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize( 5 );
		taskExecutor.setQueueCapacity( 100 );
		taskExecutor.afterPropertiesSet();
		return taskExecutor;
	}

}
