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
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
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
	private Map<Integer, Player> playerIdToPlayer;
	private Map<Integer, Integer> playerScores;
	private Map<String, Integer> equipmentIdToPlayerId;

	private JPanel greenTeamPlayerListPanel;
	private JPanel redTeamPlayerListPanel;

	private int greenTeamScore = 0;
	private int redTeamScore = 0;

	JTextPane actionLogArea = new JTextPane();


	private PhotonClient pss;
	private Clip musicClip;

	public PlayActionScreen(List<Player> greenTeamPlayers, List<Player> redTeamPlayers, Map<Integer, String> equipmentMap,
							Map<String, Integer> equipmentIdToPlayerId ,Map<Integer, Player> playerIdToPlayer, PhotonClient pss) {
		this.greenTeamPlayers = greenTeamPlayers;
		this.redTeamPlayers = redTeamPlayers;
		this.equipmentMap = equipmentMap;
		this.playerIdToPlayer = playerIdToPlayer;
		this.pss = pss;
		this.playerScores = new HashMap<>();
		this.equipmentIdToPlayerId = equipmentIdToPlayerId;
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

		int greenScore = scoreTotal(greenTeamPlayers);
		greenTeamScoreLabel = new JLabel("Green team Score: " + greenScore, SwingConstants.CENTER);
		greenTeamScoreLabel.setForeground(Color.GREEN);
		greenTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

		int redScore = scoreTotal(redTeamPlayers);
		redTeamScoreLabel = new JLabel("Red team Score: " + redScore, SwingConstants.CENTER);
		redTeamScoreLabel.setForeground(Color.RED);
		redTeamScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));

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

		actionLogArea = new JTextPane();
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
			if (timeRemaining == 15) {
				playMusic();
			} else if (timeRemaining == 0) {
				countdownTimer.setText("GAME STARTING");
				startGame();
				timer.stop();
				
				Timer gameStartingTimer = new Timer(1000, event -> {
					startGameTimer();
				});
				gameStartingTimer.setRepeats(false);
				gameStartingTimer.start();
			}
		});
		timer.start();
	}

	private void startGame() {

		logAction("Game Starting!");
		try {
			pss.sendStartSignal();
		} catch (IOException e) {
			logAction("Failed to send start signal: " + e.getMessage());
			return;
		}

		new Thread(() -> {
			try {
				String lastProcessedData = null;

				while (true) {
					String receivedData = pss.getReceivedData();

					if (receivedData == null || receivedData.isEmpty() || receivedData.equals(lastProcessedData)) {
						Thread.sleep(100);
						continue;
					}

					lastProcessedData = receivedData;

					if (receivedData.contains(":")) {
						String[] parts = receivedData.split(":");

						if (parts[1].trim().equals("53")) {
							 String transmitterEquipmentId = parts[0].trim();
					int playerTransmitting = equipmentIdToPlayerId.get(transmitterEquipmentId);
					Player baseHitter = playerIdToPlayer.get(playerTransmitting);
        
					// Adjust individual player's score and toggle base hit status
					playerScores.put(playerTransmitting, playerScores.get(playerTransmitting) + 100);
					baseHitter.setBaseHit(true);

					logAction(baseHitter.getCodeName() + " hit Red base and earned 100 points!");
					continue;
							

						} else if (parts[1].trim().equals("43")) {
							 String transmitterEquipmentId = parts[0].trim();
						int playerTransmitting = equipmentIdToPlayerId.get(transmitterEquipmentId);
						Player baseHitter = playerIdToPlayer.get(playerTransmitting);

						// Adjust individual player's score and toggle base hit status
						playerScores.put(playerTransmitting, playerScores.get(playerTransmitting) + 100);
						baseHitter.setBaseHit(true);

						logAction(baseHitter.getCodeName() + " hit Green base and earned 100 points!");
						continue;
							
						}
						if (parts.length == 2) {
							String transmitterEquipmentId = parts[0].trim();
							String hitEquipmentId = parts[1].trim();

							int playerTransmitting = equipmentIdToPlayerId.get(transmitterEquipmentId);
							int playerHit = equipmentIdToPlayerId.get(hitEquipmentId);

							Player player1 = playerIdToPlayer.get(playerTransmitting);
							Player player2 = playerIdToPlayer.get(playerHit);

							String transmitterCodename = player1.getCodeName();
							String hitCodename = player2.getCodeName();

							playerScores.put(playerTransmitting,
									playerScores.getOrDefault(playerTransmitting, 0) + 10);
							playerScores.put(playerHit,
									playerScores.getOrDefault(playerHit, 0) - 10);


							if (isSameTeam(playerTransmitting, playerHit)) {
								logAction("Team tag: " + transmitterCodename +
										  " hit their teammate " + hitCodename);
								pss.sendEquipmentId(Integer.parseInt(transmitterEquipmentId));
							} else {
								logAction(transmitterCodename +
										" hit " + hitCodename);
								pss.sendEquipmentId(Integer.parseInt(hitEquipmentId));
							}
							updateTeamScores();
							updatePlayerPanels();
						} else {
							logAction("Error: Malformed data received: " + receivedData);
						}
					}
				}
			} catch (Exception e) {
				logAction("Error during game logic: " + e.getMessage());
			}
		}).start();
	}

	private boolean isSameTeam(int player1Id, int player2Id) {
		try {

			boolean inGreenTeam = greenTeamPlayers.stream().anyMatch(player -> player.getId() == player1Id) &&
					greenTeamPlayers.stream().anyMatch(player -> player.getId() == player2Id);

			boolean inRedTeam = redTeamPlayers.stream().anyMatch(player -> player.getId() == player1Id) &&
					redTeamPlayers.stream().anyMatch(player -> player.getId() == player2Id);

			return inGreenTeam || inRedTeam;
		} catch (NumberFormatException e) {
			System.err.println("Error: Invalid player ID format. Player1: " + player1Id + ", Player2: " + player2Id);
			return false;
		}
	}



	private void endGame() {
		try {
			pss.sendEndSignal();
			pss.sendEndSignal();
			pss.sendEndSignal();
			logAction("Game ending");
			greenTeamPlayers.clear();
			redTeamPlayers.clear();

			stopMusic();
		
			SwingUtilities.invokeLater(this::showEndGamePopup);
		}
		catch (IOException e) {
			System.err.println("Error sending end signal: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void showEndGamePopup() {
    String winningTeam;
    if (greenTeamScore > redTeamScore) {
        winningTeam = "Green Team wins with a score of " + greenTeamScore + "!";
    } else if (redTeamScore > greenTeamScore) {
        winningTeam = "Red Team wins with a score of " + redTeamScore + "!";
    } else {
        winningTeam = "It's a tie! Both teams scored " + greenTeamScore + "!";
    }

    JFrame popupFrame = new JFrame("Game Over");
    popupFrame.setSize(400, 200);
    popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    popupFrame.setLayout(new BorderLayout());

    JLabel winnerLabel = new JLabel(winningTeam, SwingConstants.CENTER);
    winnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
    winnerLabel.setForeground(Color.BLACK);
    popupFrame.add(winnerLabel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout());
    JButton endProgramButton = new JButton("End Program");
    JButton returnToEntryButton = new JButton("Return to Player Entry");

    endProgramButton.addActionListener(e -> {
        System.exit(0);
    });

    returnToEntryButton.addActionListener(e -> {
        popupFrame.dispose();
        SwingUtilities.invokeLater(() -> {
            new PlayerEntryScreen(pss).display();
        });
    });

    buttonPanel.add(endProgramButton);
    buttonPanel.add(returnToEntryButton);
    popupFrame.add(buttonPanel, BorderLayout.SOUTH);

    popupFrame.setLocationRelativeTo(null);
    popupFrame.setVisible(true);
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
			greenTeamPlayers.sort((p1,p2) -> playerScores.get(p2.getId()) - playerScores.get(p1.getId()));
			Player player = greenTeamPlayers.get(i);
			int score = playerScores.get(player.getId());
			JLabel playerLabel = (JLabel) greenTeamPlayerListPanel.getComponent(i);
			
			String prefix = player.getBaseHit() ? "<span style='color:#FF69B4;font-style:italic;font-size:1.5em;'>B</span> " : "";

			playerLabel.setText(
				"<html>" + 
				prefix + 
				"ID: " + player.getId() + 
				" | Codename: " + player.getCodeName() + 
				" | Score: " + score + 
				"</html>"
			);
			
			if (i == 0)
			{
				nameFlashEffect(playerLabel);
			}
		}
		for (int i = 0; i < redTeamPlayers.size(); i++) {
			redTeamPlayers.sort((p1,p2) -> playerScores.get(p2.getId()) - playerScores.get(p1.getId()));
			Player player = redTeamPlayers.get(i);
			int score = playerScores.get(player.getId());
			JLabel playerLabel = (JLabel) redTeamPlayerListPanel.getComponent(i);
			
			String prefix = player.getBaseHit() ? "<span style='color:#FF69B4;font-style:italic;font-size:1.5em;'>B</span> " : "";


			playerLabel.setText(
				"<html>" + 
				prefix + 
				"ID: " + player.getId() + 
				" | Codename: " + player.getCodeName() + 
				" | Score: " + score + 
				"</html>"
			);
			if (i == 0)
			{
				nameFlashEffect(playerLabel);
			}
		}
	}
	private void nameFlashEffect(JLabel playerLabel) {
		//Color currentColor = playerLabel.getForeground();
		Timer timer = new Timer(200, e -> {
			if (playerLabel.getForeground().equals(LIGHT_TEXT)) {
				playerLabel.setForeground(Color.YELLOW);
			}
			else {
				playerLabel.setForeground(LIGHT_TEXT);
			}
		});
		timer.setRepeats(true);
		timer.start();
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
		StyledDocument doc = actionLogArea.getStyledDocument();

		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontSize(center, 14);
		StyleConstants.setForeground(center, LIGHT_TEXT);

		try {
			doc.insertString(doc.getLength(), action + "\n", null);

			doc.setParagraphAttributes(doc.getLength(), 1, center, false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		actionLogArea.setCaretPosition(actionLogArea.getDocument().getLength());
	}

}
