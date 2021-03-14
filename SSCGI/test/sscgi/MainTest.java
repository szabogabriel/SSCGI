package sscgi;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import sscgi.client.RequestOrchestrator;
import sscgi.server.SSCGIServer;

public class MainTest {

	@Test
	void mainTest() throws IOException, InterruptedException {
		SSCGIServer server = new SSCGIServer(65000,
				req -> new SSCGIMessage(new byte[] {}, ("Hello, " + new String(req.getBody())).getBytes()));

		Thread.sleep(1000);
		
		RequestOrchestrator client = new RequestOrchestrator(4, "localhost", 65000);
		
		long start = System.currentTimeMillis();
		
		IntStream.range(0, 10000).parallel().forEach(i -> {
			Optional<SSCGIMessage> response = client.sendrequest(new SSCGIMessage("".getBytes(), "World".getBytes()));
			assertEquals("Hello, World", new String(response.get().getBody()));
		});

		long stop = System.currentTimeMillis();
		
		System.out.println("Test done in " + (stop - start) + " ms.");
		
		server.stop();
	}

}
