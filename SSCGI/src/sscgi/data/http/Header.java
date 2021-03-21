package sscgi.data.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Header {
	
	private final String key;
	private final String value;
	
	public Header(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	private static byte[] serialize(Header header) {
		byte[] k = header.key.getBytes();
		byte[] v = header.value.getBytes();
		byte[] ret = new byte[k.length + v.length + 2];
		
		System.arraycopy(k, 0, ret, 0, k.length);
		ret[k.length] = (byte)0x00;
		System.arraycopy(v, 0, ret, k.length + 1, v.length);
		ret[ret.length - 1] = (byte)0x00;
		
		return ret;
	}
	
	public static byte[] serializeHeaders(List<Header> data) {
		ByteArrayOutputStream ret = new ByteArrayOutputStream();
		data.stream()
			.map(Header::serialize)
			.forEach(d -> {
				try {
					ret.write(d);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		);
		return ret.toByteArray();
	}
	
	public static List<Header> parseHeaders(byte[] data) {
		List<Header> ret = new ArrayList<>(32);
		
		byte[] key = new byte[] {}, value;
		int iK = 0, iV = 0;
		boolean keyPhase = true;
		for (int i = 0; i < data.length; i++) {
			if (keyPhase) {
				if (data[i] == 0) {
					keyPhase = false;
					key = new byte[i - iK];
					System.arraycopy(data, iK, key, 0, i - iK);
					iV = i + 1;
				}
			} else {
				if (data[i] == 0) {
					keyPhase = true;
					value = new byte[i - iV];
					System.arraycopy(data, iV, value, 0, i - iV);
					iK = i + 1;
					ret.add(new Header(new String(key), new String(value)));
				}
			}
		}
		
		return ret;
	}

}
