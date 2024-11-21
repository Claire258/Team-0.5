import view.PlayerEntryScreen;
import view.SplashScreen;
import network.PhotonServerSocket;
import database.PlayerManager;

public class Main {
    public static void main(String[] args) {
		//System.out.println("Beginning");
        PlayerManager playerManager = new PlayerManager();
        //System.out.println("Check?");
        SplashScreen splashScreen = new SplashScreen();
        //System.out.println("Checkk??");
        PhotonServerSocket pss = new PhotonServerSocket();
        //System.out.println("Checkeroni?");
        PlayerEntryScreen playerEntryScreen = new PlayerEntryScreen(pss);

        splashScreen.display();
        //System.out.println("1");
        playerEntryScreen.display();
        //System.out.println("2");
        //Thread serverThread = new Thread(() -> pss.start()); //added
		//System.out.println("3");
        playerManager.loadPlayers();
        //System.out.println("4");
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


    }
}
