package com.fileapidemo.application.upload.service.imp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fileapidemo.application.config.StorageProperties;
import com.fileapidemo.application.controller.FileController;
import com.fileapidemo.application.exception.StorageException;
import com.fileapidemo.application.upload.service.StorageService;
import com.fileapidemo.application.utils.AppConstants;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;
	Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			logger.error(AppConstants.COUNT_NOT_INITIALIZE_STORAGE_LOCATIONS, e);
			throw new StorageException(AppConstants.COUNT_NOT_INITIALIZE_STORAGE_LOCATIONS, e);
		}
	}

	@Override
	public String store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				logger.error(AppConstants.FAILED_TO_STORE_EMPTY_FILE + filename);
				throw new StorageException(AppConstants.FAILED_TO_STORE_EMPTY_FILE + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				logger.error(AppConstants.CAN_NOT_STORE_FILE_WITH_RELATIVE_PATH_OUTSIDE_CURRENT_DIRECTORY + filename);
				throw new StorageException(
						AppConstants.CAN_NOT_STORE_FILE_WITH_RELATIVE_PATH_OUTSIDE_CURRENT_DIRECTORY + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			logger.error(AppConstants.FAILED_TO_STORE_FILE + filename, e);
			throw new StorageException(AppConstants.FAILED_TO_STORE_FILE + filename, e);
		}

		return filename;
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			logger.error(AppConstants.FAILED_TO_READ_STORED_FILE, e);
			throw new StorageException(AppConstants.FAILED_TO_READ_STORED_FILE, e);
		}

	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				// throw new FileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			// throw new FileNotFoundException("Could not read file: " + filename, e);
		}
		return null;
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void deleteFile(String fileName) {

		// FileSystemUtils.deleteRecursively(rootLocation.toFile());
		File file = new File(this.rootLocation + "/" + fileName);
		if (file.delete()) {
			logger.info(AppConstants.DELETE_FILE_SUCCESSFULY);
		} else {
			logger.error(AppConstants.FILE_NOT_FOUND, fileName);
			throw new StorageException(AppConstants.FILE_NOT_FOUND + fileName);
		}

	}
}
