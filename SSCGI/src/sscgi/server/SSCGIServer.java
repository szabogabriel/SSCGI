package sscgi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSCGIServer {
	
	private ServerSocket ss;
	
	private SSCGIRequestHandler requestHandler;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private Thread t;
	
	public SSCGIServer(int port, SSCGIRequestHandler requestHandler) throws IOException {
		this.requestHandler = requestHandler;
		ss = new ServerSocket(port);
		t = new Thread(this::handleNewConnection);
		t.start();
	}
	
	private void handleNewConnection() {
		int counter = 1;
		while (true) {
			try {
				Socket socket = ss.accept();
				System.out.println("New executor service: " + counter);
				executor.execute(new SSCGIClientHandler(counter++, socket, requestHandler));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		t.interrupt();
	}
	
}
