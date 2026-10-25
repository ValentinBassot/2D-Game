# Reboot

![Couverture](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg)

RPG futuriste en vue de dessus, en Java + JavaFX. Projet « 2D Game » (POO), par Paul et Valentin.

À Néo-Lyon, le groupe radical NEXUS a piraté le data center qui relie toutes les IA. Léon affronte les robots détournés, et après chaque victoire, il choisit : **reprogrammer** le robot pour l'ajouter à son équipe, ou l'**achever**. Chaque choix change le monde.

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
| `mvn javadoc:javadoc` | Génère la Javadoc dans `target/reports/apidocs/index.html` |

Sous WSL, la fenêtre s'affiche grâce à WSLg (Windows 11, ou Windows 10 à jour) : aucun serveur X à installer.

## Commandes

| Touche | Action |
| --- | --- |
| **ZQSD** ou **flèches** | Se déplacer, naviguer dans les menus |
| **Entrée** ou **Espace** | Valider |
| **1, 2, 3, 4** | Choisir une compétence en combat |
| **Échap** | Pause, ou retour dans un menu |
| **F11** | Plein écran |

Les touches se changent dans **Options**, depuis le menu principal ou la pause. On y règle aussi la taille de la fenêtre, le plein écran et la luminosité. Tout est enregistré dans `~/.reboot/settings.properties` et appliqué tout de suite.

## Le jeu

La partie commence à la Home de Léon. Son portail mène au Quartier résidentiel, puis à la Zone industrielle, puis au Data center où attend le chef de NEXUS.

- **Combat au tour par tour.** Chaque robot a ses compétences (dégâts, soin, bouclier, coup prioritaire) et un type qui prend l'avantage sur un autre (x 1,5 de dégâts). On peut changer de robot en plein combat, mais ça coûte le tour.
- **Reprogrammer ou achever.** Après une victoire, le robot vaincu glitch et laisse échapper un souvenir. Le reprogrammer l'ajoute à l'équipe et fait baisser la barre de recherche. L'achever rapporte des pièces de réparation, mais NEXUS apprend de chaque robot détruit et ses robots deviennent plus forts.
- **Barre de recherche.** Elle monte quand on achève des robots ou qu'on neutralise un pilote. Au-delà de 75, le Data center est mieux gardé : 3 gardes au lieu d'un.
- **Équipe et Home.** L'équipe compte 3 robots au plus, les autres attendent à la Home. Un robot K.O. part en réparation pour quelques victoires, et on peut dépenser des pièces au poste de la Home pour l'accélérer. Si toute l'équipe est en réparation, c'est perdu.
- **XP et niveaux.** Le robot qui gagne un combat prend de l'XP. Les ennemis montent de niveau avec la zone et avec le réseau NEXUS.
- **Quêtes.** Chaque zone se débloque avec un chapitre de quêtes : 8 victoires dans la zone, une équipe complète, un niveau d'équipe à atteindre (11 puis 20) et le chef de zone battu. L'onglet **Quêtes** du menu pause montre ce qu'il reste à faire.

Les seuils sont calculés pour qu'il faille mélanger les deux choix. Si on achève tout, l'équipe reste trop petite. Si on reprogramme tout, il faut environ 31 victoires au lieu de 12 pour atteindre le niveau d'équipe demandé.

On gagne en battant le chef de NEXUS.

## Architecture

Le modèle (`com.reboot.model`) ne dépend pas de JavaFX : il se teste sans interface. Les vues (`com.reboot.view`) dessinent sur un canvas, et `GameController` fait le lien entre les deux.

| Paquet | Contenu |
| --- | --- |
| `model.fighter` | `Combattant` (abstraite), les robots, l'exosquelette, le boss, les états d'un robot |
| `model.skill` | Les compétences et le calcul des dégâts |
| `model.combat` | Le déroulé d'un combat et des rencontres |
| `model.team` | L'équipe, la Home et le stock de pièces |
| `model.event` | Le bus d'événements et ses événements |
| `model.nexus` | La barre de recherche et le réseau du data center |
| `model.quest` | Les quêtes, leurs chapitres et la campagne |
| `model.game` | La partie et le choix reprogrammer ou achever |
| `model.world` | La carte, les tuiles et le joueur |
| `model.settings` | Les touches et les options |
| `model.story` | Les souvenirs des robots |

