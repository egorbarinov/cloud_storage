package nio.hw.protocol_data_transfer;

// Клиент, передающий файл:
// Файл, размером 1.21 GB время передачи 61 секунд , buffer (256)
// Файл, размером 1.21 GB время передачи 29 секунд , buffer (512)
// Файл, размером 1.21 GB время передачи 12 секунд , buffer (1024)

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NioClient {

    private static String HOST = "localhost";
    private static int PORT = 8189;

    public static void main(String args[]) throws IOException {

        String pathFile = "client_storage/java-книги.rar";
        Path path = Paths.get(pathFile);
        String fileName = path.getFileName().toString();  //вытаскиваем имя файла
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8); //сохраняем имя в байтовом массиве
        int fileNameSize = path.getFileName().toString().length();  // сохраняем длину имени файла
        long len = path.toFile().length(); // сохраняем размер файла

        InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);
        try (SocketChannel socketChannel = SocketChannel.open(serverAddress)) {
            RandomAccessFile file = new RandomAccessFile(pathFile,"rw");
            FileChannel channel = file.getChannel();
            //ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + fileNameBytes.length + 8);
            ByteBuffer buffer = ByteBuffer.allocate(256);

            buffer.put((byte)25).flip();
            socketChannel.write(buffer);
            buffer.clear(); // сигнальный байт о том, что будет отправляться файл, полетел в сторону сервера
            Thread.sleep(10);

            buffer.putInt(fileNameSize).flip();
            socketChannel.write(buffer); // буфер с длиной имени полетел в сторону сервера
            buffer.clear();
            Thread.sleep(10);

            buffer.put(fileNameBytes).flip();
            Thread.sleep(10);

            socketChannel.write(buffer); // буфер с именем полетел в сторону сервера
            buffer.clear();
            //дождаться респонса от сервера, что имя получено, после отправлять длину файла,
            Thread.sleep(10);

            buffer.putLong(len).flip();
            socketChannel.write(buffer);
            buffer.clear();
            Thread.sleep(10);

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

