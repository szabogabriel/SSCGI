package sscgi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

public class SSCGIServer {

	private ServerSocket ss;

	private SSCGIRequestHandler requestHandler;

	private ExecutorService executor = Executors.newCachedThreadPool();

	private Thread t;

	public SSCGIServer(int port, SSCGIRequestHandler requestHandler, ServerSocketFactory factory) throws IOException {
		this.requestHandler = requestHandler;
		ss = factory.createServerSocket(port);
		t = new Thread(this::handleNewConnection);
		t.start();
	}

	public SSCGIServer(int port, SSCGIRequestHandler requestHandler) throws IOException {
		this(port, requestHandler, ServerSocketFactory.getDefault());
	}

	private void handleNewConnection() {
		while (true) {
			try {
				Socket socket = ss.accept();
				executor.execute(new SSCGIClientHandler(socket, requestHandler));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		t.interrupt();
	}

}
