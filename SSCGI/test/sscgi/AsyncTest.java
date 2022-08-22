package sscgi;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import sscgi.client.RequestOrchestrator;
import sscgi.data.SSCGIMessage;
import sscgi.server.SSCGIServerChannel;

public class AsyncTest {

	@Test
	void mainTest() throws IOException, InterruptedException {
		SSCGIServerChannel server = new SSCGIServerChannel(65000,
				req -> new SSCGIMessage(new byte[] {}, ("Hello, " + new String(req.getBody())).getBytes()));

		Thread.sleep(1000);
		
		RequestOrchestrator client = new RequestOrchestrator(8, "localhost", 65000);
		
		long start = System.currentTimeMillis();
		
		IntStream.range(0, 100000)
		.parallel()
		.forEach(i -> {
			Optional<SSCGIMessage> response = client.sendrequest(new SSCGIMessage("".getBytes(), "World".getBytes()));
			String mes = new String(response.get().getBody());
//			System.out.println(mes);
			assertEquals("Hello, World", mes);
		});

		long stop = System.currentTimeMillis();
		
		System.out.println("Test done in " + (stop - start) + " ms.");
		
		server.stop();
	}

}
