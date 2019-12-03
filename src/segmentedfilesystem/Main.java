package segmentedfilesystem;

import java.io.IOException;
import java.net.*;

public class Main {

    int port;
    InetAddress address;
    DatagramSocket socket = null;
    DatagramPacket packet;
    byte[] sendBuf = new byte[256];
    
    public static void main(String[] args) throws IOException {

        // Check correct number args
        if (args.length != -1) {
            System.out.println("Usage: java QuoteClient <hostname>");
            return;
        }


        try
        {
            // Create the DG-socket
            DatagramSocket socket = new DatagramSocket();
            byte[] buf = new byte[256];
            InetAddress address = InetAddress.getByName(args[0]);

            // Note: the DG-packet contains an empty byte-array (buf). This is fine when requesting a response.
            //     all that the server needs is the address/port number (which is automatically added to the packet).
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
            socket.send(packet);

            // Receiving the packet
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Quote of the moment: "  + received);

        }
        catch (SocketException e) { e.printStackTrace(); }







    }

}
