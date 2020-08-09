package protocol_file_common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ProtoHandler extends ChannelDuplexHandler {

    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE
    }

    private State currentState = State.IDLE;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;
    private final String path = "./server_storage/";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf intBuf = ((ByteBuf) msg);
        while (intBuf.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte readed = intBuf.readByte();
                if (readed == (byte) 25) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0L;
                    System.out.println("STATE: Start file receiving");
                } else {
                    System.out.println("ERROR: Invalid first byte - " + readed);
                }
            }

            if (currentState == State.NAME_LENGTH) {
                if (intBuf.readableBytes() >= 4) {
                    System.out.println("STATE: Get filename length");
                    nextLength = intBuf.readInt();
                    currentState = State.NAME;
                }
            }

            if (currentState == State.NAME) {
                if (intBuf.readableBytes() >= nextLength) {
                    byte[] fileName = new byte[nextLength];
                    intBuf.readBytes(fileName);
                    System.out.println("STATE: Filename received - " + new String(fileName, "UTF-8"));
                    out = new BufferedOutputStream(new FileOutputStream(path + new String(fileName)));
                    currentState = State.FILE_LENGTH;
                }
            }

            if (currentState == State.FILE_LENGTH) {
                if (intBuf.readableBytes() >= 8) {
                    fileLength = intBuf.readLong();
                    System.out.println("STATE: File length received - " + fileLength);
                    currentState = State.FILE;
                }
            }

            if (currentState == State.FILE) {
                while (intBuf.readableBytes() > 0) {
                    out.write(intBuf.readByte());
                    receivedFileLength++;
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        System.out.println("File received");
                        out.close();
                        break;
                    }
                }
            }
        }
        if (intBuf.readableBytes() == 0) {
            intBuf.release();
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception { // TODO задействовать для отправки файлов с сервера клиенту
        ByteBuf outBuff = (ByteBuf) msg;
        try {
            ByteBuf resultBuff = ctx.alloc().buffer(outBuff.readableBytes());

            resultBuff.writeInt(outBuff.readableBytes());
            resultBuff.writeBytes(outBuff);
            ctx.writeAndFlush(resultBuff);

        } finally {
            ReferenceCountUtil.release(outBuff);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void onServerFileList (ChannelHandlerContext ctx) {             // метод для отправки списка файлов клиенту
        try {
            ArrayList<String> serverFileList = new ArrayList<>();
            Files.list(Paths.get("./server_storage/")).map(p -> p.getFileName().toString()).forEach(serverFileList::add);
            ctx.writeAndFlush(serverFileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
