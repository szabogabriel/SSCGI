package sscgi.demo;

public class EchoServer {
	
	public static void main(String [] args) {
		try {
			new SSCGIServer(65000, req -> new SSCGIMessage(new byte [] {}, ("Hello, " + new String(req.getBody())).getBytes()))));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
