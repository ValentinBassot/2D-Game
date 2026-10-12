# Reboot

RPG futuriste en vue de dessus, en Java + JavaFX. Projet « 2D Game » (POO), par Paul et Valentin.

À Néo-Lyon, le groupe radical NEXUS a piraté le data center qui relie toutes les IA. Léon affronte les robots détournés, et après chaque victoire, il choisit : **reprogrammer** le robot pour l'ajouter à son équipe, ou l'**achever**. Chaque choix change le monde.

## Documentation

| Document | Contenu | Statut |
| --- | --- | --- |
| [docs/gdd.md](docs/gdd.md) | Game Design Document : règles, robots, combat, monde | Terminé (validation pédago en attente) |
| [Planning (Notion)](https://app.notion.com/p/3e9101f22991802991afdfc6bca8978c) | Tâches semaine par semaine, répartition, priorités, jalons | En cours |
| docs/architecture.md | Architecture, UML, design patterns |  À faire (BMAD) |

## Lancer le projet

Prérequis : un JDK 21 ou plus récent et Maven. JavaFX est téléchargé par Maven, rien d'autre à installer.

```bash
# Ubuntu / WSL
sudo apt install openjdk-21-jdk maven
```

| Commande | Effet |
| --- | --- |
| `mvn javafx:run` | Lance le jeu |
| `mvn verify` | Compile, lance les tests et génère le rapport de couverture dans `target/site/jacoco/index.html` |
| `mvn checkstyle:check` | Vérifie les règles de style (comme la CI) |

Le jeu s'ouvre sur la Home. Léon se déplace avec **ZQSD** ou les **flèches**.

Le portail allumé de la Home mène à la zone 1. Marcher dans l'herbe y déclenche des combats : on choisit une attaque avec **1, 2, 3** (ou les flèches puis **Entrée**).

Sous WSL, la fenêtre s'affiche grâce à WSLg (Windows 11, ou Windows 10 à jour) : aucun serveur X à installer.

## Stack prévue

- Java + JavaFX (interface)
- Maven (build)
- JUnit 5 + JaCoCo (tests et couverture)
- Javadoc (documentation)

## Avancement

- [x] Game Design Document
- [x] Installation de BMAD (Game Dev Studio)
- [x] Planning sur 3 semaines et tâches (Notion)
- [ ] Architecture + diagrammes UML
- [ ] Découpage en epics / stories
- [ ] Développement
- [ ] Tests, Javadoc, soutenance

## Règles d'équipe

1. Avant de coder :   git pull
  Après avoir codé : git add … → git commit → git pull → (régler les conflits) → git push`main`.
2. Chaque pull request est relue par l'autre avant le merge.
3. Pas de code mergé sans ses tests ni sa Javadoc.
4. Chacun doit pouvoir expliquer tout le code à la soutenance.