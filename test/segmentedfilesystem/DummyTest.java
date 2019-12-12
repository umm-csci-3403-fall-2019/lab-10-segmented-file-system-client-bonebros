package segmentedfilesystem;

import static org.junit.Assert.*;

import org.junit.Test;

import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is just a stub test file. You should rename it to
 * something meaningful in your context and populate it with
 * useful tests.
 */
public class DummyTest{

    public DummyTest()  {
    }

    @Test
    public void isHeader() {
        // Bytes for packet number.. 3*256... + 4.
        byte[] headerBuff = new byte[]{2,2,3,4};
        assertEquals(headerBuff[0] % 2, 0);
    }

    @Test
    public void isLastPacket() {
        byte[] lastBuff = new byte[]{7,2,2,2};
        assertEquals(lastBuff[0] % 4, 3);
    }

    @Test
    public void extractsFileName() {
        String fileName = "hello_world.txt";
        byte[] nameByte = fileName.getBytes();
        byte[] fileNameData = Arrays.copyOfRange(nameByte, 0, nameByte.length);
        assertEquals(new String(fileNameData), "hello_world.txt");
    }

    @Test
    public void isDataPacket() {
        byte[] dataBuffer = new byte[]{5,2,2,2};
        if(dataBuffer[0] % 4 == 3) {
            System.out.println("Last packet, but still a data packet");
        } else if (dataBuffer[0] % 2 == 0) {
            fail("Header packet");
        } else {
            System.out.println("A data packet");
            assert true;
        }
    }

    @Test
    public void retrieveFileID() {
        byte[] dataBuffer = new byte[]{5,2,2,2};
        byte fileID = dataBuffer[1];
        assertEquals(fileID, dataBuffer[2]); //retrieves right byte representation
        assertEquals((int) fileID, 2); //retrieves right int representation of the byte

    }
}
