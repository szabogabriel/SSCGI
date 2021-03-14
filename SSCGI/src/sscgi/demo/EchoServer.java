package sscgi.demo;

import java.io.IOException;

import sscgi.SSCGIMessage;
import sscgi.server.SSCGIServer;

public class EchoServer {
	
	public static void main(String [] args) {
		int port = 65000;
		if (args != null && args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.err.println("The input parameter must be the server port.");
				System.exit(-1);
			}
		}
		
		System.out.println("Starting server on port: " + port);
		try {
			new SSCGIServer(port, EchoServer::handleRequest);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static SSCGIMessage handleRequest(SSCGIMessage req) {
		String header = "";
		String body = "Hello, " + new String(req.getBody());
		return new SSCGIMessage(header.getBytes(), body.getBytes());
	}

}
