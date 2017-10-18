package org.sample;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
 
@RestController
@RequestMapping("/api")
public class SimpleRest {

	
	@Inject
	ObjectMapper jsonMapper ;
	
	@GetMapping ("hi")
	public ObjectNode hi() {
		
		ObjectNode result = jsonMapper.createObjectNode() ;
		
		result.put( "message", "hi" ) ;
		
		return result ;
	}
}
