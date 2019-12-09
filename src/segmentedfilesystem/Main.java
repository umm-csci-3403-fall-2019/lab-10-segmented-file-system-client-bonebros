package segmentedfilesystem;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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

        int count = 0;
        while ( count < 553 ) {
            System.out.println(count);
            count++;

            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            PacketData packetData = new PacketData(packet.getData());
            allPacketData.add(packetData);
        }

        System.out.println("Done receiving!");
        System.out.println("Start debug? <enter 'y' to start>");

        Scanner mainScan = new Scanner(System.in);
        String debug = mainScan.next();

        if (debug.equals("y")) { debug(allPacketData); }
        else {return;}
    }

    public static void debug(ArrayList<PacketData> allPacketData) {
        System.out.println("Debug started...please enter command");
        Scanner sn = new Scanner(System.in);
        while (true) {
            String input = sn.next();
            switch(input) {
                case "p":
                    System.out.println("enter index of PacketData to query");
                    int index = sn.nextInt();
                    queryPacket( allPacketData.get(index) );
                    break;
//                case "pall":
//                    pAll(allPacketData);
//                    break;
                case "list":
                    sortPacketsById(allPacketData);
                    break;
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

    public static void queryPacket(PacketData packetData) {

        System.out.println("Enter name of field to query.");
        Scanner sn1 = new Scanner(System.in);
        while (true) {
            String input = sn1.next();
            if (input.equals("q")) {
                System.out.println("Returning to top...");
                return;
            }
            else if (input.equals("h")) {
                System.out.println( "commands: id, last, header, num");
            }
            else if (input.equals("id")) {
                System.out.println( packetData.fileId);
            }
            else if (input.equals("last")) {
                System.out.println( packetData.last);
            }
            else if (input.equals("header")) {
                System.out.println( packetData.header);
            }
            else if (input.equals("num")) {
                packetData.printPacketNumber();
            }

            System.out.println("Enter another query? (Enter 'q' to return to DEBUG ... 'h' for help)");
        }
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

    public static ArrayList<PacketData> makeFiles(ArrayList<PacketData> allPacketData) {

        // Take the packet list, sort into separate (unsorted) arrays by file
        ArrayList<ArrayList<PacketData>> unsortedFiles = sortPacketsById(allPacketData);

        for (ArrayList<PacketData> unsortedFile : unsortedFiles) {
            unsortedFile.sort(new FileSorter());

//            try {
//                System.out.println("first packet:" + unsortedFile.get(0).realNumber);
//                System.out.println("second packet:" + unsortedFile.get(1).realNumber);
//                System.out.println("third packet:" + unsortedFile.get(2).realNumber);
//                System.out.println("==================================");
//            } catch (IndexOutOfBoundsException ex) {
//                continue;
//            }
        }


        return allPacketData;
    }

//    public static void pAll(ArrayList<PacketData> packetDatas) {
//
//        System.out.println("Enter the index of the byte-arrays (to print for all)");
//        Scanner sn1 = new Scanner(System.in);
//        while (true) {
//
//            String input = sn1.next();
//            if (input.equals("q")) {
//                System.out.println("Returning to top...");
//                return;
//            }
//            else {
//                for (int i = 0; i < packets.size(); i++) {
//                    System.out.println( packets.get(i).getData()[ Integer.parseInt(input) ] );
//                }
//            }
//            System.out.println("Enter another index?");
//        }
//
//
//    }



}
