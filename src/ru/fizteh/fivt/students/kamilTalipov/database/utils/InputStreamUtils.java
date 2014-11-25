package ru.fizteh.fivt.students.kamilTalipov.database.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class InputStreamUtils {
    public static int readInt(FileInputStream input) throws IOException {
        byte[] buffer = read(input, 4);

        ByteBuffer wrapped = ByteBuffer.wrap(buffer);
        return wrapped.getInt();
    }

    public static String readString(FileInputStream input, int length) throws IOException {
        if (length <= 0) {
            throw new IOException("Length must be positive number");
        }
        byte[] buffer = read(input, length);

        return new String(buffer, "UTF-8");
    }

    private static byte[] read(FileInputStream input, int length) throws IOException {
        byte[] buffer = new byte[length];
        int totalBytesRead = 0;
        while (totalBytesRead != length) {
            int currentBytesRead = input.read(buffer, totalBytesRead, length - totalBytesRead);
            if (currentBytesRead == -1) {
                throw new IOException("Couldn't read bytes: end of file");
            }
            totalBytesRead += length;
        }

        return buffer;
    }
}
