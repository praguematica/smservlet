package com.dvwsolutions.urlmapping;

public class MappingProcessorException extends Exception {
	private static final long serialVersionUID = -2858449259962603098L;

	private MappingProcessorExceptionType type = MappingProcessorExceptionType.RUNTIME;

	public MappingProcessorExceptionType getType() {
		return type;
	}

	public void setType(MappingProcessorExceptionType type) {
		this.type = type;
	}

	public MappingProcessorException() {
		super();
	}
	
	public MappingProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public MappingProcessorException(String message) {
		super(message);
	}

	public MappingProcessorException(Throwable cause) {
		super(cause);
	}

	public MappingProcessorException(MappingProcessorExceptionType type) {
		super();
		this.type = type;
	}

	public MappingProcessorException(MappingProcessorExceptionType type, String message, Throwable cause) {
		super(message, cause);
		this.type = type;
	}

	public MappingProcessorException(MappingProcessorExceptionType type, String message) {
		super(message);
		this.type = type;
	}

	public MappingProcessorException(MappingProcessorExceptionType type, Throwable cause) {
		super(cause);
		this.type = type;
	}
	
	
}
