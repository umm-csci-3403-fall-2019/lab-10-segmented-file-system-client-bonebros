package segmentedfilesystem;

import java.util.Arrays;
import java.nio.ByteBuffer;

//=========================================================================================================//

public class PacketData {

    byte fileId;
    byte[] packetNumber = new byte[2];
    short realNumber;
    byte[] data;
    boolean last = false;
    boolean header = false;

    //======================================================================================================//

    public PacketData(byte[] packetData, int length) {

        // Check to see if it's the last packet
        if(packetData[0] % 4 == 3) {
            last = true;
        }

        // Get file id
        this.fileId = packetData[1];

        // Check if is header packet
        if(packetData[0] % 2 == 0) {
            this.header = true;
            this.data = Arrays.copyOfRange(packetData, 2, length);
            this.realNumber = -1;
        }
        // Otherwise, is data packet
        else {
            this.packetNumber[0] = packetData[2];
            this.packetNumber[1] = packetData[3];
            this.data = Arrays.copyOfRange(packetData, 4, length);
            this.realNumber = ByteBuffer.wrap(this.packetNumber).getShort();
        }
    }

    //=========================================================================================================//

    public Integer getPacketNumber() {
        return (int) this.realNumber;
    }

    //=========================================================================================================//
}
