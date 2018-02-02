package org.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith ( SpringRunner.class )
@ContextConfiguration(classes= { Simple_Spring_Test.SimpleConfig.class })
@TestPropertySource(properties = {"csap.security.enabled=true"})
public class Simple_Spring_Test {

	
	final static private Logger logger = LoggerFactory.getLogger( Simple_Spring_Test.class );
	@Configuration
	@ComponentScan(basePackageClasses = HelloService.class, 
		    useDefaultFilters = false,
		    includeFilters = {
		        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
		        		value = HelloService.class)
		    } )
	public static class SimpleConfig {
		
	}
	@Autowired
	private ApplicationContext applicationContext;
	
	@Test
	public void init() {
		
		logger.info( "Simple Example of a SINGLE spring bean loaded, versus the default of scanning entire app" );
		
		logger.info( "beans loaded: {}\n\t {}", 
			applicationContext.getBeanDefinitionCount(),
			Arrays.asList( applicationContext.getBeanDefinitionNames() ));
	
		assertThat( applicationContext.getBeanDefinitionCount() )
			.as( "Spring Bean count" )
			.isEqualTo( 10 );
		
	}

	
}
