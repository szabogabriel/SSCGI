package sscgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SSCGIMessage {
	
	private byte[] headers;
	private byte[] body;
	
	public SSCGIMessage() {
		this(new byte [] {}, new byte [] {});
	}
	
	public SSCGIMessage(byte[] headers, byte[] body) {
		if (headers == null || body == null) throw new NullPointerException();
		
		setHeaders(headers);
		setBody(body);
	}
	
	public SSCGIMessage(InputStream in) throws IOException {
		int len = readLength(in);
		headers = read(in, len);
		len = readLength(in);
		body = read(in, len);
	}
	
	public void setHeaders(byte[] headers) {
		this.headers = headers;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	public byte[] getHeaders() {
		return headers;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public void serialize(OutputStream out) throws IOException {
		out.write(Integer.toString(headers.length).getBytes());
		out.write(58); // the character ':'
		out.write(headers);
		out.write(Integer.toString(body.length).getBytes());
		out.write(58); // the character ':'
		out.write(body);
	}
	
	private byte[] read(InputStream socketIn, int length) throws IOException {
		byte[] buffer = new byte[length];
		int read = socketIn.read(buffer, 0, length);
		if (read == -1) {
			throw new IOException("Stream closed.");
		}
		return buffer;
	}
	
	private int readLength(InputStream socketIn) throws IOException {
		int ret = 0;
		int buf;
		
		while ((buf = socketIn.read()) != ':') {
			if (buf == -1) {
				throw new IOException("Stream closed.");
			}
			ret = (ret * 10) + (buf - '0');
		}
		
		return ret;
	}

}
