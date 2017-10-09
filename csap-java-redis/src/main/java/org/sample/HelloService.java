package org.sample;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloService {

	final Logger logger = LoggerFactory.getLogger( HelloService.class );
	
	@RequestMapping("/hello")
	public String hello() {
		
		logger.info( "simple log" ) ;
		return "Hello from " + HOST_NAME + " at "
				+ LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );
	}
	

	
	private static String SAMPLE_SESSION_VAR = "sampleSessionVar" ;
	
	@RequestMapping("/addSessionVar")
	public String addSessionVar( HttpSession session)  {
		
		if ( session.getAttribute( SAMPLE_SESSION_VAR ) == null ) session.setAttribute( SAMPLE_SESSION_VAR, new AtomicInteger(0) );
		
		AtomicInteger val =  (AtomicInteger ) session.getAttribute( SAMPLE_SESSION_VAR ) ;
		int curValue = val.incrementAndGet() ;
		
		logger.info( "Updated session variable {} : {}", SAMPLE_SESSION_VAR, curValue ) ;
		
		// in order for spring session to replicate to redis, you must explicit set the attribute
		// this is a performance optimization to avoid replicating everything every time
		 session.setAttribute( SAMPLE_SESSION_VAR, val ) ;
		
		return HOST_NAME + ": Updated session variable " + SAMPLE_SESSION_VAR + " to: " + curValue ;
	}
	
	
	@RequestMapping("/testAclFailure")
	public String testAclFailure()  {
		
		logger.info( "simple log" ) ;
		return "ACL page will be displayed if security is enabled in application.acl";
	}
	
	

	

	
	static String HOST_NAME="notFound" ;
	static {
		try {
			HOST_NAME = InetAddress.getLocalHost().getHostName() ;
		} catch (UnknownHostException e) {
			HOST_NAME="HOST_LOOKUP_ERROR";
		}
	}
	
}