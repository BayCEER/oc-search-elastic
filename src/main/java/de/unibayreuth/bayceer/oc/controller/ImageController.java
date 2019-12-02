package de.unibayreuth.bayceer.oc.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ImageController {
	
	
	@Autowired
	String imagePath;
	
	private static final String image_extension = ".png";
	
	public enum ImageType {
		THUMBNAIL("th"), IMAGE("img");		
		public String code;
		private ImageType(String code) {
			this.code = code;
		}		
	}
					
	
	@RequestMapping(value = "/{collection}/thumbnail/{key}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getThumb(@PathVariable String collection, @PathVariable String key) throws NoSuchFileException, IOException {
			return Files.readAllBytes(getImagePath(collection,key,ImageType.THUMBNAIL));		
	}
	
	@RequestMapping(value = "/{collection}/image/{key}", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage(@PathVariable String collection, @PathVariable String key) throws NoSuchFileException, IOException {				
			return Files.readAllBytes(getImagePath(collection, key, ImageType.IMAGE));				 
	}
	
	
	@RequestMapping(value = "/{collection}/thumbnails", method = RequestMethod.DELETE)
	public void deleteImages(@PathVariable String collection) {
		deleteFiles(collection, ImageType.THUMBNAIL);
	}
	
	@RequestMapping(value = "/{collection}/images", method = RequestMethod.DELETE)
	public void deleteThumbs(@PathVariable String collection) {
		deleteFiles(collection,ImageType.IMAGE);
	}
	
	
	@RequestMapping(value = "/{collection}/thumbnail/{key}", method = RequestMethod.POST, consumes = MediaType.IMAGE_PNG_VALUE)
	public void createThumb(@PathVariable String collection, @PathVariable String key,@RequestBody byte[] image) throws IOException {
			Files.createDirectories(Paths.get(imagePath, collection));	
			Files.write(getImagePath(collection, key, ImageType.THUMBNAIL), image);		
	}
	
	@RequestMapping(value = "/{collection}/image/{key}", method = RequestMethod.POST, consumes = MediaType.IMAGE_PNG_VALUE)
	public void createImage(@PathVariable String collection, @PathVariable String key,@RequestBody byte[] image) throws IOException {
			Files.createDirectories(Paths.get(imagePath, collection));
			Files.write(getImagePath(collection,key, ImageType.IMAGE), image);		
	}
	
	@RequestMapping(value = "/{collection}/thumbnail/{key}", method = RequestMethod.DELETE)
	public void deleteThumb(@PathVariable String collection,@PathVariable String key) throws IOException {
			Files.deleteIfExists(getImagePath(collection,key,ImageType.THUMBNAIL));		
	}
	
	
	@RequestMapping(value = "/{collection}/image/{key}", method = RequestMethod.DELETE)
	public void deleteImage(@PathVariable String collection, @PathVariable String key) throws IOException {
			Files.deleteIfExists(getImagePath(collection,key, ImageType.IMAGE));		
	}
	
		
	public boolean exits(String collection, String key, ImageType imageType) {
	    return getImagePath(collection, key, imageType).toFile().exists();		
	}
	
	private void deleteFiles(String collection, ImageType imageType) {				
		for(File f: Paths.get(imagePath,collection).toFile().listFiles(fileFilter(imageType))) {
			f.delete();
		}		
	}
	
	private Path getImagePath(String collection, String key, ImageType imageType) {	
		return Paths.get(imagePath,collection, key  + "_" + imageType.code + image_extension);	
	}
	
	private FilenameFilter fileFilter(ImageType imageType) {
		return new FilenameFilter() {			
			String suffix = imageType.code + image_extension;
			@Override
			public boolean accept(File dir, String name) {	
				return name.endsWith(suffix);				
			}
		};
		
	}
	
	
}
