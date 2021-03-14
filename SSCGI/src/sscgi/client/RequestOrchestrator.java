package sscgi.client;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sscgi.SSCGIMessage;

public class RequestOrchestrator {
	
	private BlockingQueue<SSCGIClient> handlers;
	
	public RequestOrchestrator(int capacity, String serverHost, int serverPort) {
		handlers = new ArrayBlockingQueue<>(capacity);
		
		while (handlers.offer(new SSCGIClient(serverHost, serverPort)));
	}
	
	public Optional<SSCGIMessage> sendrequest(SSCGIMessage request) {
		Optional<SSCGIMessage> ret = Optional.empty();
		try {
			SSCGIClient handler = handlers.take();
			ret = Optional.of(handler.sendAndReceiveMessage(request));
			handlers.offer(handler);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
