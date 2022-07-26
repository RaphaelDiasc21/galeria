package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.entities.File;
import com.example.demo.entities.User;
import com.example.demo.repositories.FileRepository;
import com.example.demo.repositories.UserRepository;

@Component
public class FileService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileRepository fileRepository;
	
	private final String AMAZONURL = "https://s3-sa-east-1.amazonaws.com/galeria4/";
	
	private void uploadToS3(String fileName,InputStream inputStream, ObjectMetadata metadata) {
		
		AWSCredentials credentials = new BasicAWSCredentials("AKIARPSG46V5IMZHAMOK","2G26QETmDCKrmEOJt2FckS1OUhBIJ+gbqQcD5ncA");
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
							.withCredentials(new AWSStaticCredentialsProvider(credentials))
							.withRegion(Regions.SA_EAST_1)
							.build();
		
		
		Object object = SecurityContextHolder.getContext().getAuthentication().getDetails();
		s3client.putObject("galeria4", fileName,inputStream,metadata);
		
		
	}
	
	public File uploadFile(MultipartFile file) throws IOException {
		
		Object userAuthenticatedId = SecurityContextHolder.getContext().getAuthentication().getDetails();
		String filePath = AMAZONURL + userAuthenticatedId.toString() + file.getOriginalFilename();
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		
		uploadToS3(userAuthenticatedId.toString() + file.getOriginalFilename(),file.getInputStream(),metadata);
		
		Optional<User> userOpt = userRepository.findById(Integer.parseInt(userAuthenticatedId.toString()));
				
		File fileCreated = new File();
		fileCreated.setPath(filePath);
		fileCreated.setUser(userOpt.get());
		
		return fileRepository.save(fileCreated);
		
	}
	
	public List<File> findAllFilesFromUser() {
		Object userAuthenticatedId = SecurityContextHolder.getContext().getAuthentication().getDetails();
		Optional<User> userOpt = userRepository.findById(Integer.parseInt(userAuthenticatedId.toString()));
		return fileRepository.findByUser(userOpt.get());
	}
	
	public File deleteFile(Integer fileId) {
		
		Optional<File> fileOpt = fileRepository.findById(fileId);
		
		File fileDb = fileOpt.get();
		
		
		AWSCredentials credentials = new BasicAWSCredentials("AKIARPSG46V5IMZHAMOK","2G26QETmDCKrmEOJt2FckS1OUhBIJ+gbqQcD5ncA");
		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
							.withCredentials(new AWSStaticCredentialsProvider(credentials))
							.withRegion(Regions.SA_EAST_1)
							.build();
		
		
		Object object = SecurityContextHolder.getContext().getAuthentication().getDetails();
		s3client.deleteObject("galeria4",fileDb.getPath().substring(AMAZONURL.length()));
		
		fileRepository.delete(fileDb);
		
		return fileDb;
	
	}
	
	
	
}
