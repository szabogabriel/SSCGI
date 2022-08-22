package nbtime;

import java.net.InetAddress;
import java.net.Socket;

public class MyEchoClient {

	private static final int port = 65000;

	public static void main(String[] args) {
		try {
			InetAddress lh = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
			Socket socket = new Socket(lh, port);

			socket.getOutputStream().write("Hello, world!".getBytes());

			System.out.println("Data: ");
			int data;
			while ((data = socket.getInputStream().read()) != -1) {
				System.out.print((char) data);
			}

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
