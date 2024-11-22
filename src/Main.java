import view.PlayerEntryScreen;
import view.SplashScreen;
import network.PhotonClient;
import database.PlayerManager;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
	
	try {
        PlayerManager playerManager = new PlayerManager();

        SplashScreen splashScreen = new SplashScreen();
    
        PhotonClient pss = new PhotonClient();
        PlayerEntryScreen playerEntryScreen = new PlayerEntryScreen(pss);

        splashScreen.display();

        playerEntryScreen.display();
        
        playerManager.loadPlayers();
 
        Thread serverThread = new Thread(() -> {
			try {
				pss.run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
		serverThread.start();
	} catch (IOException e) {
		e.printStackTrace();
	}
	}
}
