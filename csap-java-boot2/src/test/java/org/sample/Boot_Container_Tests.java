package org.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = Boot2DemoApplication.class )
@ActiveProfiles ( "junit" )
public class Boot_Container_Tests {
	

	final Logger logger = LoggerFactory.getLogger( getClass() );
	

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void verify_that_spring_context_initialized () {
		
		logger.info( "Beans loaded: {}", applicationContext.getBeanDefinitionCount() );

		assertThat( applicationContext.getBeanDefinitionCount() )
			.as( "Spring Bean count" )
			.isGreaterThan( 100 );
	}

}
