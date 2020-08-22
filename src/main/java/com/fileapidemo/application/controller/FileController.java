package com.fileapidemo.application.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fileapidemo.application.model.FileResponse;
import com.fileapidemo.application.upload.service.StorageService;
import com.fileapidemo.application.utils.AppConstants;

@Controller
public class FileController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/")
	public String listAllFiles(Model model) {

		model.addAttribute("files",
				storageService.loadAll().map(path -> ServletUriComponentsBuilder.fromCurrentContextPath()
						.path("/download/").path(path.getFileName().toString()).toUriString())
						.collect(Collectors.toList()));

		return "index";
	}

	@GetMapping("/download/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

		Resource resource = storageService.loadAsResource(filename);
		if (resource == null)
			return ResponseEntity.notFound().header(AppConstants.MESSAGE + filename + AppConstants.FILE_NOT_FOUND)
					.build();
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				AppConstants.FILE_DOWNLOAD_HTTP_HEADER + resource.getFilename() + "\"").body(resource);
	}

	@PostMapping("/upload-file")
	@ResponseBody
	public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {
		String name = storageService.store(file);

		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(name).toUriString();

		return new FileResponse(name, uri, file.getContentType(), file.getSize());
	}

	@DeleteMapping("/delete/{id}")
	@ResponseBody
	public ResponseEntity<Resource> deleteFile(@PathVariable String id) {

		try {
			storageService.deleteFile(id);
		} catch (Exception e) {
			return ResponseEntity.notFound().header(AppConstants.MESSAGE, e.getMessage()).build();
		}
		return ResponseEntity.ok().header(id + AppConstants.DELETE_FILE_SUCCESSFULY).build();
	}

	@PostMapping("/upload-multiple-files")
	@ResponseBody
	public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.stream(files).map(file -> uploadFile(file)).collect(Collectors.toList());
	}
}