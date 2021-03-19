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
		int headerLen = toInt(read(in, 4));
		int bodyLen = toInt(read(in, 4));
		headers = read(in, headerLen);
		body = read(in, bodyLen);
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
		out.write(toByteArray(headers.length));
		out.write(toByteArray(body.length));
		out.write(headers);
		out.write(body);
	}
	
	private byte[] read(InputStream socketIn, int length) throws IOException {
		byte[] buffer = new byte[length];
		int read = socketIn.read(buffer, 0, length);
		
		if (read == -1) {
			throw new IOException("Stream closed.");
		}
		
		if (read != length) {
			throw new IOException("Data not sufficient, or stream prematurally closed.");
		}
		
		return buffer;
	}
	
	private byte[] toByteArray(int value) {
		return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	private int toInt(byte[] value) {
		return value[0] << 24 | (value[1] & 0xFF) << 16 | (value[2] & 0xFF) << 8 | (value[3] & 0xFF);
	}

}
