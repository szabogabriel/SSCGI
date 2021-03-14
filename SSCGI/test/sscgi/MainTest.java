package sscgi;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sscgi.SSCGIMessage;
import sscgi.client.RequestOrchestrator;
import sscgi.server.SSCGIServer;

public class MainTest {

	@Test
	void mainTest() throws IOException, InterruptedException {
		SSCGIServer server = new SSCGIServer(65000,
				req -> new SSCGIMessage(new byte[] {}, ("Hello, " + new String(req.getBody())).getBytes()));

		Thread.sleep(1000);
		
		RequestOrchestrator client = new RequestOrchestrator(4, "localhost", 65000);
		
		Optional<SSCGIMessage> response;
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			
			response = client.sendrequest(new SSCGIMessage("".getBytes(), "World".getBytes()));
			assertEquals("Hello, World", new String(response.get().getBody()));
		}
		long stop = System.currentTimeMillis();
		
		System.out.println("10000 requests in " + (stop - start) + "ms.");
		
		
		server.stop();
	}

}
