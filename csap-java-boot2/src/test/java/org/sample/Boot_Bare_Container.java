package org.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 
 * Absolutely minimal loading of spring boot - useful for testing with
 * configurability
 * 
 * @author peter.nightingale
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = Boot_Bare_Container.Bare_Application.class )
@ConfigurationProperties ( prefix = "test" )
@ActiveProfiles ( "company" )
@DirtiesContext
public class Boot_Bare_Container {
	
	final static private Logger logger = LoggerFactory.getLogger( Boot_Bare_Container.class );

	final static String CUSTOM_CONFIGURATION ="file:" + System.getProperty( "user.home" ) + "/csap/" ;
	
	final static String OVERWRITTEN = "set by profile yml file";
	private String simple = OVERWRITTEN;

	@BeforeClass
	static public void setUpBeforeClass ()
			throws Exception {

		logger.warn( "Updating configuration paths: {}" , CUSTOM_CONFIGURATION);
		System.setProperty( "spring.config.location", CUSTOM_CONFIGURATION );
	}

	@Autowired
	private ApplicationContext spring;

	@Autowired
	private Environment springEnvironment;

	/**
	 * 
	 * @SpringBootConfiguration - ~10 beans: absolutely minimal boot config
	 * @SpringBootApplication - ~250 beans (based on pom), not counting app
	 *                        beans
	 *
	 */
	@SpringBootConfiguration
	@ImportAutoConfiguration ( classes = {
			PropertyPlaceholderAutoConfiguration.class,
			ConfigurationPropertiesAutoConfiguration.class } )
	public static class Bare_Application {
	}

	@Test
	public void load_context () {

		ConfigurationPropertiesBindingPostProcessor p;
		logger.info( "Number of Beans loaded: {}", spring.getBeanDefinitionCount() );

		logger.debug( "beans loaded: {}", Arrays.asList( spring.getBeanDefinitionNames() ) );

		logger.info( "simple from env: {}, from config props: {}",
			springEnvironment.getProperty( "test.simple" ),
			simple );

		assertThat( spring.getBeanDefinitionCount() )
			.as( "Spring Bean count" )
			.isLessThan( 20 );

		/*
		 *  If this fails - make sure 
		 */
		assertThat( simple )
			.as( "Verify test properties loaded" )
			.isNotEqualTo( OVERWRITTEN );

	}

	public String getSimple () {
		return simple;
	}

	public void setSimple ( String simple ) {
		this.simple = simple;
	}

}
