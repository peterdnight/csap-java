package org.sample;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		

		String  findFirst = strings.stream()
			.filter( this::myFilter )
			.map( String::toUpperCase )
			.findFirst()
			.orElse( "No Match" );

		logger.info( "\n\t Strings: {}, \n\t as findFirst: {}", strings, findFirst );
	}
	
	private boolean myFilter(String s) {
		
		return ! s.contains( "ello" );
	}
	
	@Test
	public void stream_map() {
		
		Map<String,Map<String,String>> applicationToServices = new HashMap<>() ;
		
		
	}
	
    protected static Map<Integer, String> buildAMap() {
        return Collections.unmodifiableMap(Stream.of(
                entry(0, "zero"),
                entry(1, "one"),
                entry(2, "two"),
                entry(3, "three"),
                entry(4, "four"),
                entry(5, "five"),
                entry(6, "six"),
                entry(7, "seven"),
                entry(8, "eight"),
                entry(9, "nine"),
                entry(10, "ten"),
                entry(11, "eleven"),
                entry(12, "twelve")).
                collect(entriesToMap()));
    }
    
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap() {
        return Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue());
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, ConcurrentMap<K, U>> entriesToConcurrentMap() {
        return Collectors.toConcurrentMap((e) -> e.getKey(), (e) -> e.getValue());
    }

}
