package nio.hw.sample_one;

// Клиент, передающий файл:
// Файл, размером 1.21 GB время передачи 61 секунд , buffer (256)
// Файл, размером 1.21 GB время передачи 29 секунд , buffer (512)
// Файл, размером 1.21 GB время передачи 12 секунд , buffer (1024)

import java.io.*;
import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NioClient {

    private static String HOST = "localhost";
    private static int PORT = 8189;
    private static final String FILE_NAME = "client_storage/java-книги.rar";

    public static void main(String args[]) throws IOException {

        InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);
        try (SocketChannel socketChannel = SocketChannel.open(serverAddress)) {

            RandomAccessFile file = new RandomAccessFile(FILE_NAME,"rw");
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(256);

            int bytesRead = channel.read(buffer); // bytesRead = -=512 здесь из файлового канала мы читаем байты в буфер, и присваиваем int размер полученных байтов
            while (bytesRead > -1) {
                buffer.flip(); // закрываем на запись, открываем на чтение
                while (buffer.hasRemaining()) { // пока в буфере есть каки-либо данные
                    socketChannel.write(buffer); // пишем в socketChannel из буфера
                }
                buffer.clear(); // очищаем буфер
                bytesRead = channel.read(buffer); // снова читаем из файлового канала в буфер, далее действуем по рекурсии, пока не передадим весь файл
            }
            file.close();
        }
    }
}
