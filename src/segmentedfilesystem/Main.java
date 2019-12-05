package segmentedfilesystem;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    int port;
    InetAddress address;
    DatagramSocket socket = null;
    DatagramPacket packet;
    byte[] sendBuf = new byte[256];
    
    public static void main(String[] args) throws IOException {

        // Check correct number args
//        if (args.length != -1) {
//            System.out.println("Usage: java QuoteClient");
//            return;
//        }


        try
        {
            // Create the DG-socket
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName("csci-4409.morris.umn.edu");

            // Note: the DG-packet contains an empty byte-array (buf). This is fine when requesting a response.
            //     all that the server needs is the address/port number (which is automatically added to the packet).
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 6014);
            socket.send(packet);

            // Receiving the packets
            int count = 0;
            ArrayList<DatagramPacket> packets = new ArrayList<DatagramPacket>(1);
            System.out.println("Receiving started...");
            while ( count < 500 ) {
                count++;
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                packets.add(packet);


                if (packet.getLength() < 256) {
                    System.out.println("End packet found");
                    System.out.println("Count: " + count);

                }

            }
            System.out.println("Done receiving!");

            Scanner sn = new Scanner(System.in);
            boolean debug = true;
            while (debug) {
                System.out.println("Starting debug...please enter command");
                String input = sn.next();
                switch(input) {
                    case "pall":
                        for (DatagramPacket pack : packets) {
                            System.out.println( pack.getData()[0] );
                        }
                        break;
                    case "p":
                        System.out.println("enter index to query");
                        int index = sn.nextInt();
                        try {
                            System.out.println("Print first and second bit");
                            System.out.println(packets.get(index).getData()[7]);
                            System.out.println(packets.get(index).getData()[6]);
                            break;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            break;
                        }
                    case "quit":
                        System.out.println("leaving...");
                        debug = false;
                        break;
                }

            }

        }
        catch (SocketException e) { e.printStackTrace(); }









    }

}
