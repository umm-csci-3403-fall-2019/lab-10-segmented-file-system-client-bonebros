package segmentedfilesystem;
import java.util.Comparator;

public class FileSorter implements Comparator<PacketData>{

    @Override
    public int compare(PacketData p1, PacketData p2) {
        return p1.getPacketNumber().compareTo(p2.getPacketNumber());
    }
}
