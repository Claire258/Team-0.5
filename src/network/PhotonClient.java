package network;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class PhotonClient {
	//System.out.println("Client called");

    private static final int IN_PORT = 7501;  // Port the server listens on
    private static final int OUT_PORT = 7500; // Port the client uses to send data
    private static final String SERVER_ADDRESS = "127.0.0.1";  // Server address

    private DatagramSocket socket;

    // Constructor for initializing the client
    public PhotonClient() {
			//System.out.println("Client called");
        try {
            socket = new DatagramSocket();
            System.out.println("Client socket initialized: " + socket);
        } catch (SocketException se) {
            System.out.println("Error setting up client socket");
            se.printStackTrace();
        }
    }

    // Sends a message (e.g., hit event) to the server
    public void sendHitEvent(String shooterId, String targetId) {
        String message = shooterId + ":" + targetId;
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), OUT_PORT);
            socket.send(packet);
            System.out.println("Sent message: " + message);
        } catch (Exception e) {
            System.out.println("Error sending hit event");
            e.printStackTrace();
        }
    }

    // Receives messages from the server
    public void receiveMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String receivedMessage = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            System.out.println("Received from server: " + receivedMessage);
        } catch (Exception e) {
            System.out.println("Error receiving message");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
		PhotonClient client = new PhotonClient();

    // Try sending a message first
		//client.sendHitEvent("101", "43");  // Send a hit event (player 101 hits the base)
		
		
    // Wait a short time and then try receiving
		try {
			Thread.sleep(1000);  // Give the server a moment to respond
			client.receiveMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	

		//clientSocket.receiveMessage();  // Receive the response from the server
}

}
