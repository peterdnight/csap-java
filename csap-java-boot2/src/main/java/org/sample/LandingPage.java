package org.sample;

import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LandingPage {

	final Logger logger = LoggerFactory.getLogger( getClass() );

	
	
	@GetMapping ( value = "/" )
	public String get ( Model springViewModel ) {

		springViewModel.addAttribute( "helpPageExample", LandingPage.class.getCanonicalName() );

		return "LandingPage";
	}

	@GetMapping ( "/maxConnections" )
	public String maxConnections ( Model springViewModel ) {

		return "maxConnections";
	}


	@GetMapping ( "/currentTime" )
	public void currentTime (
								@RequestParam ( value = "mySampleParam" , required = false , defaultValue = "1.0" ) double sampleForTesting,
								@RequestParam (required = false) ArrayList<String> sampleOptionalList,
								String a,
								PrintWriter writer ) {

		String formatedTime = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );

		logger.info( "Time now is: {}, sampleOptionalList: {}, a: {}", formatedTime, sampleOptionalList, a );

		writer.println( "currentTime: " + formatedTime );

		return;
	}

	@RequestMapping ( "/currentUser" )
	public void currentUser ( PrintWriter writer, Principal principle ) {

		logger.info( "SpringMvc writer" );

		if ( principle != null ) {
			writer.println( "logged in user: " + principle.getName() );
		} else {
			writer.println( "logged in user: principle is null - verify security is configured" );
		}

		return;
	}

	@RequestMapping ( "/currentUserDetails" )
	public void currentUserDetails ( PrintWriter writer ) {

		logger.info( "SpringMvc writer" );
//		CustomUserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		;
//		writer.println( "logged in user email: " + userDetails.getMail() );
//		writer.println( "\n\n user information: \n" + WordUtils.wrap( userDetails.toString(), 80 ) );

		return;
	}

	@Inject
	HelloService helloService;

	@RequestMapping ( "/testAsync" )
	@ResponseBody
	public String testAsync (
								@RequestParam ( value = "delaySeconds" , required = false , defaultValue = "5" ) int delaySeconds )
			throws Exception {
		String message = "Hello from " + this.getClass().getSimpleName()
				+ " at " + LocalDateTime.now().format( DateTimeFormatter.ofPattern( "hh:mm:ss" ) );
		helloService.printMessage( message, delaySeconds );
		return "Look in logs for async to complete in: " + delaySeconds + " seconds";
	}

	@RequestMapping ( "/testNullPointer" )
	public String testNullPointer () {

		if ( System.currentTimeMillis() > 1 ) {
			throw new NullPointerException( "For testing only" );
		}

		return "hello";
	}

	@RequestMapping ( "/missingTemplate" )
	public String missingTempate ( Model springViewModel ) {

		logger.info( "Sample thymeleaf controller" );

		springViewModel.addAttribute( "dateTime",
			LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );

		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		return "/missingTemplate";
	}

	@RequestMapping ( "/malformedTemplate" )
	public String malformedTemplate ( Model springViewModel ) {

		logger.info( "Sample thymeleaf controller" );

		springViewModel.addAttribute( "dateTime",
			LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );

		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		return "/MalformedExample";
	}

}
