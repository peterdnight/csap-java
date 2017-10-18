package org.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith ( SpringRunner.class )
@SpringBootTest ( classes = Boot2DemoApplication.class )
@ActiveProfiles ( "junit" )
public class Http_Tests {

	final Logger logger = LoggerFactory.getLogger( getClass() );

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp ()
			throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup( this.wac ).build();

	}

	@After
	public void tearDown ()
			throws Exception {
	}

	@Test
	public void http_get_landing_page ()
			throws Exception {
		logger.info( "simple mvc test" );
		// mock does much validation.....
		ResultActions resultActions = mockMvc.perform(
			get( "/" )
				.param( "sampleParam1", "sampleValue1" )
				.param( "sampleParam2", "sampleValue2" )
				.accept( MediaType.TEXT_PLAIN ) );

		//
		String landingPage_asHtml = resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentType( "text/html;charset=UTF-8" ) )
			.andReturn().getResponse().getContentAsString();

		logger.debug( "result:\n" + landingPage_asHtml );

		assertThat( landingPage_asHtml )
			.as( "Landing page contains header" )
			.contains( "<header>Simple Boot 2 Core Demo</header>" );

	}

	@Test
	public void http_get_hello_endpoint ()
			throws Exception {
		logger.info( "simple rest test" );
		// mock does much validation.....
		ResultActions resultActions = mockMvc.perform(
			get( "/hello" )
				.accept( MediaType.TEXT_PLAIN ) );

		//
		String get_hello_response = resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentType( "text/plain;charset=UTF-8" ) )
			.andReturn().getResponse().getContentAsString();

		logger.info( "result: {}" + get_hello_response );

		assertThat( get_hello_response )
			.as( "Simple get on hello method" )
			.startsWith( "Hello" );

	}
	
	

	@Test
	public void http_get_json_endpoint ()
			throws Exception {
		logger.info( "simple rest test" );
		// mock does much validation.....
		ResultActions resultActions = mockMvc.perform(
			get( "/api/hi" )
				.accept( MediaType.APPLICATION_JSON ) );

		//
		String get_hello_response = resultActions
			.andExpect( status().isOk() )
			.andExpect( content().contentType( MediaType.APPLICATION_JSON_UTF8  ) )
			.andReturn().getResponse().getContentAsString();

		logger.info( "result: {}" , get_hello_response );

		assertThat( get_hello_response )
			.as( "Simple get on hello method" )
			.contains( "message", "hi");

	}

}
