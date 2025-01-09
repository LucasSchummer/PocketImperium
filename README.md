# Pocket Imperium

## 📝 Description

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

- Les parties peuvent être sauvegardées à tout moment
- Les sauvegardes sont stockées dans [SavedGames](./SavedGames)

## 👥 Auteurs

UTT - Université de Technologie de Troyes
Romain GOLDENCHTEIN
Lucas SCHUMMER

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.


## 📚 Documentation

La documentation complète du code est disponible en JavaDoc dans le dossier `doc/`.

