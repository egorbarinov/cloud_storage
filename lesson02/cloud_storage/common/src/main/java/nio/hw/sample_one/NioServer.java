package nio.hw.sample_one;

//Сервер, принимающий файл:

import java.io.*;
import java.net.InetSocketAddress;

import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NioServer {

    private static final String FILE_NAME = "server_storage/java-книги.rar";

    public static void main(String args[]) throws IOException {

        long start = System.currentTimeMillis();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8189));
            try (SocketChannel socketChannel = serverSocketChannel.accept()) {
                try (FileChannel fileChannel = FileChannel.open(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    fileChannel.transferFrom(socketChannel, 0, Long.MAX_VALUE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) /1000 + " sec.");


    }
}

