package sscgi.data.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeaderTest {
	
	String key1 = "Key1";
	String key2 = "Key2";
	String val1 = "Val1";
	String val2 = "Val2";
	
	byte[] k1v1;
	byte[] k1v1k2v2;
	
	@BeforeEach
	void prepare() throws IOException { 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(key1.getBytes());
		out.write((byte)0x00);
		out.write(val1.getBytes());
		out.write((byte)0x00);
		k1v1 = out.toByteArray();
		out = new ByteArrayOutputStream();
		out.write(key1.getBytes());
		out.write((byte)0x00);
		out.write(val1.getBytes());
		out.write((byte)0x00);
		out.write(key2.getBytes());
		out.write((byte)0x00);
		out.write(val2.getBytes());
		out.write((byte)0x00);
		k1v1k2v2 = out.toByteArray();
	}
	
	@Test
	void serializeOne() {
		List<Header> tmp = new ArrayList<>();
		Header h = new Header(key1, val1);
		tmp.add(h);
		
		byte[] data = Header.serializeHeaders(tmp);
		
		assertTrue(Arrays.equals(k1v1, data));
	}
	
	@Test
	void serializeTwo() {
		List<Header> tmp = new ArrayList<>();
		tmp.add(new Header(key1, val1));
		tmp.add(new Header(key2, val2));
		
		byte[] data = Header.serializeHeaders(tmp);
		
		assertTrue(Arrays.equals(k1v1k2v2, data));
	}
	
	@Test
	void parseOne() {
		List<Header> tmp = Header.parseHeaders(k1v1);
		
		assertEquals(1, tmp.size());
		assertEquals(key1, tmp.get(0).getKey());
		assertEquals(val1, tmp.get(0).getValue());
	}
	
	@Test
	void parseTwo() {
		List<Header> tmp = Header.parseHeaders(k1v1k2v2);
		
		assertEquals(2, tmp.size());
		assertEquals(key1, tmp.get(0).getKey());
		assertEquals(val1, tmp.get(0).getValue());
		assertEquals(key2, tmp.get(1).getKey());
		assertEquals(val2, tmp.get(1).getValue());
	}

}
