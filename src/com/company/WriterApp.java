package com.company;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class WriterApp {

    public static void main(String[] args) throws IOException {
        AtomicInteger ofset = new AtomicInteger(2);
        int bufferSize = ("Data Line 1000").length() * 2000;
        Path filePath = Paths.get("./socketnio.txt");
        RandomAccessFile raf = new RandomAccessFile("./socketnio.txt", "rw");
        try (FileChannel channnel = raf.getChannel()) {
            MappedByteBuffer buffer = channnel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
            AtomicInteger lineCount = new AtomicInteger(0);
            for (int i = 0; i < 1000; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (((char)buffer.get(0)) != '0') {
                    i--;
                    System.out.println("Part Writing Permission waiting...");
                    continue;
                }

                System.out.println("Part Writing Process Start...");
                byte[] data = ("Data Line " + i + "\n").getBytes(StandardCharsets.UTF_8);
                for(int j = 0; j < data.length; j++) {
                    buffer.put(ofset.get() + j, data[j]);
                }

                ofset.getAndAdd(data.length);
                if (lineCount.incrementAndGet() == 5) {
                    byte[] dataPartEnd = ("PartEnd\n").getBytes(StandardCharsets.UTF_8);
                    for(int j = 0; j < dataPartEnd.length; j++) {
                        buffer.put(ofset.get() + j, dataPartEnd[j]);
                    }
                    ofset.getAndAdd(dataPartEnd.length);
                    lineCount.set(0);
                    if (i == 1000 - 1) {
                        buffer.put(0, (byte) '2'); // Last reading permisssion
                    } else {
                        buffer.put(0, (byte) '1');
                    }
                }
                System.out.println("Part Writing Process End...");
            }
        }
    }
}
