package sscgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
	
	private Map<String, String> headers = new HashMap<>();
	private String body = "";
	
	public HttpMessage(Map<String, String> headers, String body) {
		this.headers = headers;
		this.body = body;
	}
	
	public HttpMessage(SSCGIMessage message) throws ClassNotFoundException, IOException {
		if (message.getHeaders() != null) {
			headers = parseHeaders(message.getHeaders());
		}
		if (message.getBody() != null) {
			body = new String(message.getBody());
		}
	}
	
	public HttpMessage(SSCGIMessage message, String bodyEncoding) throws ClassNotFoundException, IOException {
		if (message.getHeaders() != null) {
			headers = parseHeaders(message.getHeaders());
		}
		if (message.getBody() != null) {
			body = new String(message.getBody(), bodyEncoding);
		}
	}
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public void removeHeader(String key) {
		if (headers.containsKey(key)) {
			headers.remove(key);
		}
	}
	
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public SSCGIMessage toMessage() throws IOException {
		return new SSCGIMessage(toByteArray(headers), body.getBytes());
	}

	private static byte[] toByteArray(Map<String, String> data) throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(data);
		return byteOut.toByteArray();
	}

	private Map<String, String> parseHeaders(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
	    ObjectInputStream in = new ObjectInputStream(byteIn);
	    @SuppressWarnings("unchecked")
		Map<String, String> ret = (Map<String, String>) in.readObject();
		return ret;
	}

}
