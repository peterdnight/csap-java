package org.sample;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class HelloService {

	final Logger logger = LoggerFactory.getLogger( getClass() );

	private ObjectMapper jacksonMapper = new ObjectMapper();

	@RequestMapping(value = { "/hello", "/helloNoSecurity" })
	public String hello () {

		logger.info( "simple log" );
		return "Hello from " + HOST_NAME + " at "
				+ LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );
	}



	 
	@RequestMapping({ "helloWithOptionalName", "helloWithOptionalName/{name}" })
	public ObjectNode helloWithOptionalName ( @PathVariable Optional<String> name ) {

		ObjectNode resultJson = jacksonMapper.createObjectNode();
		logger.info( "simple log" );

		if ( name.isPresent() )
			resultJson.put( "name", name.get() );
		else
			resultJson.put( "name", "not-provided" );

		resultJson.put( "Response", "Hello from " + HOST_NAME + " at "
				+ LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );

		return resultJson;
	}

	// CANNOT be invoked from same class unless AspectJ configured. Invoked from
	// Landing Page
	@Async(Boot2DemoApplication.ASYNC_EXECUTOR)
	public void printMessage ( String message, int delaySeconds )
			throws Exception {

		Thread.sleep( 5000 );
		logger.info( "Time now: {}, Message Received: {}",
			LocalDateTime.now().format( DateTimeFormatter.ofPattern( "hh:mm:ss" ) ),
			message );
	}

	@RequestMapping({ "sleep/{seconds}" })
	public ObjectNode sleep ( @PathVariable int seconds )
			throws InterruptedException {

		ObjectNode resultJson = jacksonMapper.createObjectNode();
		logger.info( "sleeping for {} seconds", seconds );

		resultJson.put( "seconds", seconds );

		resultJson.put( "host", HOST_NAME );

		resultJson.put( "start", LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );

		Thread.sleep( seconds * 1000 );

		resultJson.put( "done", LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );
		return resultJson;
	}

	@RequestMapping("/helloWithRestAcl")
	public String helloWithRestAcl () {

		logger.info( "simple log" );
		return "helloWithRestAcl - Hello from " + HOST_NAME + " at "
				+ LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );
	}

	@RequestMapping("/testException")
	public String testException () {

		logger.info( "simple log" );
		throw new RuntimeException( "Spring Rest Exception" );
	}

	private static String SAMPLE_SESSION_VAR = "sampleSessionVar";

	@RequestMapping("/addSessionVar")
	public String addSessionVar ( HttpSession session ) {

		if ( session.getAttribute( SAMPLE_SESSION_VAR ) == null )
			session.setAttribute( SAMPLE_SESSION_VAR, new AtomicInteger( 0 ) );

		AtomicInteger val = (AtomicInteger) session.getAttribute( SAMPLE_SESSION_VAR );
		int curValue = val.incrementAndGet();

		logger.info( "Updated session variable {} : {}", SAMPLE_SESSION_VAR, curValue );

		// in order for spring session to replicate to redis, you must explicit
		// set the attribute
		// this is a performance optimization to avoid replicating everything
		// every time
		session.setAttribute( SAMPLE_SESSION_VAR, val );

		return HOST_NAME + ": Updated session variable " + SAMPLE_SESSION_VAR + " to: " + curValue;
	}

	@RequestMapping("/testAclFailure")
	public String testAclFailure () {

		logger.info( "simple log" );
		return "ACL page will be displayed if security is enabled in application.yml";
	}

	static String HOST_NAME = "notFound";
	static {
		try {
			HOST_NAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			HOST_NAME = "HOST_LOOKUP_ERROR";
		}
	}

}