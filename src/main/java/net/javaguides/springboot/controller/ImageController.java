package net.javaguides.springboot.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.javaguides.springboot.db.ImageRepository;
import net.javaguides.springboot.model.Image;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "images")
public class ImageController {
    private byte[] bytes;

	@Autowired
	private ImageRepository imageRepository;
	
	@GetMapping("/get")
	public List<Image> getBooks() {
		return imageRepository.findAll();
	}
	
	@PostMapping("/upload")
	public BodyBuilder uploadImage(@RequestParam("imageFile") MultipartFile file) throws IOException {

		System.out.println("Original Image Byte Size - " + file.getBytes().length);
		Image img = new Image(file.getOriginalFilename(), null,
				null, null, compressBytes(file.getBytes()));
		imageRepository.save(img);
		return (BodyBuilder) ResponseEntity.status(HttpStatus.OK);
	}
	
	

	@PostMapping("/add")
	public void createImage(@RequestBody Image image) throws IOException {
		image.setPicByte(this.bytes);
		imageRepository.save(image);
		this.bytes = null;
	}
	
	
	@PutMapping("/update")
	public void updateImage(@RequestBody Image image) {
		imageRepository.save(image);
	}
	
	// compress the image bytes before storing it in the database
		public static byte[] compressBytes(byte[] data) {
			Deflater deflater = new Deflater();
			deflater.setInput(data);
			deflater.finish();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			try {
				outputStream.close();
			} catch (IOException e) {
			}
			System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

			return outputStream.toByteArray();
		}
		
		// uncompress the image bytes before returning it to the angular application
		public static byte[] decompressBytes(byte[] data) {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			try {
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer);
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
			} catch (IOException ioe) {
			} catch (DataFormatException e) {
			}
			return outputStream.toByteArray();
		}

	
	
}
