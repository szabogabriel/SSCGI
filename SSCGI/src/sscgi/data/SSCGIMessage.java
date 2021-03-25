package sscgi.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SSCGIMessage {
	
	private static final byte[] EMPTY = new byte[] {};
	
	private byte[] headers;
	private byte[] body;
	
	public SSCGIMessage() {
		this(EMPTY, EMPTY);
	}
	
	public SSCGIMessage(byte[] data) {
		int headerLen = toInt(data);
		int bodyLen = toInt(data, 4);
		headers = new byte[headerLen];
		body = new byte[bodyLen];
		if (headerLen > 0) {
			System.arraycopy(data, 8, headers, 0, headerLen);
		}
		if (bodyLen > 0) {
			System.arraycopy(data, 8 + headerLen, body, 0, bodyLen);
		}
	}
	
	public SSCGIMessage(byte[] headers, byte[] body) {
		if (headers == null || body == null) throw new NullPointerException();
		
		setHeaders(headers);
		setBody(body);
	}
	
	public SSCGIMessage(InputStream in) throws IOException {
		byte[] tmp = read(in, 8);
		
		int headerLen = toInt(tmp);
		int bodyLen = toInt(tmp, 4);
		
		tmp = read(in, headerLen + bodyLen);
		headers = new byte[headerLen];
		body = new byte[bodyLen];
		
		System.arraycopy(tmp, 0, headers, 0, headerLen);
		System.arraycopy(tmp, headerLen, body, 0, bodyLen);
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
		byte[] dataToSend = new byte[8 + headers.length + body.length];
		
		System.arraycopy(toByteArray(headers.length), 0, dataToSend, 0, 4);
		System.arraycopy(toByteArray(body.length), 0, dataToSend, 4, 4);
		System.arraycopy(headers, 0, dataToSend, 8, headers.length);
		System.arraycopy(body, 0, dataToSend, 8 + headers.length, body.length);
		
		out.write(dataToSend);
	}
	
	private byte[] read(InputStream socketIn, int length) throws IOException {
		byte[] buffer = new byte[length];
		int read = socketIn.read(buffer, 0, length);
		
		if (read != length) {
			throw new IOException("Data not sufficient, or stream prematurally closed.");
		}
		
		return buffer;
	}
	
	private static byte[] toByteArray(int value) {
		return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}
	
	private static int toInt(byte[] value) {
		return toInt(value, 0);
	}
	
	private static int toInt(byte[] value, int offset) {
		return value[offset] << 24 | (value[offset + 1] & 0xFF) << 16 | (value[offset + 2] & 0xFF) << 8 | (value[offset + 3] & 0xFF);
	}

}
