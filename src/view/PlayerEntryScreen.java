package view;

import model.Player;
import network.PhotonServerSocket;
import database.PlayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.net.*;


public class PlayerEntryScreen {

    private JButton loadPlayersButton;
    private static final int NUM_PLAYERS = 15;
    private static final Color DARK_BACKGROUND = new Color(45, 45, 45);
    private static final Color DARKER_BACKGROUND = new Color(30, 30, 30);
    private static final Color LIGHT_TEXT = new Color(200, 200, 200);

    private JTextField[][] greenTeamFields = new JTextField[NUM_PLAYERS][3];
    private JTextField[][] redTeamFields = new JTextField[NUM_PLAYERS][3];
    private PhotonServerSocket pss;
    private JFrame frame;
    
    public PlayerEntryScreen(PhotonServerSocket pss)
    {
		this.pss = pss;
	}
	
    public void display() {
        JFrame frame = new JFrame("Laser Tag - Photon");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel greenTeamPanel = createTeamPanel("Green Team", Color.GREEN, greenTeamFields);
        JPanel redTeamPanel = createTeamPanel("Red Team", Color.RED, redTeamFields);

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2));
        teamsPanel.add(greenTeamPanel);
        teamsPanel.add(redTeamPanel);
        teamsPanel.setBackground(DARK_BACKGROUND);

        JButton enterNewPlayerButton = new JButton("Enter New Player");
        JButton submitPlayersButton = new JButton("Submit Players");
        loadPlayersButton = new JButton("Load Players");

        enterNewPlayerButton.setBackground(DARKER_BACKGROUND);
        enterNewPlayerButton.setForeground(LIGHT_TEXT);
        enterNewPlayerButton.setFocusPainted(false);
        enterNewPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPlayerForm(frame);
            }
        });

        submitPlayersButton.setBackground(DARKER_BACKGROUND);
        submitPlayersButton.setForeground(LIGHT_TEXT);
        submitPlayersButton.setFocusPainted(false);
        submitPlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitPlayers();
            }
        });

        loadPlayersButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loadPlayersButton.setBackground(DARKER_BACKGROUND);
        loadPlayersButton.setForeground(LIGHT_TEXT);
        loadPlayersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPlayers();
            }
        });


        // Create an instructional label
        JLabel instructionLabel = new JLabel("<F5> to submit players and <F12> to clear players");
        instructionLabel.setForeground(LIGHT_TEXT);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(enterNewPlayerButton);
        buttonPanel.add(submitPlayersButton);
        buttonPanel.add(loadPlayersButton);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(instructionLabel, BorderLayout.SOUTH);
        bottomPanel.setBackground(DARK_BACKGROUND);

        setupKeyBindings(frame.getRootPane());

        frame.add(teamsPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.requestFocusInWindow();
        frame.getContentPane().setBackground(DARK_BACKGROUND);
        frame.setVisible(true);
    }


    private void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("F12"), "clearPlayers");
        inputMap.put(KeyStroke.getKeyStroke("F5"), "submitPlayers");
        actionMap.put("clearPlayers", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearPlayers();
            }
        });
        actionMap.put("submitPlayers", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitPlayers();
            }
        });
    }

    private void loadPlayers() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> players = playerManager.loadPlayers();

        JDialog dialog = new JDialog(frame, "Select Players and Assign Teams", true);
        dialog.setSize(600, 400);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<Player> listModel = new DefaultListModel<>();
	    
        for (Player player : players) {
            listModel.addElement(player);
        }

        JList<Player> playerList = new JList<>(listModel);
        playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Player player = (Player) value;
                label.setText("ID: " + player.getId() + " | Codename: " + player.getCodeName());
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(playerList);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        JLabel hardwareIdLabel = new JLabel("Hardware ID:");
        JTextField hardwareIdField = new JTextField();
        inputPanel.add(hardwareIdLabel);
        inputPanel.add(hardwareIdField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton assignGreenTeamButton = new JButton("Assign to Green Team");
        JButton assignRedTeamButton = new JButton("Assign to Red Team");

        assignGreenTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player selectedPlayer = playerList.getSelectedValue();
                String hardwareId = hardwareIdField.getText();
                if (selectedPlayer != null && !hardwareId.isEmpty()) {
                    assignPlayerToTeam(selectedPlayer, "Green", hardwareId);
                    listModel.removeElement(selectedPlayer);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a player and enter a hardware ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        assignRedTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player selectedPlayer = playerList.getSelectedValue();
                String hardwareId = hardwareIdField.getText();
                if (selectedPlayer != null && !hardwareId.isEmpty()) {
                    assignPlayerToTeam(selectedPlayer, "Red", hardwareId);
                    listModel.removeElement(selectedPlayer);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a player and enter a hardware ID.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(assignGreenTeamButton);
        buttonPanel.add(assignRedTeamButton);

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void assignPlayerToTeam(Player player, String team, String hardwareId) {
        JTextField[][] playerFields = team.equals("Green") ? greenTeamFields : redTeamFields;

        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (playerFields[i][0].getText().isEmpty()) {
                playerFields[i][0].setText(String.valueOf(player.getId()));
                playerFields[i][1].setText(player.getCodeName());
                playerFields[i][2].setText(hardwareId);
                break;
            }
        }
    }

    public void clearPlayers() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            for (int j = 0; j < 3; j++) {
                greenTeamFields[i][j].setText("");
                redTeamFields[i][j].setText("");
            }
        }
        JOptionPane.showMessageDialog(frame, "All players have been cleared!", "Info", JOptionPane.INFORMATION_MESSAGE);
	    //PhotonServerSocket.RemoveBaseHitter(); //reset toggle for base hitter to allow a new one
    }


    private JPanel createTeamPanel(String teamName, Color teamColor, JTextField[][] playerFields) {
        JPanel teamPanel = new JPanel(new BorderLayout());
        teamPanel.setBorder(BorderFactory.createLineBorder(teamColor, 3));
        teamPanel.setBackground(DARK_BACKGROUND);

        JLabel teamLabel = new JLabel(teamName, SwingConstants.CENTER);
        teamLabel.setFont(new Font("Arial", Font.BOLD, 24));
        teamLabel.setOpaque(true);
        teamLabel.setBackground(teamColor);
        teamLabel.setForeground(Color.WHITE);
        teamPanel.add(teamLabel, BorderLayout.NORTH);

        JPanel playerInputPanel = new JPanel(new GridLayout(NUM_PLAYERS, 1, 5, 5));
        playerInputPanel.setBackground(DARK_BACKGROUND);

        for (int i = 0; i < NUM_PLAYERS; i++) {
            JPanel row = new JPanel(new GridLayout(1, 3, 5, 5));
            row.setBackground(DARK_BACKGROUND);

            JTextField playerIdField = createInputField("Player ID", true);
            JTextField codeNameField = createInputField("Code Name", false);
            JTextField equipmentIdField = createInputField("Equipment ID", true);

            row.add(playerIdField);
            row.add(codeNameField);
            row.add(equipmentIdField);

            playerFields[i][0] = playerIdField;
            playerFields[i][1] = codeNameField;
            playerFields[i][2] = equipmentIdField;

            playerInputPanel.add(row);
        }

        teamPanel.add(playerInputPanel, BorderLayout.CENTER);

        return teamPanel;
    }

    private JTextField createInputField(String title, boolean isNumeric) {
        JTextField textField = new JTextField();
        textField.setBackground(DARKER_BACKGROUND);
        textField.setForeground(LIGHT_TEXT);
        textField.setCaretColor(LIGHT_TEXT);
        textField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(LIGHT_TEXT, 1), title, 0, 0, null, LIGHT_TEXT));

        if (isNumeric) {
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != '\b') {
                        e.consume();
                    }
                }
            });
        }

        return textField;
    }

    private void openPlayerForm(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "New Player Form", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 2));

        JLabel playerIdLabel = new JLabel("Player ID:");
        JTextField playerIdField = new JTextField();
        playerIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume();
                }
            }
        });

        JLabel codeNameLabel = new JLabel("Code Name:");
        JTextField codeNameField = new JTextField();

        JLabel equipmentIdLabel = new JLabel("Equipment ID:");
        JTextField equipmentIdField = new JTextField();
        equipmentIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '\b') {
                    e.consume();
                }
            }
        });

        JLabel teamLabel = new JLabel("Team:");
        String[] teams = {"Green", "Red"};
        JComboBox<String> teamSelector = new JComboBox<>(teams);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerId = playerIdField.getText();
                String codeName = codeNameField.getText();
                String equipmentId = equipmentIdField.getText();
                String team = (String) teamSelector.getSelectedItem();

                if (!playerId.isEmpty() && !codeName.isEmpty()) {
                    System.out.println("Equipment ID for UDP: " + equipmentId);
                    updatePlayerEntry(playerId, codeName, equipmentId, team);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dialog.add(playerIdLabel);
        dialog.add(playerIdField);
        dialog.add(codeNameLabel);
        dialog.add(codeNameField);
        dialog.add(equipmentIdLabel);
        dialog.add(equipmentIdField);
        dialog.add(teamLabel);
        dialog.add(teamSelector);
        dialog.add(new JLabel());
        dialog.add(submitButton);

        dialog.setVisible(true);
    }

    private void updatePlayerEntry(String playerId, String codeName, String equipmentId, String team) {
        JTextField[][] playerFields = team.equals("Green") ? greenTeamFields : redTeamFields;

	    //Adjust the font size so nothing get's cut off. Declare a font!
        Font customFont = new Font("Arial", Font.PLAIN, 9);

       	for (int i = 0; i < NUM_PLAYERS; i++) {
            if (playerFields[i][0].getText().isEmpty()) {
                playerFields[i][0].setText(playerId);
                playerFields[i][1].setText(codeName);
                playerFields[i][2].setText(equipmentId);
               
		//Added to set font sizes
		playerFields[i][0].setFont(customFont);
		playerFields[i][1].setFont(customFont);
		playerFields[i][2].setFont(customFont);		
		break;
            }
        }
    }
    private void submitPlayers() {
        PlayerManager playerManager = new PlayerManager();
        List<Player> greenTeamPlayers = new ArrayList<>();
        List<Player> redTeamPlayers = new ArrayList<>();
        Map<Integer, String> equipmentMap = new HashMap<>(); // Map to store equipment IDs

        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (!greenTeamFields[i][0].getText().isEmpty()) {
                int id = Integer.parseInt(greenTeamFields[i][0].getText());
                String codename = greenTeamFields[i][1].getText();
                String equipmentId = greenTeamFields[i][2].getText();
                Player greenPlayer = new Player(id, codename);
                greenTeamPlayers.add(greenPlayer);
                equipmentMap.put(id, equipmentId); // Add equipment ID to the map
                playerManager.insertPlayer(greenPlayer);
                
                //Let the UDP know what the hardware codes are
                assignCodeAndSendUDP(equipmentId);
            }
            if (!redTeamFields[i][0].getText().isEmpty()) {
                int id = Integer.parseInt(redTeamFields[i][0].getText());
                String codename = redTeamFields[i][1].getText();
                String equipmentId = redTeamFields[i][2].getText();
                Player redPlayer = new Player(id, codename);
                redTeamPlayers.add(redPlayer);
                equipmentMap.put(id, equipmentId); // Add equipment ID to the map
                playerManager.insertPlayer(redPlayer);
                
                //Let the UDP know what the hardware codes are
                assignCodeAndSendUDP(equipmentId);
            }
        }
        JDialog dialog = new JDialog(frame, "ERROR SUBMITTING PLAYERS", true);

        if (greenTeamPlayers.isEmpty() && redTeamPlayers.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Players empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            PlayActionScreen playActionScreen = new PlayActionScreen(greenTeamPlayers, redTeamPlayers, equipmentMap, pss);
            playActionScreen.display();
        }
    }
    // Method to assign the code and send the equipment ID via UDP
private void assignCodeAndSendUDP(String equipmentId) {
    // Call assignCode to handle any internal logic you want to do with the equipmentId (e.g., storing it)
    int equipmentCode = Integer.parseInt(equipmentId);
    
    // Now send the equipment ID over UDP
    sendHardwareIdToUDP(equipmentId, "127.0.0.1", 7501); // Example IP and port
}

// Dummy sendHardwareIdToUDP method (use the actual UDP method you have)
private void sendHardwareIdToUDP(String equipmentId, String udpIp, int udpPort) {
    try {
        DatagramSocket socket = new DatagramSocket();
        byte[] message = equipmentId.getBytes();
        DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName(udpIp), udpPort);
        socket.send(packet);
        socket.close();
        System.out.println("Sent equipment ID " + equipmentId + " to UDP at " + udpIp + ":" + udpPort + " supposedly");
    } catch (Exception e) {
        System.out.println("Error sending UDP packet: " + e.getMessage());
    }
}
}
