//Server is ran through the other file. We do not need a server at all, get rid of this file entirely.


package network;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class PhotonServerSocket {
    private static final int IN_PORT = 7500;  // Port the client will send data to
    private static final int OUT_PORT = 7501; // Port the server sends responses to
    private static final String SERVER_ADDRESS = "127.0.0.1"; // Localhost for communication

    private ExecutorService exe = Executors.newCachedThreadPool();  // Executor service for handling clients
    private DatagramSocket sin;
    private DatagramSocket sout;
    
    private ClientHandler ch;

    // Constructor to initialize the server
    public PhotonServerSocket() {
        try {
            sin = new DatagramSocket(IN_PORT);
            sout = new DatagramSocket(OUT_PORT);
            System.out.println("Server input socket initialized: " + sin);
            sin.setReuseAddress(true); //added
            sout.setReuseAddress(true); //added
        } catch (SocketException se) {
            System.out.println("Error setting up sockets");
            se.printStackTrace();
        }
        if(sin == null || sout == null)
        {
			System.out.println("Error: sockets are currently null");
		}
        else {
			System.out.println("sin and sout are not null");
			AddClientHandler();
		}
    }

    // Starts the client handler to listen for incoming messages
    private void AddClientHandler() {
        System.out.println("Client handler setuppp");
        ch = new ClientHandler(sin, this);
        exe.submit(ch);  // Run client handler in a separate thread
    }

    // Method to send a code to the client (to assign ID or notify)
    public void assignCode(int code) {
        String codeString = String.valueOf(code);
        byte[] byteArray = codeString.getBytes(StandardCharsets.UTF_8);

        try {
            DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, InetAddress.getByName(SERVER_ADDRESS), OUT_PORT);
            sout.send(packet);
        } catch (Exception e) {
            System.out.println("Error sending out a code");
            e.printStackTrace();
        }
    }

    // Decode the received code and relay it back to the main program's log
    public void Decode(String code, ClientHandler ch) {
        System.out.println("Received message: " + code);
        String response = null;

        // Validate input format
        if (code == null || code.trim().isEmpty()) {
            response = "Invalid message received (empty or null)";
            sendResponse(response, ch);
            return;
        }

        // Logic based on code structure (e.g., "shooter:target")
        if (code.contains(":")) {
            String[] players = code.split(":");
            if (players.length == 2) {
                String shooter = players[0];
                String target = players[1];

                // Handle hit events
                if (Integer.parseInt(target) == 43) {
                    response = "Player " + shooter + " hit the base!";
                } else if (Integer.parseInt(target) == 53) {
                    response = "Player " + shooter + " hit the green base!";
                } else {
                    response = "Player " + shooter + " hit Player " + target;
                }
            } else {
                response = "Invalid player format in code: " + code;
            }
        } else if (code.equals("202")) {
            response = "Game started!";
        } else if (code.equals("221")) {
            response = "Game ended!";
            // Send response and stop the server
            sendResponse(response, ch);
            StopRunningThread(); // Shut down the server once the game ends
        } else {
            response = "Invalid code received: " + code;
        }

        // Send the response back to the client
        if (response != null) {
            sendResponse(response, ch);
        }
    }

    // Sends the response back to the client
    private void sendResponse(String message, ClientHandler ch) {
        try {
            byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), OUT_PORT);
            sout.send(responsePacket);
            System.out.println("Response sent: " + message);
        } catch (Exception e) {
            System.out.println("Error sending response: " + message);
            e.printStackTrace();
        }
    }

    // Clean up the server threads
    public void StopRunningThread() {
        try {
            ch.StopRunning();  // Stop the client handler thread
            exe.shutdownNow();  // Attempt to stop all tasks
            if (!exe.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("Executor did not terminate in the specified time.");
            }
        } catch (Exception e) {
            System.out.println("Error shutting down the thread");
            e.printStackTrace();
        }
    }

    // Client handler that listens for UDP messages
    private static class ClientHandler implements Runnable {

        private DatagramSocket sin;
        private PhotonServerSocket pss;

        private boolean keepRunning = true;

        public ClientHandler(DatagramSocket sin, PhotonServerSocket pss) {
            this.pss = pss;
            this.sin = sin;
        }

        public void run() {
            while (keepRunning) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    sin.receive(packet);

                    int length = packet.getLength();
                    byte[] data = packet.getData();
                    pss.Decode(new String(data, 0, length, StandardCharsets.UTF_8), this);  // Process received message
                } catch (Exception e) {
                    System.out.println("Error in receiving packets");
                    e.printStackTrace();
                }
            }
        }

        public void StopRunning() {
            keepRunning = false;
        }
    }

    /*public static void main(String[] args) {
        try {
			PhotonServerSocket server = new PhotonServerSocket();
			System.out.println("Server started...");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
    }*/
}
