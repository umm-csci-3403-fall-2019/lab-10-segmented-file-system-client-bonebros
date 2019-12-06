package segmentedfilesystem;

import java.util.Arrays;

public class PacketData {

    byte fileId;
    byte[] packetNumber = new byte[2];
    byte[] data;
    boolean last = false;
    boolean header = false;

    public PacketData(byte[] packetData) {

        // Check to see if it's the last packet
        if(packetData[0] % 4 == 3) {
            last = true;
        }

        // Get file id
        this.fileId = packetData[1];

        // Check if is header packet
        if(packetData[0] % 2 == 0) {
            this.header = true;
            this.data = Arrays.copyOfRange(packetData, 2, packetData.length);
        }
        // Otherwise, is data packet
        else {
            this.packetNumber[0] = packetData[2];
            this.packetNumber[1] = packetData[3];
            this.data = Arrays.copyOfRange(packetData, 4, packetData.length);
        }
    }

}
