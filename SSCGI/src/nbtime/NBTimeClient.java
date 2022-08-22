package nbtime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

public class NBTimeClient {

	private static final int DEFAULT_TIME_PORT = 8900;

	public static void main(String[] args) throws UnknownHostException, IOException {
		InetAddress lh = InetAddress.getLocalHost();

		long start = System.currentTimeMillis();
		IntStream.range(0, 10000).parallel().forEach(i -> {
			try {
				Socket s = new Socket(lh, DEFAULT_TIME_PORT);
				int read;
				byte[] buffer = new byte[1024];
				while ((read = s.getInputStream().read(buffer)) > 0) {
					String sss = new String(buffer, 0, read);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		long end = System.currentTimeMillis();
		
		System.out.println("10000 requests in " + (end - start) + "ms.");
	}

}
