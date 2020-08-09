package nio.lesson02;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NIOServer implements Runnable {

    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buf = ByteBuffer.allocate(256);
    private int acceptedClientIndex = 1;
    private final ByteBuffer welcomeBuf = ByteBuffer.wrap("Добро пожаловать в чат!\n".getBytes());

    NIOServer() throws IOException {
        this.server = ServerSocketChannel.open();
        this.server.socket().bind(new InetSocketAddress(8189));
        this.server.configureBlocking(false);
        this.selector = Selector.open();
        this.server.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            System.out.println("Сервер запущен (Порт: 8189)");
            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (this.server.isOpen()) {
                selector.select();
                iter = this.selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();
                    if (key.isAcceptable()) {
                        this.handleAccept(key);
                    }
                    if (key.isReadable()) {
                        this.handleRead(key);
                    }
                }
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
        String clientName = "Клиент #" + acceptedClientIndex;
        acceptedClientIndex++;
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ, clientName);
        sc.write(welcomeBuf);
        welcomeBuf.rewind();
        System.out.println("Подключился новый клиент " + clientName);
    }

    private void handleRead(SelectionKey key) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(80);
        int count = ((SocketChannel)key.channel()).read(buffer);
        if (count == -1) {
            key.channel().close();
            return;
        }
        buffer.flip();
        StringBuilder sb = new StringBuilder();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        for (SelectionKey key1 : selector.keys()) {
            if (key1.channel() instanceof SocketChannel && key1.isReadable()) {
                ((SocketChannel)key1.channel()).write(ByteBuffer.wrap(sb.toString().getBytes()));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new NIOServer()).start();

    }
}
