package sscgi.data.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sscgi.data.SSCGIMessage;

public class HttpMessage {
	
	private List<Header> headers = new ArrayList<>();
	private String body = "";
	
	public HttpMessage(List<Header> headers, String body) {
		this.headers = headers;
		this.body = body;
	}
	
	public HttpMessage(SSCGIMessage message) throws ClassNotFoundException, IOException {
		if (message.getHeaders() != null) {
			headers = Header.parseHeaders(message.getHeaders());
		}
		if (message.getBody() != null) {
			body = new String(message.getBody());
		}
	}
	
	public HttpMessage(SSCGIMessage message, String bodyEncoding) throws ClassNotFoundException, IOException {
		if (message.getHeaders() != null) {
			headers = Header.parseHeaders(message.getHeaders());
		}
		if (message.getBody() != null) {
			body = new String(message.getBody(), bodyEncoding);
		}
	}
	
	public void addHeader(String key, String value) {
		headers.add(new Header(key, value));
	}
	
	public void removeHeader(String key) {
		headers = headers.stream().filter(h -> !h.getKey().equals(key)).collect(Collectors.toList());
	}
	
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}

	public List<Header> getHeaders() {
		return headers;
	}
	
	public SSCGIMessage toMessage() throws IOException {
		return new SSCGIMessage(Header.serializeHeaders(headers), body.getBytes());
	}

//	private static byte[] toByteArray(Map<String, String> data) throws IOException {
//		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
//		ObjectOutputStream out = new ObjectOutputStream(byteOut);
//		out.writeObject(data);
//		return byteOut.toByteArray();
//	}
//
//	private Map<String, String> parseHeaders(byte[] data) throws IOException, ClassNotFoundException {
//		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
//	    ObjectInputStream in = new ObjectInputStream(byteIn);
//	    @SuppressWarnings("unchecked")
//		Map<String, String> ret = (Map<String, String>) in.readObject();
//		return ret;
//	}

}
