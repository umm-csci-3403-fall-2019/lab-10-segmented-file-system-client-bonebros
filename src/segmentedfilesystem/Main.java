package segmentedfilesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) throws IOException {

        // Create the DG-socket
        DatagramSocket socket = new DatagramSocket();
        byte[] buf = new byte[1028];
        InetAddress address = InetAddress.getByName("csci-4409.morris.umn.edu");

        // Note: the DG-packet contains an empty byte-array (buf). This is fine when requesting a response.
        //     all that the server needs is the address/port number (which is automatically added to the packet).
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6014);
        socket.send(packet);

        // Receiving the packets
        System.out.println("Receiving started...");
        ArrayList<PacketData> allPacketData = new ArrayList<PacketData>(1);

        // It is implied that we are receiving 3 files. It is safe to say that when we receive 3 end packets, then
        // we sufficient information to reset 'maxPackets'. But we don't the limit until we get the end packets, so
        // it is set to maximum value.

        int count = 0;  int maxPackets = Integer.MAX_VALUE;
        int endCount = 0; int[] endPacketNums = new int[3];
        while (count < maxPackets) {
            count++;

            // Take the received packet, create a PacketData, and append to arraylist.
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            PacketData packetData = new PacketData(packet.getData(), packet.getLength());
            allPacketData.add(packetData);

            // Now check to see if it's an end packet, and reset the while loop condition if we have 3 total.
            if (packetData.last) {
                endPacketNums[endCount] = packetData.realNumber; // This array contains the highest packet number for each file.
                endCount++;
                if (endCount == 3) {
                    maxPackets = sumArray(endPacketNums) + 6; // plus 3 for the headers, plus another 3 for 0-based indexing
                }
            }
        }
        System.out.println("Done receiving!");
        makeFiles(allPacketData);
    }

    public static ArrayList<PacketData> makeFiles(ArrayList<PacketData> allPacketData) {

        // Sort the packets by fileId into separate ArrayLists. Then add each to another ArrayList.
        ArrayList<ArrayList<PacketData>> unsortedFiles = sortPacketsById(allPacketData);

        // Now sort each packet list by the packet number.
        for (ArrayList<PacketData> unsortedFile : unsortedFiles) {

            // Sort the packets using enumerator and write to file
            unsortedFile.sort(new FileSorter());
            writeToFile(unsortedFile);
        }
        return allPacketData;
    }

    // Separates all of the packets per each file into their respective file grouping, but in no particular order
    public static ArrayList<ArrayList<PacketData>> sortPacketsById(ArrayList<PacketData> allPacketData) {
        ArrayList<PacketData> file1 = new ArrayList<>();
        ArrayList<PacketData> file2 = new ArrayList<>();
        ArrayList<PacketData> file3 = new ArrayList<>();

        ArrayList<Byte> uniqueIds = getUniqueIds(allPacketData);

        for(PacketData packetData: allPacketData) {
            if(packetData.fileId == uniqueIds.get(0)){
                file1.add(packetData);
            } else if(packetData.fileId == uniqueIds.get(1)){
                file2.add(packetData);
            } else {
                file3.add(packetData);
            }
        }

        ArrayList<ArrayList<PacketData>> filesList = new ArrayList<>();
        filesList.add(file1); filesList.add(file2); filesList.add(file3);

        return filesList;
    }

    // Retrieves all of the file ID's
    public static ArrayList<Byte> getUniqueIds(ArrayList<PacketData> allPacketData) {
        ArrayList<Byte> ids = new ArrayList<>();

        // For each packet, if we encounter a new id, add it to the list.
        for (PacketData packetData : allPacketData) {
            if (! (ids.contains(packetData.fileId))) {
                ids.add(packetData.fileId);
            }
        }
        return ids;
    }

    public static void writeToFile(ArrayList<PacketData> unsortedFile) {

        // Get the data from the header packet, and use that as the filename.
        File file = new File( new String(unsortedFile.get(0).data) );

        // Now start writing to the file...
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            for (int i = 1; i < unsortedFile.size(); i++) {
                fos.write(unsortedFile.get(i).data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int sumArray(int[] packetNums) {
        int sum = 0;
        for (int num : packetNums) {
            sum = sum + num;
        }
        return sum;
    }

}
