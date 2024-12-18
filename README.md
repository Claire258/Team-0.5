# Team 1 Laser Tag Program - UARK

## Team Members
| GitHub Username                  | Real Name                | Trello Username   |
|----------------------------------|-------------------------|-------------------|
| Claire258                        | Claire Oliver           | claireoliver22    |
| theonlyduckleftinmars            | Jose Eduardo Hernandez  | edjh22            |

## Table of Contents
1. [How to Run the Application](#how-to-run-the-application)
2. [Install Git LFS](#install-git-lfs)
3. [Game Mechanics](#game-mechanics)
4. [Trouble Shooting](#trouble-shooting)

## How to Run the Application

1. Install SDKMAN and Java 22:
   ```bash
   curl -s "https://get.sdkman.io" | bash
   sdk install java 22.0.2-amzn

2. Please open a new terminal, or run the following in the existing one, use your username for local username (obviously):

   ```bash
    source "/Users/<local username>/.sdkman/bin/sdkman-init.sh"

2. If java 22 is not available, you can also check the available versions with:

   ```bash
   sdk list java

3. Make sure you set the default java version and verify:

   ```bash
   sdk default java 22.0.2-amzn
   javac -version
   
4. After cloning the repository, from the `team-1` directory, make the `run.bash` executable:
   ```bash
   chmod +x run.bash
5. Start the program by typing:
   ```bash
   ./run.bash

## Install Git LFS

SKIP: No more need for Git LFS, since files come compressed already***

1. Install Git LFS:
   For macOS:
   ```bash
   brew install git-lfs
   ```
   For Linux:
   ```bash
   sudo apt install git-lfs
2. Initialize Git LFS: In your repository (after cloning), run:
   ```bash
   git lfs install
3. Fetch the actual files. (fetches .wav file):
   ```bash
   git lfs pull

# Game Mechanics

This section further clarifies player entry screen mechanics, including how to add and load players, assign them to teams, and submit the player list to start the game.

---

### Adding a New Player
1. To add a new player, select the **"Enter New Player"** button at the bottom left of the screen.
2. Enter the following details for the new player in the provided text fields:
  - **User ID**
  - **Code Name**
  - **Hardware ID**
3. Assign the player to either the **Red Team** or **Green Team** using the team selection buttons.

---

### Loading an Existing Player
1. To load an existing player from the database, select the **"Load Player"** button at the bottom right of the screen.
2. Choose the player from the drop-down list.
3. Enter the player's **Hardware ID**.
4. Assign the loaded player to either the **Red Team** or **Green Team**.

---

### Clearing All Players
- To clear all players from the screen, press the **F12** key.

---

### Submitting Players and Starting the Game
- Once all players have been added or loaded and assigned to teams, select one of the following options to submit the list and start the game:
  - Click the **"Submit Player"** button at the bottom of the screen, or
  - Press the **F5** key.

---

### End of Game
- At the end of the game, a pop-up window will appear showing the team with the highest score.
- You can choose one of the following actions:
  - **End the program**, or
  - **Return to the player entry screen** to start a new game.

---
# Trouble Shooting
- If the program does not compile, stating that **'void <init>()' not found** or errors relating to **missing class loader files**, please delete all class files currently in the program. After deleting files, navigate to the src folder in the terminal within the cloned repository.
  ```bash
  cd
  cd Team-0.5/src
- Compile Main.java
  ```bash
  javac Main.java
- Navigate back to main folder of the program directory
  ```bash
  cd ..
- Recompile the program with updated class files
  ```bash
  ./run.bash
