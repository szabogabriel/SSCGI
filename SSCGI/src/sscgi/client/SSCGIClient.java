package sscgi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import sscgi.SSCGIMessage;

public class SSCGIClient {

	private String host;
	private int port;

	private Socket socket;

	private InputStream socketIn;
	private OutputStream socketOut;

	public SSCGIClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private void setup() throws UnknownHostException, IOException {
		if (socket == null || socket.isClosed()) {
			socket = new Socket(host, port);
			
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);

			socketIn = socket.getInputStream();
			socketOut = socket.getOutputStream();
		}
	}

	public SSCGIMessage sendAndReceiveMessage(SSCGIMessage request) throws IOException {
		setup();
		request.serialize(socketOut);
		return new SSCGIMessage(socketIn);
	}

}
