package com.dvwsolutions.urlmapping;

import java.io.PrintWriter;
import java.io.StringWriter;


public class MappingProcessorError {
	private String message;
	private transient Throwable throwable;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public MappingProcessorError() {
		super();
	}

	public MappingProcessorError(String message) {
		super();
		this.message = message;
	}

	public MappingProcessorError( String message, Throwable throwable) {
		super();
		this.message = message;
		this.throwable = throwable;
	}

	@Override
	public String toString() {
		String details = "";
		if (throwable != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			details = "\n\nDetails: " + sw.toString();
		}
		return "Error Message: " + message + details;
	}
	
	


}
