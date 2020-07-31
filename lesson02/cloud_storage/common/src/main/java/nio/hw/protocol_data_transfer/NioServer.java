package nio.hw.protocol_data_transfer;

//Сервер, принимающий файл:
// время скачивания 1.21 gb при буфере 256 - 60 сек.

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NioServer {

    private static State currentState = State.IDLE;
    private static int nextLength;
    private static long fileLength;
    private static long receivedFileLength;
    private static String nameOfFile;
    private static BufferedOutputStream out;


    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE
    }

    public static void main(String args[]) throws IOException {

        long start = System.currentTimeMillis();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(8189));
            try (SocketChannel socketChannel = serverSocketChannel.accept()) {

                //ByteBuffer buffer = ByteBuffer.allocate(256);
                ByteBuffer buffer = ByteBuffer.allocate(256);

                // Получаем сигнальный байт
                if (currentState == State.IDLE) {
                    socketChannel.read(buffer);
                    buffer.flip();
                    byte readed = buffer.get();  // получаем сигнальный байт
                    buffer.clear();
                    if (readed == (byte) 25) {
                        currentState = State.NAME_LENGTH;
                        receivedFileLength = 0L;
                        System.out.println("STATE: Start file receiving");
                    } else {
                        System.out.println("ERROR: Invalid first byte - " + readed);
                    }
                }

                //Получаем длину имени файла, ожидаем, что внутри лежит int
                if (currentState == State.NAME_LENGTH) {
                    socketChannel.read(buffer);
                    buffer.flip();
                    int fileNameSize;
                    if ((fileNameSize = buffer.getInt()) >= 4) {
                        buffer.clear();
                        System.out.println("STATE: Get filename length");
                        nextLength = fileNameSize;
                        currentState = State.NAME;
                    }
                }

                //получаем из канала массив байтов, где хранится имя файла
                if (currentState == State.NAME) {
                    socketChannel.read(buffer);
                    buffer.flip();
                    byte[] fileName = buffer.array();
                    buffer.clear();
                    if (fileName.length >= nextLength) {
                        nameOfFile = new String(fileName, StandardCharsets.UTF_8);
                        System.out.println("STATE: Filename received " + nameOfFile);
                        currentState = State.FILE_LENGTH;
                    }
                }

                // ожидаем из канала длину файла в long
                if (currentState == State.FILE_LENGTH) {
                    socketChannel.read(buffer);
                    buffer.flip();

                    if (buffer.capacity() >= 8) {
                        fileLength = buffer.getLong();   // читаем длину файла в long
                        buffer.clear();
                        System.out.println("STATE: File length received - " + fileLength);
                        currentState = State.FILE;
                    }
                }

                // Создадим путь к будущему файлу
                Path path = Paths.get("server_storage" ,nameOfFile.trim());
                try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    if (currentState == State.FILE) {
                        int bytesRead = socketChannel.read(buffer);
                        while (bytesRead > -1) {
                            buffer.flip();
                            while (buffer.hasRemaining()) {

                                fileChannel.write(buffer);
                                receivedFileLength++;
                                if (fileLength == receivedFileLength) {
                                    currentState = State.IDLE;
                                    System.out.println("File received");
                                    out.close();
                                    break;
                                }
                            }
                            buffer.clear();
                            bytesRead = socketChannel.read(buffer);
                        }
                    }
                }

            }
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) /1000 + "sec.");
    }
}

