package sscgi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sscgi.data.SSCGIMessage;

public class SSCGIClientHandler implements Runnable {
	
	private SSCGIRequestHandler requestHandler;

	private Socket socket;

	private InputStream socketIn;
	private OutputStream socketOut;
	
	public SSCGIClientHandler(Socket socket, SSCGIRequestHandler requestHandler) throws IOException {
		this.socket = socket;
		
		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);

		socketIn = socket.getInputStream();
		socketOut = socket.getOutputStream();

		this.requestHandler = requestHandler;
	}

	@Override
	public void run() {
		SSCGIMessage request, response;

		try {
			while (true) {
				request = new SSCGIMessage(socketIn);
				response = requestHandler.handle(request);
				response.serialize(socketOut);
			}
		} catch (Exception e) {
			if ("Stream closed.".equals(e.getMessage())) {
				System.out.println("Stream closed.");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (!socket.isClosed()) {
					socket.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
