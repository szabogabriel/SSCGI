package sscgi.server;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

import sscgi.data.SSCGIMessage;

public class SSCGIServerChannel {

	private final SSCGIRequestHandler requestHandler;
	private final int port;

	public static void main(String[] args) throws Exception {
		new SSCGIServerChannel(65000, null);
	}

	public SSCGIServerChannel(int port, SSCGIRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
		this.port = port;
		new Thread(this::acceptConnections).start();
	}

	private void acceptConnections() {
		try {
			Selector selector = SelectorProvider.provider().openSelector();

			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.configureBlocking(false);

			InetAddress lh = InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 });
			InetSocketAddress isa = new InetSocketAddress(lh, port);
			ssc.socket().bind(isa);

			SelectionKey acceptKey = ssc.register(selector, SelectionKey.OP_ACCEPT);

			while (true) {
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> keys = readyKeys.iterator();

				while (keys.hasNext()) {
					SelectionKey sk = keys.next();
					keys.remove();

					if (sk.isAcceptable()) { // accept new connection
						ServerSocketChannel server = (ServerSocketChannel) sk.channel();
						SocketChannel channel = server.accept();
						channel.configureBlocking(false);
						channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(2048));
					} else if (sk.isReadable()) { // read data
						SocketChannel channel = (SocketChannel) sk.channel();
						ByteBuffer buffer = (ByteBuffer) sk.attachment();
						channel.read(buffer);

//						System.out.println("Read: " + buffer.position());

						SSCGIMessage request = new SSCGIMessage(buffer.array());
						SSCGIMessage response = requestHandler.handle(request);
						ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
						response.serialize(responseStream);

						buffer.clear();
						buffer.put(responseStream.toByteArray());
						buffer.flip();

						sk.interestOps(SelectionKey.OP_WRITE);
					} else if (sk.isWritable()) { // write data
						SocketChannel channel = (SocketChannel) sk.channel();
						try {
							ByteBuffer buffer = (ByteBuffer) sk.attachment();
							channel.write(buffer);
							if (buffer.hasRemaining()) {
								buffer.compact();
//							System.out.println("Dang!!!");
							} else {
								buffer.clear();
								sk.interestOps(SelectionKey.OP_READ);
							}
						} catch (Exception e) {
							e.printStackTrace();
							channel.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		// TODO
	}

}
