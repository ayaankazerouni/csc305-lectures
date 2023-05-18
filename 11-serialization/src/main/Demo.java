package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class Demo {
    public static void main(String[] args) {
        short shortNum = 32673;

        try (RandomAccessFile raf = new RandomAccessFile(new File("raf.dat"), "rw")) {
            // Allocate a new ByteBuffer
            ByteBuffer bb = ByteBuffer.allocate(2);
            bb.putShort(shortNum);
            byte[] asArray = bb.array();

            raf.write(asArray);

            // Reading from a RAF
            byte[] readIn = new byte[2];
            raf.seek(0);
            int numRead = raf.read(readIn);
            short num = ByteBuffer.wrap(readIn).getShort();
            System.out.println(num);
        } catch (IOException e) {
            System.out.println("Cannot open this file");
        }
    }
}
