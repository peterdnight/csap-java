package org.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = A_Simple_Application.Simple_Application.class, webEnvironment = WebEnvironment.RANDOM_PORT )
@ActiveProfiles ( "junit" )
@DirtiesContext
public class A_Simple_Application {
	final static private Logger logger = LoggerFactory.getLogger( A_Simple_Application.class );

	@BeforeClass
	// @Before
	static public void setUpBeforeClass ()
			throws Exception {

		System.out.println( "Starting logging" );
	}

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * 
	 * Simple test app that excludes security autoconfiguration
	 *
	 */
//	@SpringBootApplication(exclude = {
//            org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
//            org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class})
	
	@SpringBootApplication
	public static class Simple_Application {

		@RestController
		static public class Hello {

			@GetMapping ( "/hi" )
			public String hi () {
				return "Hello" +
						LocalDateTime.now()
							.format( DateTimeFormatter
								.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );
			}

			@Inject
			ObjectMapper jsonMapper;

		}

	}

	@Test
	public void load_context () {

		logger.info( "beans loaded: {}", applicationContext.getBeanDefinitionCount() );

		assertThat( applicationContext.getBeanDefinitionCount() )
			.as( "Spring Bean count" )
			.isGreaterThan( 200 );

		// Assert.assertFalse( true);

	}
	

    @LocalServerPort
    private int testPort;
    

	@Inject
	RestTemplateBuilder restTemplateBuilder;
	
	
	@Test
	public void http_get_hi_from_simple_app() throws Exception {
		String simpleUrl = "http://localhost:" + testPort +"/hi" ;
		
		logger.info( "hitting url: {}" , simpleUrl);
		// mock does much validation.....

		TestRestTemplate restTemplate = new TestRestTemplate( restTemplateBuilder );
		
		ResponseEntity<String> response = restTemplate.getForEntity( simpleUrl, String.class ) ;
		
		logger.info( "result:\n" + response );

		assertThat( response.getBody() )
				.startsWith( "Hello") ;
	}

}
