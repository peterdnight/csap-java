package org.sample;

import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.csap.CsapMonitor;
import org.csap.integations.CsapInformation;
import org.csap.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@CsapMonitor
@RequestMapping("/")
public class LandingPage {

	protected final Log logger = LogFactory.getLog( getClass() );

	
	@Autowired
	CsapInformation csapInformation ;

	@RequestMapping(method = RequestMethod.GET)
	public String get(Model springViewModel ) {

		springViewModel.addAttribute( "host", csapInformation.getHostName() );
		springViewModel.addAttribute( "name", csapInformation.getName() );
		springViewModel.addAttribute( "version", csapInformation.getVersion() );

		springViewModel.addAttribute( "dateTime",
				LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) ) );

		return "LandingPage";
	}

	@RequestMapping("/currentTime")
	public void currentTime(PrintWriter writer) {
		
		logger.info( "SpringMvc writer" );
		
		writer.println( "currentTime: " + LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) )) ;
		
		return ;
	}

	@RequestMapping("/currentUser")
	public void currentUser(PrintWriter writer, Principal principle) {
		
		logger.info( "SpringMvc writer" );
		
		writer.println( "logged in user: " + principle.getName() ) ;
		return ;
	}
	
	@RequestMapping("/currentUserDetails")
	public void currentUserDetails(PrintWriter writer ) {
		
		logger.info( "SpringMvc writer" );
		CustomUserDetails userDetails = (CustomUserDetails ) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); ;
		writer.println( "logged in user email: " +  userDetails.getMail() );
		writer.println( "\n\n user information: \n" +  WordUtils.wrap( userDetails.toString(), 80 ) );
		
		return ;
	}
	
	@RequestMapping("/testNullPointer")
	public String testNullPointer() {

		if (System.currentTimeMillis() > 1)
			throw new NullPointerException("For testing only");

		return "hello";
	}
	
	@RequestMapping("/missingTemplate")
	public String missingTempate(Model springViewModel) {
		
		logger.info( "Sample thymeleaf controller" );
		
		springViewModel.addAttribute( "dateTime", 
				LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) )) ;
		
		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		
		return "/missingTemplate" ;
	} 
	
	@RequestMapping("/malformedTemplate")
	public String malformedTemplate(Model springViewModel) {
		
		logger.info( "Sample thymeleaf controller" );
		
		springViewModel.addAttribute( "dateTime", 
				LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) )) ;
		
		// templates are in: resources/templates/*.html
		// leading "/" is critical when running in a jar
		
		return "/MalformedExample" ;
	} 

}
