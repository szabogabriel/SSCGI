package sscgi.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import sscgi.data.SSCGIMessage;

public class SSCGIClient {

	private String host;
	private int port;

	private Socket socket;

	private InputStream socketIn;
	private OutputStream socketOut;
	
	private SocketFactory socketFactory;

	public SSCGIClient(String host, int port, SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
		this.host = host;
		this.port = port;
		try {
			setup();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setup() throws UnknownHostException, IOException {
		socket = socketFactory.createSocket(host, port);

		socket.setKeepAlive(true);
		socket.setTcpNoDelay(true);

		socketIn = socket.getInputStream();
		socketOut = socket.getOutputStream();
	}

	public SSCGIMessage sendAndReceiveMessage(SSCGIMessage request) throws IOException {
		try {
			request.serialize(socketOut);
			socketOut.flush();
		} catch (Exception ex) {
			setup();
			request.serialize(socketOut);
		}
		SSCGIMessage ret = new SSCGIMessage(socketIn);
		return ret;
	}

}
