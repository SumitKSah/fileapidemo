package com.fileapidemo.application.utils;

public class AppConstants {

	public static final String DOWNLOAD_PATH = "/downloadFile/";
	public static final String DOWNLOAD_URI = "/downloadFile/{fileName:.+}";
	public static final String FILE_DOWNLOAD_HTTP_HEADER = "attachment; filename=";
	public static final String FILE_PROPERTIES_PREFIX = "file";
	public static final String FILE_STORAGE_EXCEPTION_PATH_NOT_FOUND = "Could not create the directory where the uploaded files will be stored";
	public static final String FILE_NOT_FOUND = "File not found ";
	public static final String FILE_STORAGE_EXCEPTION = "Could not store file %s !! Please try again!";
	
	public static final String MESSAGE = "Message : ";
	public static final String DELETE_FILE_SUCCESSFULY = " deleted successfuly";
	public static final String COUNT_NOT_INITIALIZE_STORAGE_LOCATIONS = "Could not initialize storage location";
	public static final String FAILED_TO_STORE_EMPTY_FILE = "Failed to store empty file " ;
	public static final String CAN_NOT_STORE_FILE_WITH_RELATIVE_PATH_OUTSIDE_CURRENT_DIRECTORY = "Cannot store file with relative path outside current directory ";
	public static final String FAILED_TO_STORE_FILE  = "Failed to store file ";
	public static final String FAILED_TO_READ_STORED_FILE  = "Failed to read stored files";
}
