package org.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CoreJava {

	final Logger logger = LoggerFactory.getLogger( getClass() );

	@Test
	public void simple_stream_immutable_strings () {
		List<String> strings = new ArrayList<>();
		strings.add( "Hello, World!" );
		strings.add( "Welcome to simple tests" );
		strings.add( "This pad is running Java 8." );

		List<String> upperTraditional = new ArrayList<>();
		for ( String string : strings ) {
			upperTraditional.add( string.toUpperCase() );
		}

		logger.info( "\n\t Strings: {}, \n\t as upperTraditional: {}", strings, upperTraditional );

		List<String> upperStream = strings.stream()
			.map( String::toUpperCase )
			.collect( Collectors.toList() );

		logger.info( "\n\t Strings: {}, \n\t as upperStream: {}", strings, upperStream );

		List<String> upperFilterStream = strings.stream()
			.filter( this::myFilter )
			.map( String::toUpperCase )
			.collect( Collectors.toList() );

		logger.info( "\n\t Strings: {}, \n\t as upperFilterStream: {}", strings, upperFilterStream );

		String findFirst = strings.stream()
			.filter( this::myFilter )
			.map( String::toUpperCase )
			.findFirst()
			.orElse( "No Match" );

		logger.info( "\n\t Strings: {}, \n\t as findFirst: {}", strings, findFirst );
	}

	private boolean myFilter ( String s ) {

		return !s.contains( "ello" );
	}

	@Test
	public void stream_map () {

		Map<String, Map<String, String>> applicationToServiceAttributes = serviceMap();

		logger.info( "service Map: {}", applicationToServiceAttributes );

		List<String> jobs = applicationToServiceAttributes
			.entrySet()
			.stream()
			.filter( serviceEntry -> serviceEntry.getKey().startsWith( "service" ) )
			.flatMap( serviceEntry -> serviceEntry.getValue().entrySet().stream() )
			.filter( attributeEntry -> attributeEntry.getKey().equals( "jobs" ) )
			.map( Map.Entry::getValue )
			.collect( Collectors.toList() );

		logger.info( "Job list: {}", jobs );

	}

	ObjectMapper jsonBuilder = new ObjectMapper();

	@Test
	public void count_words_using_java_core ()
			throws Exception {

		
		// for reference demos only; rest clients should always rely on apache httpclient, spring restTemplate, or similar
		URL url = new URL( "https://api.datamuse.com/words?ml=duck&sp=b*&max=10" );
		HttpURLConnection httpConnection_no_pooling = (HttpURLConnection) url.openConnection();
		httpConnection_no_pooling.setRequestMethod( "GET" );

		int responseCode = httpConnection_no_pooling.getResponseCode();

		logger.info( "responseCode: {}", responseCode );

		StringBuffer content = new StringBuffer();
		try (
				BufferedReader in = new BufferedReader(
					new InputStreamReader( httpConnection_no_pooling.getInputStream() ) );) {

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				content.append( inputLine );
			}
		}

		httpConnection_no_pooling.disconnect();

		logger.info( "Content returned is JSON: {}", content );

		// convert to json
		ArrayNode wordResponse = (ArrayNode) jsonBuilder.readTree( content.toString() );

		// wordResponse.forEach( System.out::println );

		Stream<JsonNode> wordsStream = IntStream.range( 0, wordResponse.size() ).mapToObj( wordResponse::get );

		Map<String, String> wordToScoreMap = wordsStream
			.collect(
				Collectors.toMap(
					( wordAttributes ) -> wordAttributes.get( "word" ).asText(),
					( wordAttributes ) -> wordAttributes.get( "score" ).asText() ) );

		logger.info( "wordToScoreMap: \n {}", wordToScoreMap );

		Map<String, String> top5WordScores = wordToScoreMap
			.entrySet()
			.stream()
			.sorted( Collections.reverseOrder( Map.Entry.comparingByValue() ) )
			.limit( 5 )
			.collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue,
				( e1, e2 ) -> e2, LinkedHashMap::new ) );
		

		logger.info( "top5WordScores: \n {}", top5WordScores );
		
	}

	protected static Map<String, Map<String, String>> serviceMap () {
		return Collections.unmodifiableMap( Stream.of(
			entry( "service_one", serviceAttributes() ),
			entry( "service_two", serviceAttributes() ),
			entry( "dummy", serviceAttributes() ) )
			.collect( entriesToMap() ) );
	}

	static Random initializeRandom = new Random();

	protected static Map<String, String> serviceAttributes () {
		return Collections.unmodifiableMap( Stream.of(
			entry( "parameters", "-Xms256M -Xmx256M" ),
			entry( "environmentVariables", "var1=val1,var2=val2" ),
			entry( "jobs", "job_" + (50 + initializeRandom.nextInt( 50 )) ) )
			.collect( entriesToMap() ) );
	}

	public static <K, V> Map.Entry<K, V> entry ( K key, V value ) {
		return new AbstractMap.SimpleEntry<>( key, value );
	}

	public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap () {
		return Collectors.toMap( ( e ) -> e.getKey(), ( e ) -> e.getValue() );
	}

	public static <K, U> Collector<Map.Entry<K, U>, ?, ConcurrentMap<K, U>> entriesToConcurrentMap () {
		return Collectors.toConcurrentMap( ( e ) -> e.getKey(), ( e ) -> e.getValue() );
	}

}
