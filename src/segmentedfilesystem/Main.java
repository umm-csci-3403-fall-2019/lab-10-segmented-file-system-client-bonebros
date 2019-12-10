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
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName("csci-4409.morris.umn.edu");

        // Note: the DG-packet contains an empty byte-array (buf). This is fine when requesting a response.
        //     all that the server needs is the address/port number (which is automatically added to the packet).
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6014);
        socket.send(packet);

        // Receiving the packets
        System.out.println("Receiving started...");
        ArrayList<PacketData> allPacketData = new ArrayList<PacketData>(1);

        // It is implied that we are receiving 3 files. It is safe to say that when we receive 3 end packets, then
        // we sufficient information to reset 'maxPackets'
        int count = 0;
        int[] endPacketNums = new int[3];
        int endCount = 0;
        int maxPackets = Integer.MAX_VALUE;
        while (count < maxPackets) {
            System.out.println(count);
            count++;

            // Take the received packet, create a PacketData, and append to arraylist.
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
//            PacketData packetData = new PacketData(packet.getData());
            PacketData packetData = new PacketData(packet.getData(), packet.getOffset(), packet.getLength());

            allPacketData.add(packetData);

            if (packetData.header) {
                System.out.println(new String(buf, packet.getOffset(), packet.getLength()));
            }

            // Now check to see if it's an end packet, and reset the while loop condition if we have 3.
            if (packetData.last) {
                endPacketNums[endCount] = packetData.realNumber;
                endCount++;
                if (endCount == 3) {
                    maxPackets = sumArray(endPacketNums) + 6; // plus 3 for the headers, plus another 3 for 0-based indexing
                }
                System.out.println("actual end length" + new String(buf, 0, packet.getLength()));
            }
        }

        System.out.println("Done receiving!");
        System.out.println("Start debug? <enter 'y' to start>");

        Scanner mainScan = new Scanner(System.in);
        String debug = mainScan.next();

        if (debug.equals("y")) { debug(allPacketData); }
        else {return;}
    }

    public static int sumArray(int[] packetNums) {
        int sum = 0;
        for (int num : packetNums) {
            sum = sum + num;
        }
        return sum;
    }

    public static void debug(ArrayList<PacketData> allPacketData) {
        System.out.println("Debug started...please enter command");
        Scanner sn = new Scanner(System.in);
        while (true) {
            String input = sn.next();
            switch(input) {
                case "fuck you":
                    for (int i = 0; i < 10; i++) {
                        System.out.println("No, FUCK YOU ASSHOLE!");
                        return;
                    }
                case "files":
                    System.out.println("Making files...");
                    makeFiles(allPacketData);
                    break;
                case "q":
                    System.out.println("leaving...");
                    return;
            }
            System.out.println("Anything else?");
        }
    }


    public static ArrayList<PacketData> makeFiles(ArrayList<PacketData> allPacketData) {

        // Sort the packets by fileId into separate ArrayLists. Then add each to another ArrayList.
        ArrayList<ArrayList<PacketData>> unsortedFiles = sortPacketsById(allPacketData);

        // Now sort each packet list by the packet number.
        int count = 0;
        for (ArrayList<PacketData> unsortedFile : unsortedFiles) {

            // Sort the packets
            unsortedFile.sort(new FileSorter());

            // Time to write to the file.
            count++;
            File file = new File( new String(unsortedFile.get(0).data));
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
        return allPacketData;
    }

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

        System.out.println("f1: " + file1.size());
        System.out.println("f2: " + file2.size());
        System.out.println("f3: " + file3.size());

        ArrayList<ArrayList<PacketData>> filesList = new ArrayList<>();
        filesList.add(file1); filesList.add(file2); filesList.add(file3);

        return filesList;
    }

    public static ArrayList<Byte> getUniqueIds(ArrayList<PacketData> allPacketData) {
        ArrayList<Byte> ids = new ArrayList<>();
        for (PacketData packetData : allPacketData) {
            if (! (ids.contains(packetData.fileId))) {
                ids.add(packetData.fileId);
            }
        }
        System.out.println(ids);
        return ids;
    }

}
