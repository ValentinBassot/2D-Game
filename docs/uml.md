# Diagrammes UML de Reboot

Première version, tirée de la section « Architecture visée » du [GDD](gdd.md). Rien n'est encore codé : ces diagrammes décrivent le modèle à construire, pas un existant. Les noms de classes repris du GDD sont gardés tels quels ; ceux ajoutés ici sont des propositions, listées dans [Points à trancher](#points-à-trancher).

Les diagrammes sont écrits en Mermaid : GitHub les affiche directement, et ils se modifient comme du texte.

| # | Diagramme | Ce qu'il montre | Design pattern |
| --- | --- | --- | --- |
| 1 | [Combattants et compétences](#1-combattants-et-compétences) | Héritage des robots, compétences | Héritage, polymorphisme |
| 2 | [États d'un robot](#2-états-dun-robot) | Détourné, Allié, En réparation | State |
| 3 | [Combat](#3-combat) | Tour par tour, IA ennemie, dégâts | Strategy |
| 4 | [Conséquences du choix](#4-conséquences-du-choix) | Réseau, barre de recherche, pièces | Observer |
| 5 | [Joueur et monde](#5-joueur-et-monde) | Équipe, Home, zones, rencontres | Composition, Factory |
| 6 | [Séquence : un tour de combat](#6-séquence--un-tour-de-combat) | Qui appelle qui pendant un tour | Aucun |
| 7 | [Séquence : reprogrammer ou achever](#7-séquence--reprogrammer-ou-achever) | La scène phare du POC | State + Observer |
| 8 | [Enchaînement des écrans](#8-enchaînement-des-écrans) | La boucle de jeu | Aucun |

## 1. Combattants et compétences

`Combattant` porte ce qui est commun à tout ce qui se bat : stats, PV, compétences. Les stats se calculent à partir des stats de base et du niveau (+ 10 % de la base par niveau).

Deux branches en héritent. Les `Robot` ont un état et peuvent être reprogrammés. Les `Exosquelette` sont pilotés par des humains : `canBeReprogrammed()` renvoie `false`. Le chef de NEXUS est un `Boss`, un exosquelette particulier qui redéfinit `getLevel()` pour renvoyer son niveau fixe : c'est l'exemple de polymorphisme du GDD.

Chaque compétence est un objet `Skill` : en ajouter une ne demande de toucher à aucune classe de robot.

```mermaid
classDiagram
    direction TB

    class Combattant {
        <<abstract>>
        #String name
        #int level
        #int hp
        #BaseStats baseStats
        +getLevel() int
        +getMaxHp() int
        +getAtk() int
        +getDef() int
        +getSpeed() int
        +getSkills() List~Skill~
        +takeDamage(int amount) void
        +heal(int amount) void
        +isKo() boolean
        +canBeReprogrammed() boolean*
    }

    class BaseStats {
        <<record>>
        +int hp
        +int atk
        +int def
        +int speed
    }

    class Robot {
        <<abstract>>
        -RobotState state
        -int xp
        +getType() RobotType*
        +reprogram() void
        +knockOut() void
        +gainXp(int amount) void
        +canFight() boolean
        +canBeReprogrammed() boolean
    }

    class TankBot
    class HealerBot
    class InfighterBot

    class Exosquelette {
        -boolean overloadUsed
        +canBeReprogrammed() boolean
    }

    class Boss {
        -int fixedLevel
        +getLevel() int
    }

    class RobotType {
        <<enumeration>>
        TANK
        HEALER
        INFIGHTER
        +beats(RobotType other) boolean
    }

    class Skill {
        <<interface>>
        +getName() String
        +hasPriority() boolean
        +apply(Combat combat, Combattant user, Combattant target) void
    }

    class DamageSkill {
        -int power
        -boolean ignoresDefense
        -boolean priority
    }

    class TeamHealSkill {
        -int amount
    }

    class DefenseBoostSkill {
        -int multiplier
        -int turns
    }

    Combattant <|-- Robot
    Combattant <|-- Exosquelette
    Robot <|-- TankBot
    Robot <|-- HealerBot
    Robot <|-- InfighterBot
    Exosquelette <|-- Boss
    Combattant *-- BaseStats
    Combattant o-- "1..3" Skill
    Robot --> RobotType
    Skill <|.. DamageSkill
    Skill <|.. TeamHealSkill
    Skill <|.. DefenseBoostSkill
```

Correspondance avec les compétences du GDD :

| Compétence | Classe | Réglage |
| --- | --- | --- |
| Frappe, Écrasement, Rafale de coups | `DamageSkill` | puissance 10, 16, 16 |
| Nanobots corrosifs | `DamageSkill` | puissance 12, `ignoresDefense` |
| Crochet | `DamageSkill` | puissance 12, `priority` |
| Surcharge (exosquelettes) | `DamageSkill` | puissance 18, une fois par combat via `overloadUsed` |
| Patch réseau | `TeamHealSkill` | 15 PV à toute l'équipe active |
| Blindage | `DefenseBoostSkill` | DEF × 2 pendant 2 tours |

## 2. États d'un robot

Un ennemi et un allié sont la même classe `Robot` : seul son état change. `Robot` délègue à son `RobotState` tout ce qui dépend de l'état, au lieu d'empiler des `if`.

```mermaid
classDiagram
    direction LR

    class Robot {
        <<abstract>>
        -RobotState state
        +reprogram() void
        +knockOut() void
        +canFight() boolean
        ~setState(RobotState next) void
    }

    class RobotState {
        <<interface>>
        +isHostile() boolean
        +canFight() boolean
        +onReprogram(Robot robot) void
        +onKnockOut(Robot robot) void
        +onBattleWon(Robot robot) void
    }

    class HijackedState
    class AllyState

    class RepairingState {
        -int victoriesLeft
        +usePart(Robot robot) void
    }

    Robot o-- "1" RobotState
    RobotState <|.. HijackedState
    RobotState <|.. AllyState
    RobotState <|.. RepairingState
```

Les transitions entre états :

```mermaid
stateDiagram-v2
    direction LR

    state "Détourné" as Hijacked
    state "Allié" as Ally
    state "En réparation" as Repairing

    [*] --> Hijacked : rencontre créée par RobotFactory
    [*] --> Ally : robot de départ de Léon
    Hijacked --> Ally : reprogrammer
    Hijacked --> [*] : achever
    Ally --> Repairing : K.O. en combat
    Repairing --> Repairing : victoire ou pièce dépensée (délai − 1)
    Repairing --> Ally : délai à 0 (3 victoires au départ)
```

## 3. Combat

`Combat` ne connaît ni JavaFX ni la carte : il se teste seul. L'IA ennemie est une `CombatStrategy` interchangeable ; `RandomStrategy` reçoit son générateur aléatoire par le constructeur, ce qui rend les tests reproductibles. `CombatLauncher` est la frontière entre l'exploration et le combat.

```mermaid
classDiagram
    direction TB

    class CombatLauncher {
        <<interface>>
        +launch(Combattant enemy) void
    }

    class Combat {
        -Team playerTeam
        -Combattant enemy
        -CombatStrategy enemyStrategy
        -int turn
        +useSkill(Skill skill) TurnResult
        +switchRobot(Robot next) TurnResult
        +isOver() boolean
        +getOutcome() CombatOutcome
    }

    class CombatStrategy {
        <<interface>>
        +chooseSkill(Combattant self, Combattant opponent) Skill
    }

    class RandomStrategy {
        -Random random
    }

    class OffensiveStrategy {
        -WantedBar wantedBar
    }

    class DamageCalculator {
        +compute(Combattant attacker, Combattant defender, int power, boolean ignoresDefense) int
    }

    class TurnResult {
        <<record>>
        +List~String~ log
        +boolean combatOver
    }

    class CombatOutcome {
        <<enumeration>>
        VICTORY
        DEFEAT
    }

    CombatLauncher ..> Combat : crée
    Combat --> "1" Team : équipe du joueur
    Combat --> "1" Combattant : ennemi
    Combat o-- "1" CombatStrategy
    Combat ..> DamageCalculator
    Combat ..> TurnResult
    Combat ..> CombatOutcome
    CombatStrategy <|.. RandomStrategy
    CombatStrategy <|.. OffensiveStrategy
```

`DamageCalculator` applique la formule du GDD : `max(1, puissance + ATK − DEF)`, puis × 1,5 arrondi à l'entier inférieur si le type de l'attaquant bat celui du défenseur. `OffensiveStrategy` est un bonus (COULD).

## 4. Conséquences du choix

Reprogrammer, achever ou neutraliser un pilote publie un événement : la barre de recherche bouge dans les trois cas, donc il y a trois événements. Ceux qui en dépendent s'abonnent : le code du combat et de l'écran de choix ne connaît ni le réseau, ni la barre de recherche, ni le stock de pièces.

```mermaid
classDiagram
    direction TB

    class GameEvent {
        <<interface>>
    }

    class RobotKilledEvent {
        <<record>>
        +Robot robot
    }

    class RobotReprogrammedEvent {
        <<record>>
        +Robot robot
    }

    class PilotNeutralizedEvent {
        <<record>>
        +Exosquelette exosquelette
    }

    class GameEventListener {
        <<interface>>
        +onEvent(GameEvent event) void
    }

    class EventBus {
        -List~GameEventListener~ listeners
        +subscribe(GameEventListener listener) void
        +publish(GameEvent event) void
    }

    class DataCenter {
        -int robotsKilled
        +getNetworkBonus() int
    }

    class WantedBar {
        -int value
        +getValue() int
        +getTier() WantedTier
    }

    class WantedTier {
        <<enumeration>>
        UNKNOWN
        SPOTTED
        HUNTED
        PUBLIC_ENEMY
    }

    class PartsStock {
        -int parts
        +getParts() int
        +spend(int amount) boolean
    }

    GameEvent <|.. RobotKilledEvent
    GameEvent <|.. RobotReprogrammedEvent
    GameEvent <|.. PilotNeutralizedEvent
    EventBus o-- "*" GameEventListener
    EventBus ..> GameEvent : publie
    GameEventListener <|.. DataCenter
    GameEventListener <|.. WantedBar
    GameEventListener <|.. PartsStock
    WantedBar --> WantedTier
```

| Événement | `DataCenter` | `WantedBar` | `PartsStock` |
| --- | --- | --- | --- |
| `RobotKilledEvent` | robots achevés + 1 | + 15 | + (1 + niveau / 3) pièces |
| `RobotReprogrammedEvent` | Aucun effet | − 5 | Aucun effet |
| `PilotNeutralizedEvent` | Aucun effet | + 10 | Aucun effet |

Le bonus du réseau vaut `min(5, robotsAchevés / 3)`, et la barre reste entre 0 et 100.

## 5. Joueur et monde

`Player` possède sa `Team` (3 robots au plus) ; la `Home` stocke les autres et répare les K.O. `RobotFactory` crée les rencontres d'une zone, au niveau `zone + bonus du réseau`.

```mermaid
classDiagram
    direction TB

    class Game {
        -Zone currentZone
        +enterZone(Zone zone) void
        +loadDemoSave() void
    }

    class Player {
        -int x
        -int y
        +move(Direction direction) void
    }

    class Team {
        +MAX_SIZE int$
        -List~Robot~ robots
        -Robot active
        +add(Robot robot) boolean
        +remove(Robot robot) void
        +isFull() boolean
        +hasFighterLeft() boolean
        +healAll() void
    }

    class Home {
        -List~Robot~ stored
        +store(Robot robot) void
        +swap(Robot fromTeam, Robot fromHome) void
        +healTeam(Team team) void
    }

    class Zone {
        -String name
        -int level
        -List~RobotType~ dominantTypes
        -boolean unlocked
        +unlock() void
    }

    class TileMap {
        -Tile[][] tiles
        +load(String path) TileMap$
        +tileAt(int x, int y) Tile
        +isWalkable(int x, int y) boolean
    }

    class Tile {
        <<enumeration>>
        GROUND
        WALL
        ENCOUNTER
        STATION
        PORTAL
    }

    class RobotFactory {
        -Random random
        +createEncounter(Zone zone, DataCenter dataCenter) Robot
        +createZoneBoss(Zone zone) Exosquelette
        +createStarter() Robot
    }

    Game *-- "1" Player
    Game *-- "1" Home
    Game *-- "4" Zone : Home + 3 zones
    Game *-- "1" DataCenter
    Game *-- "1" WantedBar
    Game *-- "1" PartsStock
    Game *-- "1" EventBus
    Player *-- "1" Team
    Team o-- "0..3" Robot
    Home o-- "*" Robot
    Zone *-- "1" TileMap
    TileMap --> Tile
    Zone ..> RobotFactory : rencontres
    RobotFactory ..> Robot : crée
    RobotFactory ..> Exosquelette : crée
    RobotFactory ..> DataCenter : lit le bonus
```

## 6. Séquence : un tour de combat

Le joueur choisit une compétence. Le plus rapide joue en premier, sauf si une compétence est prioritaire (Crochet).

```mermaid
sequenceDiagram
    actor Joueur
    participant Vue as CombatView (JavaFX)
    participant Combat
    participant IA as CombatStrategy
    participant Calc as DamageCalculator
    participant Allie as Robot actif
    participant Ennemi as Combattant ennemi

    Joueur->>Vue: touche 1, 2 ou 3
    Vue->>Combat: useSkill(skill)
    Combat->>IA: chooseSkill(ennemi, robot actif)
    IA-->>Combat: compétence ennemie
    Note over Combat: ordre du tour : priorité, puis VIT

    Combat->>Calc: compute(robot actif, ennemi, puissance)
    Calc-->>Combat: dégâts
    Combat->>Ennemi: takeDamage(dégâts)

    alt l'ennemi est encore debout
        Combat->>Calc: compute(ennemi, robot actif, puissance)
        Calc-->>Combat: dégâts
        Combat->>Allie: takeDamage(dégâts)
        opt le robot actif est K.O.
            Combat->>Allie: knockOut()
            Note over Allie: passe en RepairingState
        end
    end

    Combat-->>Vue: TurnResult (journal, combat fini ?)
    Vue-->>Joueur: PV et journal mis à jour
```

## 7. Séquence : reprogrammer ou achever

La scène phare : après une victoire contre un robot, le joueur choisit. Les deux branches montrent le State (le robot change d'état) et l'Observer (les conséquences se propagent seules).

```mermaid
sequenceDiagram
    actor Joueur
    participant Vue as ChoiceView (JavaFX)
    participant Choix as ChoiceResolver
    participant Robot as Robot vaincu
    participant Equipe as Team
    participant Home
    participant Bus as EventBus
    participant DC as DataCenter
    participant Barre as WantedBar
    participant Pieces as PartsStock

    Vue-->>Joueur: souvenir du robot, glitch rouge et bleu

    alt Reprogrammer
        Joueur->>Vue: bouton Reprogrammer
        Vue->>Choix: reprogram(robot)
        Choix->>Robot: reprogram()
        Note over Robot: HijackedState devient AllyState
        alt l'équipe a une place
            Choix->>Equipe: add(robot)
        else l'équipe est pleine
            Choix->>Home: store(robot)
        end
        Choix->>Bus: publish(RobotReprogrammedEvent)
        Bus->>Barre: onEvent, barre − 5
    else Achever
        Joueur->>Vue: bouton Achever
        Vue->>Choix: finish(robot)
        Choix->>Bus: publish(RobotKilledEvent)
        Bus->>DC: onEvent, robots achevés + 1
        Bus->>Barre: onEvent, barre + 15
        Bus->>Pieces: onEvent, + (1 + niveau / 3) pièces
    end

    Choix-->>Vue: conséquences à afficher
    Vue-->>Joueur: retour à l'exploration
```

## 8. Enchaînement des écrans

La boucle de jeu du GDD, vue comme une suite d'écrans.

```mermaid
stateDiagram-v2
    state "Menu principal" as Menu
    state "Exploration (Home)" as HomeScreen
    state "Équipe" as TeamScreen
    state "Exploration (zone)" as ZoneScreen
    state "Combat" as Fight
    state "Choix" as Choice
    state "Fin" as Ending

    [*] --> Menu
    Menu --> HomeScreen : nouvelle partie
    Menu --> HomeScreen : charger la partie démo
    HomeScreen --> TeamScreen : poste de la Home
    TeamScreen --> HomeScreen : retour
    HomeScreen --> ZoneScreen : portail allumé
    ZoneScreen --> HomeScreen : portail
    ZoneScreen --> Fight : rencontre
    Fight --> Choice : robot vaincu
    Fight --> ZoneScreen : exosquelette vaincu, portail suivant allumé
    Fight --> HomeScreen : toute l'équipe K.O.
    Fight --> Ending : chef de NEXUS vaincu
    Choice --> ZoneScreen : reprogrammer ou achever
    Ending --> [*]
```

## Points à trancher

Ces choix ne sont pas dans le GDD. Ils sont à valider avant de coder.

1. **Classes ajoutées** : `BaseStats`, `RobotType`, les trois implémentations de `Skill`, `DamageCalculator`, `TurnResult`, `CombatOutcome`, `EventBus`, `PartsStock`, `WantedTier`, `ChoiceResolver`, `Game`, `Zone`, `TileMap`, `Tile`. Leurs noms sont libres.
2. **Surcharge une fois par combat** : gérée ici par un booléen dans `Exosquelette`. Une `Skill` à usage limité serait plus générale.
3. **Plus aucun robot disponible** (tous en réparation) : toujours ouvert dans le GDD, donc absent des diagrammes.
4. **XP et niveaux** (SHOULD) : seul `gainXp()` apparaît ; le détail du passage de niveau n'est pas modélisé.

## Déjà tranché

- **`Boss` hérite d'`Exosquelette`**, et non de `Robot` : le chef de NEXUS est un humain en exosquelette, jamais reprogrammable et sans état « Détourné ».
- **Trois événements** : `RobotKilledEvent`, `RobotReprogrammedEvent` et `PilotNeutralizedEvent`, parce que la barre de recherche bouge aussi quand on reprogramme (− 5) ou qu'on neutralise un pilote (+ 10).
