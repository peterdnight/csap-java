package org.sample;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.csap.helpers.CsapRestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @see http://docs.spring.io/spring-data/redis/docs/1.6.0.RELEASE/reference/
 *      html/#get-started:first-steps:samples
 * 
 * @author pnightin
 *
 */
@RestController
@RequestMapping ( "redis" )
public class RedisService {

	static String HOST_NAME = "notFound";

	static {
		try {
			HOST_NAME = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			HOST_NAME = "HOST_LOOKUP_ERROR";
		}
	}

	@Autowired
	private StringRedisTemplate redisTemplate;

	final Logger logger = LoggerFactory.getLogger( RedisService.class );

	Random rand = new Random();

	@RequestMapping ( { "demo/add", "demo/add/{key}", "demo/add/{key}/{value}" } )
	public ObjectNode add (
							@PathVariable Optional<String> key,
							@PathVariable Optional<String> value ) {

		logger.info( "simple push" );
		ObjectNode results = jacksonMapper.createObjectNode();

		String sampleKey = HOST_NAME + "_" + rand.nextInt( 10 );
		String sampleValue = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "HH:mm:ss,   MMMM d  uuuu " ) );

		if ( key.isPresent() ) {
			sampleKey = key.get();
		}
		if ( value.isPresent() ) {
			sampleValue = value.get();
		}

		if ( !key.isPresent() && !value.isPresent() )
			results.put( "usage", "add/{some key}/{some value}; if not supplied defaults will be generated" );

		results.put( "provided", "key: " + sampleKey + " value: " + sampleValue );
		//
		try {
			redisTemplate
				.opsForValue()
				.set( sampleKey, sampleValue );
		} catch (Exception e) {
			results.put( "Error", "Failed to push: " + e.getMessage() );
			logger.error( "Failed to addd: {}", results.toString(), e );
		}

		return results;
	}

	@RequestMapping ( { "demo/get", "demo/get/{key}" } )
	public ObjectNode get (
							@PathVariable Optional<String> key, HttpServletRequest request ) {

		logger.info( "simple show" );

		ObjectNode results = jacksonMapper.createObjectNode();

		String sampleKey = HOST_NAME + "_1";

		if ( key.isPresent() )
			sampleKey = key.get();

		if ( !key.isPresent() )
			results.put( "usage", "get/{some key}; if not supplied defaults will be generated" );

		results.put( "requestedKey", sampleKey );
		try {
			results.put( "value",
				redisTemplate
					.opsForValue().get( sampleKey ) );

			addRelatedCommands( request, results, sampleKey );
		} catch (InvalidDataAccessApiUsageException e) {
			// Maybe this is a hash type?
			logger.debug( "Failed getting {}", sampleKey, e );
			results.set( "hashEntries", getHashEntries( key.get() ) );
		}

		return results;
	}

	private void addRelatedCommands ( HttpServletRequest request, ObjectNode results, String sampleKey ) {
		// hateoas pattern

		URL base = getBaseUrl( request );

		ArrayNode commands = results.putArray( "relatedCommands" );
		commands.add( base + "delete/" + sampleKey );
		commands.add( base + "search/" );
		commands.add( base + "add/" );
		commands.add( base + "backToLandingPage" );
	}

	private URL getBaseUrl ( HttpServletRequest request ) {
		URL base = null;
		try {
			base = new URL( request.getScheme(),
				request.getServerName(),
				request.getServerPort(),
				request.getContextPath() + "/redis/demo/" );

		} catch (MalformedURLException e) {
			logger.error( "Failed to build paths", e );
		}
		return base;
	}

	@RequestMapping ( { "demo/delete", "demo/delete/{key}" } )
	public ObjectNode delete (
								@PathVariable Optional<String> key, HttpServletRequest request ) {

		logger.info( "delete" );

		ObjectNode results = jacksonMapper.createObjectNode();

		String sampleKey = HOST_NAME + "_1";

		if ( key.isPresent() )
			sampleKey = key.get();

		if ( !key.isPresent() )
			results.put( "usage", "delete/{some key}; if not supplied defaults will be generated" );

		results.put( "requestedKey", sampleKey );
		try {

			redisTemplate.delete( sampleKey );

			results.put( "deleted", true );
			addRelatedCommands( request, results, sampleKey );

		} catch (Exception e) {
			// Maybe this is a hash type?
			logger.debug( "Failed deleting {}", sampleKey, e );
			results.put( "deleted", e.getMessage() );
		}

		return results;
	}

	@RequestMapping ( "demo/getHash/{key}" )
	public ObjectNode getHash (
								@PathVariable Optional<String> key ) {

		logger.info( "simple show" );

		ObjectNode results = jacksonMapper.createObjectNode();

		if ( !key.isPresent() )
			results.put( "error", "key is a required param: demo/getHash/{key}" );
		else {
			redisTemplate.setValueSerializer( new GenericToStringSerializer<Long>( Long.class ) );
			results.put( "requestedKey", key.get() );

			results.set( "hashEntries", getHashEntries( key.get() ) );

		}

		return results;
	}

	private ObjectNode getHashEntries ( String redisKey ) {
		ObjectNode sessionHash = jacksonMapper.createObjectNode();

		Map<Object, Object> redisHashEntries = redisTemplate
			.opsForHash().entries( redisKey );

		redisHashEntries.keySet().forEach(
			item -> sessionHash.put( item.toString(), redisHashEntries.get( item ).toString() ) );
		return sessionHash;
	}

	@RequestMapping ( { "demo/search", "demo/search/{pattern}" } )
	public ObjectNode search (
								@PathVariable Optional<String> pattern,
								HttpServletRequest request )
			throws UnknownHostException {

		ObjectNode results = jacksonMapper.createObjectNode();
		results.put( "note", "you can use search/*, search/*abc*, etc" );

		String scanPattern = InetAddress.getLocalHost().getHostName() + "*";

		if ( pattern.isPresent() )
			scanPattern = pattern.get();

		logger.info( "searching for: {}", scanPattern );

		results.put( "requestedPattern", scanPattern );

		ScanOptions scanOptions = ScanOptions.scanOptions().match( scanPattern ).build();
		// ScanOptions scanOptions = ScanOptions.NONE;
		// Cursor<Entry<Object, Object>> cursor =
		// redisTemplate.opsForSet().scan("KEY", scanOptions);

		try {
			redisTemplate.execute( (RedisCallback<ObjectNode>) conn -> {

				int numItems = 0;
				results.put( "matchCount", numItems );
				ArrayNode matches = results.putArray( "matches" );

//				try (Cursor<byte[]> cursor = conn.scan( scanOptions )) {
				Cursor<byte[]> cursor = conn.scan( scanOptions ) ;
					while (cursor.hasNext()) {
						numItems++;
						String keyFound = "CouldNotConvert";
						try {
							keyFound = new String( cursor.next(), "UTF-8" );
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// hateoas pattern
						matches.add( getBaseUrl( request ) + "get/" + keyFound );
					}
//				} catch (Exception e) {
//					logger.warn( "Failed search", e );
//				}
				// cursor.
				results.put( "matchCount", numItems );
				return results;
			} );
		} catch (Exception e) {
			logger.error( "Failed redis search {}", CsapRestTemplateFactory.getFilteredStackTrace( e, "sample" ) );
		}

		addRelatedCommands( request, results, "" );
		return results;
	}

	private ObjectMapper jacksonMapper = new ObjectMapper();

	@RequestMapping ( "info" )
	public ObjectNode info ()
			throws UnknownHostException {

		logger.debug( "Getting metrics" );

		// lambda as a replacement anonymous. This is obfuscated because lamda
		// has multiple
		// matches, and must be explicitly targeted
		ObjectNode result = redisTemplate.execute( (RedisCallback<ObjectNode>) conn -> {
			Properties redisInfo = conn.info();
			redisInfo.put( "numberOfKeys", conn.dbSize() + "" );
			return jacksonMapper.convertValue( redisInfo, ObjectNode.class );
		} );

		return result;
	}

	@RequestMapping ( "info/keyCount" )
	public ObjectNode numberOfKeys ()
			throws UnknownHostException {

		logger.info( "simple numberOfKeys" );

		return redisTemplate.execute( (RedisCallback<ObjectNode>) conn -> {
			ObjectNode result = jacksonMapper.createObjectNode();
			result.put( "numberOfKeys", conn.dbSize() + "" );
			return result;
		} );
	}

	@RequestMapping ( "clear" )
	public String clear ()
			throws UnknownHostException {

		logger.info( "simple show" );

		String result = redisTemplate
			.opsForValue()
			.get( "peter" );
		;

		return result;
	}

	@RequestMapping ( "demo/backToLandingPage" )
	public void currentTime ( HttpServletRequest request, HttpServletResponse response )
			throws Exception {
		response.sendRedirect( getBaseUrl( request ) + "../.." );
	}
	// public RedisConnection getRedis() {
	// return redisTemplate.getConnectionFactory().getConnection() ;
	// }
}
