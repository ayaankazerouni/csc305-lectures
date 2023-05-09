package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Demo {
    public static void main(String[] args) {
        short shortNum = 32673;

        try (RandomAccessFile raf = new RandomAccessFile(new File("raf.dat"), "rw")) {
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.putShort(shortNum);
            byte[] b = bb.array();
            raf.write(b);

            byte[] input = new byte[2];
            raf.seek(0);
            raf.read(input, 0, 2);
            for (byte c : input) {
                System.out.println(c);
            }
        } catch (IOException e) {
            System.out.println("Cannot open this file");
        }
    }
}
