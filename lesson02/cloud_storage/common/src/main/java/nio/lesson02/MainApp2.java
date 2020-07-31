package nio.lesson02;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MainApp2 {
// передача файлов через каналы NIO


    public static void main(String[] args) throws IOException {

        RandomAccessFile src = new RandomAccessFile("./common/2/3/3.txt", "rw");
        FileChannel srcCh = src.getChannel();

        RandomAccessFile dst = new RandomAccessFile("./common/2/3/5.txt", "rw");
        FileChannel dstCh = dst.getChannel();

        srcCh.transferTo(0, srcCh.size(), dstCh); // перемещение по каналам
//        dstCh.transferFrom(dstCh,0, srcCh.size()); // идетнично будет работать

        // FileChannel - файловый канал (fc.transferTo(sc)
        // DatagramChannel (UDP)
        // SocketChannel (TCP)
        // ServerSocketChannel

        // так можно прочитать байты из файла приложения в консоль
        RandomAccessFile file = new RandomAccessFile("./common/2/3/3.txt", "rw");
        FileChannel channel = file.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(8); //создаем ByteBuffer и говорим ему, что он на 8 байт
        int bytesRead = channel.read(buffer);
        while (bytesRead > -1){
            buffer.flip();
            while (buffer.hasRemaining()) { // пока в буфере есть каки-либо данные
                System.out.print((char) buffer.get());
            }
            buffer.clear();
            bytesRead = channel.read(buffer);
        }
        file.close();

    }
}