Design patterns utilisés :

- **State** : un robot est détourné, allié ou en réparation (`RobotState`), et son comportement change avec son état.
- **Observer** : achever ou reprogrammer publie un événement sur l'`EventBus`, et la barre de recherche, le réseau et le stock de pièces réagissent chacun de leur côté.
- **Factory** : `RobotFactory` crée les ennemis selon la zone et le niveau du réseau.
- **Strategy** : l'IA ennemie est une `CombatStrategy` (`RandomStrategy` en jeu, `FirstSkillStrategy` par défaut), et `Combat` lui demande sa compétence sans savoir comment elle choisit.
- **Decorator** : `OncePerCombatSkill` enveloppe une compétence pour qu'elle ne serve qu'une fois par combat, comme la Surcharge des exosquelettes.
- **Composite** : un chapitre (`QuestGroup`) est lui-même une `Quest` qui regroupe d'autres quêtes.
- **Template Method** : `MenuView` gère la navigation, et chaque menu ne fournit que ses entrées.

Le polymorphisme passe par des méthodes abstraites redéfinies dans chaque combattant (`getSpriteId`, `isReprogrammable`) et par `Skill.apply`, sans aucun `instanceof` dans le code du jeu.

## Documentation

| Document | Contenu |
| --- | --- |
| [docs/gdd.md](docs/gdd.md) | Game Design Document : règles, robots, combat, monde |
| [docs/uml.md](docs/uml.md) | Diagrammes UML (Mermaid) |
| [Planning (Notion)](https://app.notion.com/p/3e9101f22991802991afdfc6bca8978c) | Tâches semaine par semaine, répartition, priorités, jalons |
| Javadoc | `mvn javadoc:javadoc`, puis ouvrir `target/reports/apidocs/index.html` |

## Tests et couverture

Les tests JUnit 5 couvrent le modèle du jeu (combat, robots, quêtes, équipe...). La couverture est mesurée par JaCoCo pendant `mvn verify`.

Les badges en haut de cette page sont mis à jour par la CI à chaque push sur `dev` (workflow `Coverage`) : elle relance les tests et régénère les badges à partir du rapport JaCoCo. Le résumé de chaque exécution est visible dans l'onglet Actions, et le rapport détaillé par paquet est publié comme artefact par la workflow de tests.

La mesure porte sur le code testable : tout le paquet `model`, plus `Camera` et `DiscoveredArea`. Les écrans JavaFX (`*View`, `Hud`, `Minimap`, `SpriteStore`, `ScaledScreen`), `App` et `GameController` sont exclus dans le `pom.xml`. Ils ne font que dessiner sur un canvas et brancher les vues, on les vérifie en jouant. C'est pour ça qu'on a gardé le modèle sans aucune dépendance à JavaFX.

`mvn verify` échoue si la couverture des instructions ou des lignes passe sous **85 %** (règle `jacoco:check`), donc la CI aussi.

## Stack

- Java 21 + JavaFX 21 (interface)
- Maven (build)
- JUnit 5 + JaCoCo (tests et couverture)
- Checkstyle (style) et Javadoc (documentation)
- GitHub Actions (tests, lint, couverture, sécurité)

## Avancement

- [x] Game Design Document
- [x] Planning sur 3 semaines et tâches (Notion)
- [x] Diagrammes UML
- [x] Combat, équipe, reprogrammer ou achever
- [x] Barre de recherche, réseau NEXUS, Home
- [x] XP, quêtes et déblocage des zones
- [x] Menus, options et HUD
- [x] Tests (85 % minimum) et Javadoc
- [ ] IA ennemie plus fine
- [ ] Soutenance
