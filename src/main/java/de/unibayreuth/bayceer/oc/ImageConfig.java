package de.unibayreuth.bayceer.oc;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfig {
		
	@Value("${IMAGE_PATH:}")
	private String imagePath;
			
	@Bean	
	public String imagePath() throws IOException  {		
		Path p;
		if (imagePath.isEmpty()) {
			p = FileSystems.getDefault().getPath("oc_images");
		} else {
			p = Paths.get(imagePath);
		}				
		if (Files.notExists(p)) {
			Files.createDirectories(p);			
		}		
		return p.toString();		 
	}
				
}
