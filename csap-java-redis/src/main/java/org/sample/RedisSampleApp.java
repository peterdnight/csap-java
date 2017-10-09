package org.sample;

import org.csap.CsapBootApplication;
import org.csap.integations.CsapSecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


@CsapBootApplication
public class RedisSampleApp {

	final Logger logger = LoggerFactory.getLogger( HelloClient.class );

	public static void main(String[] args) {
		// devTools will post process param
		SpringApplication.run( RedisSampleApp.class, args );
	}
	
	/**
	 * 
	 * 
	 *  Persisted session stored in redis
	 * 
	 */

	@EnableRedisHttpSession 
	public static class HttpSessionConfig { }


	@Bean
	public CsapSecurityConfiguration.CustomHttpSecurity customRules() {
		
		
		// Unprotect the redis info endpoint for csap metric collection
		return ( httpSecurity -> {
			httpSecurity
					.authorizeRequests()
					.antMatchers( "/redis/info" )
					.permitAll();
		}) ;

	}

	// Spring boot pre configures many services in runtime. But you can override trivially as 
	// shown below for redis
//	@Bean
//	public RedisConnectionFactory jedisConnectionFactory() {
//		RedisConnectionFactory factory = null ;
//	  RedisSentinelConfiguration sentinelConfig=null;
//	try {
//		logger.info( "Created redis factory" );
//		sentinelConfig = new RedisSentinelConfiguration() .master("mymaster")
//		  .sentinel("csap-dev01", 26379);
//		factory = new JedisConnectionFactory(sentinelConfig) ;
//	} catch (Exception e) {
//		logger.error( "Failed redis" );
//	}
//	  return factory;
//	}

}
