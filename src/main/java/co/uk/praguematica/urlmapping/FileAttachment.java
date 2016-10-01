package co.uk.praguematica.urlmapping;

public class FileAttachment {
	private String contentType;
	private String fileName;
	private byte[] fileContents;
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFileContents() {
		return fileContents;
	}
	public void setFileContents(byte[] fileContents) {
		this.fileContents = fileContents;
	}
	
	public FileAttachment(String contentType, String fileName, byte[] fileContents) {
		super();
		this.contentType = contentType;
		this.fileName = fileName;
		this.fileContents = fileContents;
	}
	
}
