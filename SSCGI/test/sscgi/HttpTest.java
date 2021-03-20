package sscgi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import sscgi.client.RequestOrchestrator;
import sscgi.server.SSCGIServer;

public class HttpTest {

	@Test
	void mainTest() throws IOException, InterruptedException {
		SSCGIServer server = new SSCGIServer(65000, HttpTest::handle);

		Thread.sleep(1000);

		RequestOrchestrator client = new RequestOrchestrator(4, "localhost", 65000);
		HttpMessage request = createRequest();
		String targetAnswer = "Hello 2 times, World";

		long start = System.currentTimeMillis();

		IntStream.range(0, 10000).parallel().forEach(i -> {
			try {
				Optional<SSCGIMessage> response;

				response = client.sendrequest(request.toMessage());

				assertEquals(targetAnswer, new String(response.get().getBody()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		long stop = System.currentTimeMillis();

		System.out.println("Test done in " + (stop - start) + " ms.");

		server.stop();
	}

	private HttpMessage createRequest() throws IOException {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "text/plain");
		headers.put("Request-method", "GET");

		HttpMessage req = new HttpMessage(headers, "World");

		return req;
	}

	private static SSCGIMessage handle(SSCGIMessage req) {
		try {
			HttpMessage ret = null, hreq = new HttpMessage(req);
			Map<String, String> headers = hreq.getHeaders();
			int size = headers.size();
			String body = hreq.getBody();
			String retMessage = "Hello " + size + " times, " + body;
			ret = new HttpMessage(hreq.getHeaders(), retMessage);
			return ret.toMessage();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
