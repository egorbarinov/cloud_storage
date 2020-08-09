package nio.lesson02;

import java.io.*;
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

        ////////////////////////////////////////////////////////////////////////////////////////

        // так можно прочитать байты из файла приложения в консоль посредством JavaNIO

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
        System.out.println();
        file.close();

        // а так можно вывести файл в консль посредсмтвом JavaIO
        int x = 0;
        try (InputStream is = new BufferedInputStream(new FileInputStream("./common/2/3/3.txt"))) {
           while ( (x = is.read()) != -1) {
               System.out.print((char) x);
           }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///////////////////////////////////////////////////////////////////////////

        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.put((byte)65);
        buf.put((byte)66);
 //       buf.put((byte)67); // при переполнении буфера возникнет Exception in thread "main" java.nio.BufferOverflowException
        buf.flip();
        System.out.println(buf.get()); // выведет 65
        System.out.println(buf.get()); //выведет 66
        buf.rewind();
//        System.out.println(buf.get()); // Exception in thread "main" java.nio.BufferUnderflowException
        System.out.println(buf.get()); // выведет 65
        System.out.println(buf.get()); //выведет 66

    }
}
