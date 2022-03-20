package com.company;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ReaderApp {

    public static void main(String[] args) throws IOException {
        AtomicInteger ofset = new AtomicInteger(2);
        int bufferSize = ("Data Line 1000").length() * 2000;
        Path filePath = Paths.get("./socketnio.txt");
        RandomAccessFile raf = new RandomAccessFile("./socketnio.txt", "rw");
        try (FileChannel channnel = raf.getChannel()) {
            MappedByteBuffer buffer = channnel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);
            while(((char)buffer.get(0)) != '3') {
                String line = "";
                AtomicInteger lineCount = new AtomicInteger(0);
                if (((char)buffer.get(0)) != '0') {
                    for (int i = ofset.get(); i < bufferSize; i++) {
                        char a = (char)buffer.get(i);
                        if (a == '\n') {
                            if (line != null && !line.isEmpty()) {
                                if (line.equals("PartEnd")) {
                                    break;
                                } else {
                                    System.out.println(line);
                                }
                            }
                            line = "";
                        } else {
                            line += Character.toString(a);
                        }
                        ofset.getAndIncrement();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (((char)buffer.get(0)) == '2') {
                        buffer.put(0, (byte) '3'); //Stop
                    } else {
                        buffer.put(0, (byte) '0');
                    }
                }
            }
        }
    }
}
