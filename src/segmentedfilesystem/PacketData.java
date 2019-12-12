package segmentedfilesystem;

import java.util.Arrays;
import java.nio.ByteBuffer;

public class PacketData {

    byte fileId;
    byte[] packetNumber = new byte[2];
    short realNumber;
    byte[] data;
    boolean last = false;
    boolean header = false;

    int offset;
    int packetLength;

    public PacketData(byte[] packetData, int off, int length) {

        this.offset = off;
        this.packetLength = length;

        // Check to see if it's the last packet
        if(packetData[0] % 4 == 3) {
            last = true;
        }

        // Get file id
        this.fileId = packetData[1];

        // Check if is header packet
        if(packetData[0] % 2 == 0) {
            this.header = true;
            this.data = Arrays.copyOfRange(packetData, 2, packetLength);
            this.realNumber = -1;
        }
        // Otherwise, is data packet
        else {
//            if(last) {
//                this.packetNumber[0] = packetData[2];
//                this.packetNumber[1] = packetData[3];
//                this.data = Arrays.copyOfRange(packetData, 4, packetLength);
//                this.realNumber = ByteBuffer.wrap(this.packetNumber).getShort();
//            } else {
                this.packetNumber[0] = packetData[2];
                this.packetNumber[1] = packetData[3];
                this.data = Arrays.copyOfRange(packetData, 4, packetLength);
                this.realNumber = ByteBuffer.wrap(this.packetNumber).getShort();
//            }

        }
    }

    public Integer getPacketNumber() {
        return (int) this.realNumber;
    }

    public void printPacketNumber() {
        System.out.println( ByteBuffer.wrap(this.packetNumber).getShort() );
    }

}
