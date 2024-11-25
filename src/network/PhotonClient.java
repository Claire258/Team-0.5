package network;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PhotonClient {
	//System.out.println("Client called");

    private static final int IN_PORT = 7501;  // Port the server listens on
    private static final int OUT_PORT = 7500; // Port the client uses to send data
    private static final String SERVER_ADDRESS = "127.0.0.1";  // Server address

    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;
    private InetAddress serverAddress;
    //Keep track of how many times 221 has been sent (3 to end game)
    private int terminationSigCount = 0;
    private String receivedData = ""; //Hold the last message
    
    

    // Constructor for initializing the client
    public PhotonClient() throws IOException {
		sendSocket = new DatagramSocket();
		receiveSocket = new DatagramSocket(IN_PORT);
		serverAddress = InetAddress.getByName(SERVER_ADDRESS);
        
    }

    public void sendEquipmentId(int equipmentId) throws IOException {
		String message = String.valueOf(equipmentId);
		sendBroadcast(message);
	}
	
	public void sendStartSignal() throws IOException {
		//System.out.println("Sending signal 202 to " + serverAddress + " : " + OUT_PORT);
		sendBroadcast("202");
	}
	
	public void sendEndSignal() throws IOException {
		sendBroadcast("221");
		sendBroadcast("221");
		sendBroadcast("221");
	}
	
	public void listenAndProcess() {
		byte[] buffer = new byte[1024];
		while(terminationSigCount < 3) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				receiveSocket.receive(packet);
				String data = new String(packet.getData(), 0, packet.getLength());
				processReceivedData(data);
			} catch (IOException e) {
				System.err.println("Error receiving packet: " + e.getMessage());
			}
		}
		//Close the socket when you're done
		sendSocket.close();
		receiveSocket.close();
		
	}
	
	private void processReceivedData(String data) throws IOException {
		this.receivedData = data;
		//See if it's time to end the game (send 3 times)
		if(data.equals("221")) {
			terminationSigCount++;
			//System.out.println("Termination signal count: " + terminationSigCount);
		} else if (data.contains(":")) {
			String [] parts = data.split(":");
			int playerTransmitting = Integer.parseInt(parts[0]);
			int playerHit = Integer.parseInt(parts[1]);
			sendEquipmentId(playerHit);
		}
	}
	
	public String getReceivedData() {
		return receivedData;
	}
	
	private void sendBroadcast (String message) throws IOException {
		byte [] data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, OUT_PORT);
		sendSocket.send(packet);
		//System.out.println("Broadcasted message: " + message);
	}
	
	public void run()
	{
		listenAndProcess();
	}
}
