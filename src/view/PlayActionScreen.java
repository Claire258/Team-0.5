package view;

import model.Player;

import network.PhotonClient;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JLabel;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class PlayActionScreen {

	private static final int GAME_TIMER = 360;
	private int gameTimer = GAME_TIMER;
	private static final int TIME_REMAINING = 30;
	private JLabel countdownTimer;
	private JLabel greenTeamScoreLabel;
	private JLabel redTeamScoreLabel;
	private Timer timer;
	private int timeRemaining = TIME_REMAINING;

	private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
	private static final Color LIGHT_TEXT = new Color(200, 200, 200);

	private List<Player> greenTeamPlayers;
	private List<Player> redTeamPlayers;
	private Map<Integer, String> equipmentMap;
	private Map<Integer, Integer> playerScores;

	private JPanel greenTeamPlayerListPanel;
	private JPanel redTeamPlayerListPanel;

	private int greenTeamScore = 0;
	private int redTeamScore = 0;

	private JTextArea actionLogArea;

    private PhotonClient pss;
	private Clip musicClip;

	public PlayActionScreen(List<Player> greenTeamPlayers, List<Player> redTeamPlayers, Map<Integer, String> equipmentMap, PhotonClient pss) {
		this.greenTeamPlayers = greenTeamPlayers;
		this.redTeamPlayers = redTeamPlayers;
		this.equipmentMap = equipmentMap;
		this.pss = pss;
		this.playerScores = new HashMap<>();
		for (Player player : greenTeamPlayers) {
			playerScores.put(player.getId(), 0);
		}
		for (Player player : redTeamPlayers) {
			playerScores.put(player.getId(), 0);
		}
	}

	public void display() {
		JFrame frame = new JFrame("Laser Tag - Play Action");
		frame.setSize(1000, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		countdownTimer = new JLabel("Countdown to laser mayhem: " + timeRemaining, SwingConstants.CENTER);
		countdownTimer.setFont(new Font("Arial", Font.BOLD, 24));
		countdownTimer.setForeground(LIGHT_TEXT);
		countdownTimer.setBackground(DARK_BACKGROUND);
		countdownTimer.setOpaque(true);
		frame.add(countdownTimer, BorderLayout.NORTH);

		JPanel teamPanel = new JPanel(new GridLayout(1, 3));

		JPanel greenTeamPanel = createTeamPanel("Green Team", greenTeamPlayers, Color.GREEN);
		JPanel redTeamPanel = createTeamPanel("Red Team", redTeamPlayers, Color.RED);
		JPanel actionLogPanel = createActionLogPanel();
		
		JPanel scorePanel = new JPanel(new GridLayout(2,1));
		scorePanel.setBackground(DARK_BACKGROUND);
		
		//Display green team total score
		int greenScore = scoreTotal(greenTeamPlayers);
		greenTeamScoreLabel = new JLabel("Green team Score: " + greenScore, SwingConstants.CENTER);
		greenTeamScoreLabel.setForeground(Color.GREEN);
		greenTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
		//Display red team total score
		int redScore = scoreTotal(redTeamPlayers);
		redTeamScoreLabel = new JLabel("Red team Score: " + redScore, SwingConstants.CENTER);
		redTeamScoreLabel.setForeground(Color.RED);
		redTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
		//Add score panels to the screen
		scorePanel.add(greenTeamScoreLabel);
		scorePanel.add(redTeamScoreLabel);

		teamPanel.add(greenTeamPanel);
		teamPanel.add(actionLogPanel);
		teamPanel.add(redTeamPanel);

		frame.add(scorePanel, BorderLayout.SOUTH);
		frame.add(teamPanel, BorderLayout.CENTER);
		frame.getContentPane().setBackground(DARK_BACKGROUND);
		frame.setVisible(true);

		updateTeamScores();
		startCountdownTimer();
	}
	
	//Find total score for all players on a team
	    public int scoreTotal(List<Player> team)
    {
		int totalScore = 0;
		
		for(Player player: team)
		{
			totalScore += player.getScore();
		}
		return totalScore;
	}


	private JPanel createActionLogPanel() {
		JPanel actionLogPanel = new JPanel(new BorderLayout());
		actionLogPanel.setBackground(DARK_BACKGROUND);

		JLabel titleLabel = new JLabel("Action Log");
		titleLabel.setForeground(Color.YELLOW);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		actionLogPanel.add(titleLabel, BorderLayout.NORTH);

		actionLogArea = new JTextArea();
		actionLogArea.setEditable(false);
		actionLogArea.setBackground(DARK_BACKGROUND);
		actionLogArea.setForeground(LIGHT_TEXT);
		actionLogArea.setFont(new Font("Arial", Font.PLAIN, 16));

		JScrollPane scrollPane = new JScrollPane(actionLogArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(LIGHT_TEXT));
		actionLogPanel.add(scrollPane, BorderLayout.CENTER);

		return actionLogPanel;
	}
	private JPanel createTeamPanel(String teamName, List<Player> players, Color teamColor) {
		JPanel teamPanel = new JPanel(new BorderLayout());
		teamPanel.setBorder(BorderFactory.createLineBorder(teamColor, 3));
		teamPanel.setBackground(DARK_BACKGROUND);

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(DARK_BACKGROUND);

		JLabel teamLabel = new JLabel(teamName, SwingConstants.CENTER);
		teamLabel.setFont(new Font("Arial", Font.BOLD, 24));
		teamLabel.setForeground(LIGHT_TEXT);
		headerPanel.add(teamLabel, BorderLayout.CENTER);



		teamPanel.add(headerPanel, BorderLayout.NORTH);

		JPanel playerListPanel = new JPanel(new GridLayout(players.size(), 1, 5, 5));
		playerListPanel.setBackground(DARK_BACKGROUND);

		for (Player player : players) {
			JLabel playerLabel = new JLabel(
					"ID: " + player.getId() + " | Codename: " + player.getCodeName() + " | Score: 0");
			playerLabel.setForeground(LIGHT_TEXT);
			playerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
			playerListPanel.add(playerLabel);
		}

		teamPanel.add(playerListPanel, BorderLayout.CENTER);

		if (teamName.equals("Green Team")) {
			greenTeamPlayerListPanel = playerListPanel;
		} else {
			redTeamPlayerListPanel = playerListPanel;
		}

		return teamPanel;
	}

	private void playMusic() {
		System.out.println("Music should be starting");

		File musicFolder = new File("view/music/");
		File[] musicFiles = musicFolder.listFiles((dir, name) -> name.endsWith(".wav"));

		if (musicFiles != null && musicFiles.length > 0) {
			Random random = new Random();
			File randomTrack = musicFiles[random.nextInt(musicFiles.length)];

			try {
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(randomTrack);
				musicClip = AudioSystem.getClip();
				musicClip.open(audioStream);
				musicClip.start();
				musicClip.loop(Clip.LOOP_CONTINUOUSLY);
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}
	private void stopMusic() {
		if (musicClip != null && musicClip.isRunning()) {
			musicClip.stop();
			musicClip.close();
		}
	}
	private void startGameTimer()
	{
		try {
					pss.sendStartSignal();
					System.out.println("Successfully broadcasted signal 202");
				} catch (IOException e){
					System.err.println("Failed to send signal to start game");
				}
		Timer gameTimer = new Timer(1000, e-> {
			if(this.gameTimer > 0){
				this.gameTimer--;
				countdownTimer.setText("Time remaining: " + (this.gameTimer / 60) + " minutes " + (this.gameTimer % 60) + " seconds");
			}
			else {
				((Timer) e.getSource()).stop();
				countdownTimer.setText("Game over!");
				System.out.println("Game ending");
				endGame();
			}
		});
		gameTimer.start();
	}

	private void startCountdownTimer() {
		timer = new Timer(1000, e -> {
			timeRemaining--;
			countdownTimer.setText("Countdown to laser mayhem: " + timeRemaining);
			//System.out.println("Can you see me?");
			//countdownTimer.setHorizontalAlignment(SwingConstants.CENTER);

			if (timeRemaining == 15) {
				playMusic();
			} else if (timeRemaining == 0) {
				countdownTimer.setText("GAME STARTING");
				//countdownTimer.setHorizontalAlignment(SwingConstants.CENTER);
				timer.stop();
				
				Timer gameStartingTimer = new Timer(1000, event -> {
					//countdownTimer.setText("Time remaining: " );
					//countdownTimer.setHorizontalAlignment(SwingConstants.CENTER);
					startGameTimer();
				});//.setRepeats(false).start();
				gameStartingTimer.setRepeats(false);
				gameStartingTimer.start();
			}
		});
		timer.start();
	}

	private void startGame() {
		System.out.print("Game started!");

		//PhotonServerSocket pss = new Photon
		//ServerSocket();
		try {
			pss.sendStartSignal();
			logAction("Game started!");
		}
		catch (IOException e) {
			System.err.println("Error sending start signal: " + e.getMessage());
			e.printStackTrace();
		}

		new Thread(() -> {
			try {
				Random random = new Random();
				int counter = 0;

				while (true) {
					Player greenPlayer = greenTeamPlayers.get(random.nextInt(greenTeamPlayers.size()));
					Player redPlayer = redTeamPlayers.get(random.nextInt(redTeamPlayers.size()));

					String greenEquipmentId = equipmentMap.get(greenPlayer.getId());
					String redEquipmentId = equipmentMap.get(redPlayer.getId());

					if (!isNumeric(greenEquipmentId) || !isNumeric(redEquipmentId)) {
						logAction("Invalid equipment ID detected. Skipping event.");
						continue;
					}

					String message;
					if (random.nextBoolean()) {
						message = redPlayer.getCodeName() + " hit " + greenPlayer.getCodeName() + "!";
						updateScores(redPlayer.getId(), greenPlayer.getId(), false);
					} else {
						message = greenPlayer.getCodeName() + " hit " + redPlayer.getCodeName() + "!";
						updateScores(greenPlayer.getId(), redPlayer.getId(), false);
					}

					if (counter == 10) {
						message = redPlayer.getCodeName() + " hit the base!";
						updateScores(redPlayer.getId(), -1, true);
					}
					if (counter == 20) {
						message = greenPlayer.getCodeName() + " hit the base!";
						updateScores(greenPlayer.getId(), -1, true);
					}

					logAction(message);

					Thread.sleep(random.nextInt(3000) + 1000);

					if (counter >= 30) {
						logAction("Game ended!");
						pss.sendEndSignal();
						pss.sendEndSignal();
						pss.sendEndSignal();
						endGame();
						break;
					}
					counter++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
	private void endGame() {
		greenTeamPlayers.clear();
		redTeamPlayers.clear();

		stopMusic();

		SwingUtilities.invokeLater(() -> {
			PlayerEntryScreen playerEntryScreen = new PlayerEntryScreen(pss);
			playerEntryScreen.display();
		});

		Window currentWindow = SwingUtilities.getWindowAncestor(countdownTimer);
		if (currentWindow != null) {
			currentWindow.dispose();
		}
	}


	private void updateScores(int hitterId, int hitId, boolean isBaseHit) {
		int hitScoreChange = isBaseHit ? 20 : -10;
		int hitterScoreChange = isBaseHit ? 20 : 10;

		playerScores.put(hitterId, playerScores.get(hitterId) + hitterScoreChange);

		if (hitId != -1) {
			playerScores.put(hitId, playerScores.get(hitId) + hitScoreChange);
		}

		updateTeamScores();
		updatePlayerPanels();
	}

	private void updateTeamScores() {
		greenTeamScore = greenTeamPlayers.stream().mapToInt(player -> playerScores.get(player.getId())).sum();
		redTeamScore = redTeamPlayers.stream().mapToInt(player -> playerScores.get(player.getId())).sum();

		if (greenTeamScoreLabel != null) {
			greenTeamScoreLabel.setText("Green Team Score: " + greenTeamScore);
		}
		if (redTeamScoreLabel != null) {
			redTeamScoreLabel.setText("Red Team Score: " + redTeamScore);
		}
	}

	private void updatePlayerPanels() {
		for (int i = 0; i < greenTeamPlayers.size(); i++) {
			Player player = greenTeamPlayers.get(i);
			int score = playerScores.get(player.getId());
			JLabel playerLabel = (JLabel) greenTeamPlayerListPanel.getComponent(i);
			playerLabel.setText("ID: " + player.getId() + " | Codename: " + player.getCodeName() + " | Score: " + score);
		}
		for (int i = 0; i < redTeamPlayers.size(); i++) {
			Player player = redTeamPlayers.get(i);
			int score = playerScores.get(player.getId());
			JLabel playerLabel = (JLabel) redTeamPlayerListPanel.getComponent(i);
			playerLabel.setText("ID: " + player.getId() + " | Codename: " + player.getCodeName() + " | Score: " + score);
		}
	}

	private boolean isNumeric(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void logAction(String action) {
		actionLogArea.append(action + "\n");
		actionLogArea.setCaretPosition(actionLogArea.getDocument().getLength());
	}
}
