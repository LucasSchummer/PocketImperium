# Pocket Imperium
<br>

[English](#-description--en) | [Français](#-description--fr) 

<br>

## 📝 Description - EN

Pocket Imperium is a 3X (eXplore, eXpand, eXterminate) space strategy game developed in Java with JavaFX. The game pits 3 players against each other for control of galactic sectors.

## 🎮 Main Features

- Full graphical interface with JavaFX  
- Combat and movement system on a hexagonal grid  
- Three possible types of actions: Explore, Expand, and Exterminate  
- 3 types of bots with different strategies:  
  - Offensive Bot  
  - Defensive Bot  
  - Random Bot  
- Scoring and victory system  
- Save/load game system  
- Music and sound effects  
- Game action log  

## 🛠️ Technologies Used

- Java 21  
- JavaFX 21  
- Maven 3.13+  

## ⚙️ Installation

### Prerequisites

- JDK 21+ installed  
- Maven 3.13+ installed  
- JAVA_HOME environment variable configured  

### Compilation

```bash
mvn clean install
```

### Launch

```bash
mvn javafx:run
```

## 📁 Project Structure

```plaintext
src/
├── main/
│   └── java/
│       └── pimperium/
│           ├── controllers/    # MVC controllers
│           ├── models/         # Data models
│           ├── views/          # JavaFX views
│           ├── players/        # Players logic
│           ├── elements/       # Game elements
│           └── utils/         # Utility classes
```

## 🎯 How to play

1. Start the game
2. Choose the number of human players (0–3)
3. Choose each player's nickname
4. For each bot, select its strategy
5. Each turn:
   - Choose the order of the 3 actions (Explore, Expand, Exterminate)
   - Execute the actions in the chosen order
   - Score points by controlling systems
6. The player with the most points after 9 turns wins

## 🎵 Controls

- Left click to select hexagons and ships
- Buttons to validate actions
- Command interface to choose the order of actions
- Text field to select the number of ships or confirm actions
- Button to pause the music

## 💾 Save System

- Games are saved at the end of each turn
- Games are stored in [SavedGames](./SavedGames)

## 👥 Authors

UTT - University of Technology of Troyes

- Romain GOLDENCHTEIN
- Lucas SCHUMMER

## 📄 License

This project is licensed under the MIT License. See the LICENSE file for more details.


## 📚 Documentation

The complete code documentation is available as JavaDoc in the `doc/` folder.

<br>

---

<br>

## 📝 Description - FR

Pocket Imperium est un jeu de stratégie spatiale 3X (eXplore, eXpand, eXterminate) développé en Java avec JavaFX. Le jeu oppose 3 joueurs qui s'affrontent pour le contrôle de secteurs galactiques.

## 🎮 Caractéristiques principales

- Interface graphique complète avec JavaFX
- Système de combat et de déplacement sur une grille hexagonale 
- Trois types d'actions possibles : Explorer, Étendre et Exterminer
- 3 types de bots avec différentes stratégies :
  - Bot Offensif
  - Bot Défensif
  - Bot Aléatoire
- Système de score et de victoire
- Sauvegarde/chargement de partie
- Musique et effets sonores
- Log des actions de jeu

## 🛠️ Technologies utilisées

- Java 21
- JavaFX 21
- Maven 3.13+

## ⚙️ Installation

### Prérequis

- JDK 21+ installé
- Maven 3.13+ installé
- Variable d'environnement JAVA_HOME configurée

### Compilation

```bash
mvn clean install
```

### Lancement

```bash
mvn javafx:run
```

## 📁 Structure du projet

```plaintext
src/
├── main/
│   └── java/
│       └── pimperium/
│           ├── controllers/    # Contrôleurs MVC
│           ├── models/         # Modèles de données
│           ├── views/          # Vues JavaFX
│           ├── players/        # Logique des joueurs
│           ├── elements/       # Éléments de jeu
│           └── utils/         # Classes utilitaires
```

## 🎯 Comment jouer

1. Lancez le jeu
2. Choisissez le nombre de joueurs humains (0-3)
3. Choissisez le pseudo de chaque joueur
4. Pour chaque bot, choisissez sa stratégie
5. À chaque tour :
   - Choisissez l'ordre des 3 actions (Explorer, Étendre, Exterminer)
   - Exécutez les actions dans l'ordre choisi
   - Marquez des points en contrôlant des systèmes
6. Le joueur avec le plus de points après 9 tours gagne

## 🎵 Contrôles

- Clic gauche pour sélectionner hexagones et vaisseaux
- Boutons pour valider les actions
- Interface de commandes pour choisir l'ordre des actions
- Zone de texte pour choisir le nombre de vaisseaux ou confirmer des actions
- Bouton pour mettre en pause la musique

## 💾 Sauvegarde

- Les parties sont sauvegardées à la fin de chaque tour
- Les sauvegardes sont stockées dans [SavedGames](./SavedGames)

## 👥 Auteurs

UTT - Université de Technologie de Troyes

- Romain GOLDENCHTEIN
- Lucas SCHUMMER

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.


## 📚 Documentation

La documentation complète du code est disponible en JavaDoc dans le dossier `doc/`.

