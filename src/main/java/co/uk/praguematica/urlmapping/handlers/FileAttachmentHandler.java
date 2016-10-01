package co.uk.praguematica.urlmapping.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import co.uk.praguematica.urlmapping.FileAttachment;
import co.uk.praguematica.urlmapping.MappingProcessorException;
import co.uk.praguematica.urlmapping.MappingProcessorExceptionType;
import co.uk.praguematica.urlmapping.annotations.RequestMapping;

public class FileAttachmentHandler extends DefaultHandler {

	@Override
	public void result(HttpServletResponse response, Object result, RequestMapping rm) throws MappingProcessorException {
		try {
			String contentType = "";
			String fileName = "";
			byte[] contents = null;
			
			if (result instanceof FileAttachment) {
				FileAttachment fa = (FileAttachment) result;
				contentType = fa.getContentType();
				fileName = fa.getFileName();
				contents = fa.getFileContents();
				
			} else {
				contentType = rm.contentType();
				fileName = rm.fileName();
				if (result instanceof byte[]) {
					contents = (byte[]) result;
				} else if (result instanceof String) {
					contents = ((String)result).getBytes();
				} else if (result == null){
					result = null;
				} else {
					throw new MappingProcessorException(MappingProcessorExceptionType.ATTACHMENT_HANDLING_ERROR, "Unsupported type of contents of attachment: " + result.getClass().getCanonicalName());
				}
 
			}
			
			response.setCharacterEncoding(rm.encoding());
			response.setContentType(contentType);
			response.setContentLength((contents!=null)?contents.length:0);
			response.setHeader("Content-Transfer-Encoding", "binary");
			if (fileName != null && !fileName.equals("")) {
				response.setHeader("Content-Disposition","attachment; filename=\"" + fileName );
			}
			response.getOutputStream().write(contents);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
