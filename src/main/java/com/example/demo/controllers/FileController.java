package com.example.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.example.demo.entities.File;
import com.example.demo.services.FileService;

@RestController
@RequestMapping("/files")
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	@PostMapping
	public File uploadFile(@RequestPart MultipartFile file ) throws AmazonServiceException, SdkClientException, IOException {
		return fileService.uploadFile(file);
	}
	
	@GetMapping
	public List<File> getFiles() {
		return fileService.findAllFilesFromUser();
	}
	
	@DeleteMapping("/{fileId}")
	public File deleteFile(@PathVariable Integer fileId) {
		return fileService.deleteFile(fileId);
	}
}
