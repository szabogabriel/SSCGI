package sscgi.client;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.SocketFactory;

import sscgi.SSCGIMessage;

public class RequestOrchestrator {
	
	private BlockingQueue<SSCGIClient> handlers;
	
	public RequestOrchestrator(int capacity, String serverHost, int serverPort, SocketFactory factory) {
		handlers = new ArrayBlockingQueue<>(capacity);
		
		while (handlers.offer(new SSCGIClient(serverHost, serverPort, factory)));
	}
	
	public RequestOrchestrator(int capacity, String serverHost, int serverPort) {
		this(capacity, serverHost, serverPort, SocketFactory.getDefault());
	}
	
	public Optional<SSCGIMessage> sendrequest(SSCGIMessage request) {
		Optional<SSCGIMessage> ret = Optional.empty();
		try {
			SSCGIClient handler = handlers.take();
			ret = Optional.of(handler.sendAndReceiveMessage(request));
			handlers.offer(handler);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			//TODO: return handle, if there was an error during communication.
		}
		return ret;
	}

}
