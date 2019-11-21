package de.unibayreuth.bayceer.oc;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import org.elasticsearch.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ExceptionHandler()
	public final ResponseEntity<String> handleReadmeParserException(Exception ex, WebRequest request) {
		HttpHeaders headers = new HttpHeaders();
		log.error(ex.getMessage());
        return new ResponseEntity<String>("Failed to parse readme.", headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	@ExceptionHandler({ NoSuchFileException.class })
    public final ResponseEntity<String> handleNoSuchFileException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        NoSuchFileException nsf = (NoSuchFileException) ex;        	
        return new ResponseEntity<String>("No such file:" + nsf.getFile(), headers, HttpStatus.NOT_FOUND);        
    }
	
		
	@ExceptionHandler({ NoSuchDocException.class })
    public final ResponseEntity<String> handleNoSuchDocException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        NoSuchDocException e = (NoSuchDocException) ex;        	
        return new ResponseEntity<String>("No such doc:" + e.getKey(), headers, HttpStatus.NOT_FOUND);        
    }
	
	@ExceptionHandler({ IOException.class })
    public final ResponseEntity<String> handleIOException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        IOException e = (IOException) ex;
        log.error(e.getMessage());
        return new ResponseEntity<String>("Please check the log file.", headers, HttpStatus.INTERNAL_SERVER_ERROR);        
    }
	
	@ExceptionHandler({ ElasticsearchException.class })
    public final ResponseEntity<String> handleESException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        ElasticsearchException e = (ElasticsearchException) ex;
        log.error(e.getMessage());
        return new ResponseEntity<String>("Please check the log file.", headers, HttpStatus.INTERNAL_SERVER_ERROR);        
    }
	
	
	
    
}