# Diagrammes UML de Reboot

Ces diagrammes décrivent le code tel qu'il est dans `src/main/java/com/reboot/model`. Ils ont d'abord servi de plan (première version tirée du GDD), puis ont été remis à jour pour correspondre au code réel : mêmes noms de classes, de méthodes et de paquets.

Les diagrammes sont écrits en Mermaid : GitHub les affiche directement, et ils se modifient comme du texte. Pour garder la doc juste, une PR qui ajoute, renomme ou déplace une classe met aussi ce fichier à jour.

Le modèle ne dépend pas de JavaFX : les vues (`com.reboot.view`) et `GameController` lisent le modèle et le pilotent, mais le modèle ne les connaît pas. Tout ce qui est décrit ici se teste avec JUnit sans ouvrir de fenêtre.

| # | Diagramme | Paquet(s) | Design pattern |
| --- | --- | --- | --- |
| 1 | [Combattants et compétences](#1-combattants-et-compétences) | `fighter`, `skill` | Héritage, polymorphisme, Decorator |
| 2 | [États d'un robot](#2-états-dun-robot) | `fighter` | State |
| 3 | [Combat](#3-combat) | `combat` | Strategy |
| 4 | [Conséquences du choix](#4-conséquences-du-choix) | `event`, `nexus`, `team`, `game` | Observer |
| 5 | [Partie, équipe et monde](#5-partie-équipe-et-monde) | `game`, `team`, `world`, `fighter` | Composition, Factory |
| 6 | [Quêtes et campagne](#6-quêtes-et-campagne) | `quest` | Composite |
| 7 | [Réglages et souvenirs](#7-réglages-et-souvenirs) | `settings`, `story` | Aucun |
| 8 | [Séquence : un tour de combat](#8-séquence--un-tour-de-combat) | `combat`, `skill` | Strategy |
| 9 | [Séquence : reprogrammer ou achever](#9-séquence--reprogrammer-ou-achever) | `game`, `event` | State + Observer |
| 10 | [Enchaînement des écrans](#10-enchaînement-des-écrans) | `view` | Template Method |

## 1. Combattants et compétences

`Combattant` porte ce qui est commun à tout ce qui se bat : stats, PV, XP, compétences. Les stats se calculent à partir des stats de base et du niveau (+ 10 % de la base par niveau). Les robots et les exosquelettes connaissent tous la Frappe (`Combattant.STRIKE`) : `Robot` et `Exosquelette` l'ajoutent à leurs compétences.

Deux branches en héritent. Les `Robot` ont un type et un état, et peuvent être reprogrammés. Les `Exosquelette` sont pilotés par des humains : `isReprogrammable()` renvoie `false`. Le chef de NEXUS est un `Boss`, un exosquelette créé au niveau fixe `FIXED_LEVEL` (10), qui redéfinit `endsTheGame()` pour terminer la partie quand il est vaincu.

Le polymorphisme est partout : `isReprogrammable()` et `getSpriteId()` sont abstraites dans `Combattant` et chaque branche répond à sa façon ; `advantageAgainst()` et `weaknessTo()` ne donnent le bonus de type × 1,5 qu'entre robots, sans aucun `instanceof`.

Chaque compétence est un objet `Skill` qui applique son propre effet dans `apply()` : en ajouter une ne demande de toucher à aucune classe de robot. `OncePerCombatSkill` est un Decorator : il enveloppe n'importe quelle compétence pour la rendre utilisable une seule fois par combat (la Surcharge des exosquelettes).

```mermaid
classDiagram
    direction TB

    class Combattant {
        <<abstract>>
        +DamageSkill STRIKE$
        +int XP_PER_LEVEL$
        +int XP_REWARD_PER_LEVEL$
        -String name
        -int level
        -int xp
        -int hp
        -int baseHp
        -int baseAtk
        -int baseDef
        -int baseSpeed
        -int defenseMultiplier
        -List~Skill~ skills
        +gainXp(int amount) int
        +getXpToNextLevel() int
        +getXpReward() int
        +takeDamage(int amount) void
        +heal(int amount) void
        +isKo() boolean
        +isReprogrammable()* boolean
        +getSpriteId()* String
        +endsTheGame() boolean
        +advantageAgainst(Combattant defender) double
        #weaknessTo(RobotType attackerType) double
        +boostDefense(int multiplier, int turns) void
        +endTurn() void
        #addSkill(Skill skill) void
        +getSkills() List~Skill~
        +getLevel() int
        +getMaxHp() int
        +getAtk() int
        +getDef() int
        +getSpeed() int
    }

    class Robot {
        <<abstract>>
        -RobotType type
        -RobotState state
        +getType() RobotType
        +getSpriteId() String
        +advantageAgainst(Combattant defender) double
        #weaknessTo(RobotType attackerType) double
        +isReprogrammable() boolean
        +canFightForPlayer() boolean
        +reprogram() void
        +handleKnockOut() void
    }

    class TankBot {
        +DefenseBoostSkill ARMOR$
        +DamageSkill CRUSH$
        +TankBot(int level)
    }

    class HealerBot {
        +HealSkill NETWORK_PATCH$
        +DamageSkill CORROSIVE_NANOBOTS$
        +HealerBot(int level)
    }

    class InfighterBot {
        +DamageSkill FLURRY$
        +DamageSkill HOOK$
        +InfighterBot(int level)
    }

    class Exosquelette {
        +Skill OVERLOAD$
        +Exosquelette(int level)
        +isReprogrammable() boolean
        +getSpriteId() String
    }

    class Boss {
        +int FIXED_LEVEL$
        +Boss()
        +getSpriteId() String
        +endsTheGame() boolean
    }

    class RobotType {
        <<enumeration>>
        TANK
        HEALER
        INFIGHTER
        +double ADVANTAGE_BONUS$
        +beats(RobotType other) boolean
        +multiplierAgainst(RobotType other) double
    }

    class Skill {
        <<interface>>
        +getName() String
        +hasPriority() boolean
        +isOncePerCombat() boolean
        +apply(Combattant user, Combattant target) String
    }

    class DamageSkill {
        -String name
        -int power
        -boolean ignoresDefense
        -boolean priority
        +getPower() int
        +ignoresDefense() boolean
    }

    class HealSkill {
        -String name
        -int amount
        +getAmount() int
    }

    class DefenseBoostSkill {
        -String name
        -int multiplier
        -int turns
        +getMultiplier() int
        +getTurns() int
    }

    class OncePerCombatSkill {
        -Skill skill
        +isOncePerCombat() boolean
    }

    class DamageCalculator {
        <<utility>>
        +compute(Combattant attacker, Combattant defender, int power, boolean ignoresDefense)$ int
    }

    Combattant <|-- Robot
    Combattant <|-- Exosquelette
    Robot <|-- TankBot
    Robot <|-- HealerBot
    Robot <|-- InfighterBot
    Exosquelette <|-- Boss
    Combattant o-- "2..3" Skill
    Robot --> "1" RobotType
    Skill <|.. DamageSkill
    Skill <|.. HealSkill
    Skill <|.. DefenseBoostSkill
    Skill <|.. OncePerCombatSkill
    OncePerCombatSkill o-- "1" Skill : enveloppe
    DamageSkill ..> DamageCalculator
```

Correspondance avec les compétences du GDD :

| Compétence | Classe | Réglage |
| --- | --- | --- |
| Frappe (`Combattant.STRIKE`), Écrasement, Rafale de coups | `DamageSkill` | puissance 10, 16, 16 |
| Nanobots corrosifs | `DamageSkill` | puissance 12, `ignoresDefense` |
| Crochet | `DamageSkill` | puissance 12, `priority` |
| Surcharge (`Exosquelette.OVERLOAD`) | `OncePerCombatSkill` autour d'un `DamageSkill` | puissance 18, une fois par combat |
| Patch réseau | `HealSkill` | 15 PV |
| Blindage | `DefenseBoostSkill` | DEF × 2 pendant 2 tours |

`DamageCalculator.compute()` applique la formule du GDD : `max(1, puissance + ATK − DEF)`, la DEF étant ignorée si la compétence le permet, puis × 1,5 arrondi à l'entier inférieur si le type de l'attaquant bat celui du défenseur.

## 2. États d'un robot

Un ennemi et un allié sont la même classe `Robot` : seul son état change. `Robot` délègue à son `RobotState` tout ce qui dépend de l'état, au lieu d'empiler des `if`. Chaque événement (`onReprogram`, `onKnockOut`…) renvoie l'état suivant ; une transition interdite (reprogrammer un allié) lance une `IllegalStateException`.

```mermaid
classDiagram
    direction LR

    class Robot {
        <<abstract>>
        -RobotState state
        +getState() RobotState
        +canFightForPlayer() boolean
        +isReprogrammable() boolean
        +isUnderRepair() boolean
        +getRepairVictoriesLeft() int
        +reprogram() void
        +handleKnockOut() void
        +recordTeamVictory() void
        +useRepairPart() void
    }

    class RobotState {
        <<interface>>
        +canFightForPlayer() boolean
        +isReprogrammable() boolean
        +isUnderRepair() boolean
        +onReprogram() RobotState
        +onKnockOut() RobotState
        +onTeamVictory() RobotState
        +onPartUsed() RobotState
    }

    class HijackedState
    class AllyState

    class RepairingState {
        +int VICTORIES_TO_REPAIR$
        -int victoriesLeft
        +getVictoriesLeft() int
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

    state "Détourné (HijackedState)" as Hijacked
    state "Allié (AllyState)" as Ally
    state "En réparation (RepairingState)" as Repairing

    [*] --> Hijacked : robot créé par RobotFactory
    [*] --> Ally : robot de départ de Léon
    Hijacked --> Ally : reprogram()
    Hijacked --> [*] : achever
    Ally --> Repairing : handleKnockOut()
    Repairing --> Repairing : recordTeamVictory() ou useRepairPart() (délai − 1)
    Repairing --> Ally : délai à 0 (3 victoires au départ)
```

## 3. Combat

`Combat` ne connaît ni JavaFX ni la carte : il se teste seul. `Encounter` crée le combat et en tire un `EncounterResult` : le choix reprogrammer / achever n'est proposé qu'après une victoire contre un ennemi reprogrammable (`isReprogrammable()`, polymorphe). `CombatLauncher` est la frontière entre l'exploration et le combat : `GameController` l'implémente.

L'IA ennemie suit le pattern Strategy : `Combat` demande sa compétence à une `CombatStrategy` sans savoir comment elle décide. `FirstSkillStrategy` (toujours la première compétence) est la stratégie par défaut ; en jeu, `GameController` donne une `RandomStrategy`, qui reçoit son générateur aléatoire par le constructeur pour que les tests soient reproductibles. `Combat` ne donne à la stratégie que les compétences encore utilisables : une compétence à usage unique (`isOncePerCombat()`) déjà lancée, comme la Surcharge, en est retirée.

```mermaid
classDiagram
    direction TB

    class CombatLauncher {
        <<interface>>
        +launch(Combattant enemy) void
    }

    class Encounter {
        -Combat combat
        +Encounter(Combattant player, Combattant enemy)
        +Encounter(Team team, Combattant enemy, CombatStrategy enemyStrategy)
        +getCombat() Combat
        +isFinished() boolean
        +getResult() EncounterResult
    }

    class EncounterResult {
        <<record>>
        +CombatOutcome outcome
        +Combattant enemy
        +boolean choiceAvailable
    }

    class Combat {
        -List~Robot~ roster
        -Combattant active
        -Combattant enemy
        -CombatStrategy enemyStrategy
        -Map spentSkills
        -int turn
        +Combat(Combattant player, Combattant enemy)
        +Combat(Team team, Combattant enemy)
        +Combat(Team team, Combattant enemy, CombatStrategy enemyStrategy)
        +useSkill(Skill skill) TurnResult
        +switchTo(Robot next) TurnResult
        +getSwitchOptions() List~Robot~
        +mustSwitch() boolean
        +isOver() boolean
        +getOutcome() CombatOutcome
        +getRoster() List~Robot~
        +getTurn() int
        +getPlayer() Combattant
        +getEnemy() Combattant
    }

    class CombatStrategy {
        <<interface>>
        +chooseSkill(Combattant attacker, List~Skill~ usable, Combattant target) Skill
    }

    class FirstSkillStrategy

    class RandomStrategy {
        -Random random
        +RandomStrategy(Random random)
    }

    class Skill {
        <<interface>>
        +apply(Combattant user, Combattant target) String
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

    CombatLauncher ..> Encounter : crée
    Encounter *-- "1" Combat
    Encounter ..> EncounterResult : produit
    Combat --> "0..*" Robot : équipe du joueur
    Combat --> "1" Combattant : ennemi
    Combat o-- "1" CombatStrategy
    Combat ..> Skill : apply()
    Combat ..> TurnResult
    Combat ..> CombatOutcome
    CombatStrategy <|.. FirstSkillStrategy
    CombatStrategy <|.. RandomStrategy
```

`Combat` ne calcule pas les dégâts lui-même : il appelle `Skill.apply()`, et chaque compétence applique son effet (section 1). Quand l'ennemi tombe, le robot qui l'a mis K.O. gagne de l'XP (`gainXp()`). `OffensiveStrategy` (IA offensive selon la barre de recherche) reste un bonus COULD, non codé.

## 4. Conséquences du choix

Reprogrammer ou achever (`ChoiceResolver`) et neutraliser un pilote (`Game.onCombatEnd()`) publient chacun un événement sur l'`EventBus` : la barre de recherche bouge dans les trois cas, donc il y a trois événements. Ceux qui en dépendent s'abonnent : `ChoiceResolver` et le combat ne connaissent ni le réseau, ni la barre de recherche, ni le stock de pièces.

Chaque événement sait à quelle méthode de l'abonné il correspond (`dispatchTo()`) : l'`EventBus` n'a donc aucun `if` sur le type d'événement. `GameEventListener` a une méthode par événement, et chaque abonné ne redéfinit que celles qui l'intéressent.

```mermaid
classDiagram
    direction TB

    class GameEvent {
        <<interface>>
        +dispatchTo(GameEventListener listener) void
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
        +Combattant exosquelette
    }

    class GameEventListener {
        <<interface>>
        +onRobotKilled(RobotKilledEvent event) void
        +onRobotReprogrammed(RobotReprogrammedEvent event) void
        +onPilotNeutralized(PilotNeutralizedEvent event) void
    }

    class EventBus {
        -List~GameEventListener~ listeners
        +subscribe(GameEventListener listener) void
        +publish(GameEvent event) void
    }

    class ChoiceResolver {
        +ChoiceResolver(Game game)
        +previewReprogram(Robot robot) String
        +previewFinish(Robot robot) String
        +reprogram(Robot robot) ChoiceOutcome
        +finish(Robot robot) ChoiceOutcome
    }

    class ChoiceOutcome {
        <<record>>
        +boolean reprogrammed
        +List~String~ lines
    }

    class DataCenter {
        +int MAX_BONUS$
        +int KILLS_PER_LEVEL$
        -int robotsKilled
        +getRobotsKilled() int
        +getNetworkBonus() int
    }

    class WantedBar {
        +int MAX$
        +int KILL$
        +int PILOT$
        +int REPROGRAM$
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
        +of(int value)$ WantedTier
        +getLabel() String
        +getThreshold() int
    }

    class PartsStock {
        -int parts
        +partsFor(Robot robot)$ int
        +repair(Robot robot) boolean
        +getParts() int
    }

    GameEvent <|.. RobotKilledEvent
    GameEvent <|.. RobotReprogrammedEvent
    GameEvent <|.. PilotNeutralizedEvent
    GameEvent ..> GameEventListener : dispatchTo()
    EventBus o-- "*" GameEventListener
    EventBus ..> GameEvent : publie
    ChoiceResolver ..> EventBus : publie
    Game ..> EventBus : publie (pilote)
    ChoiceResolver ..> ChoiceOutcome : renvoie
    GameEventListener <|.. DataCenter
    GameEventListener <|.. WantedBar
    GameEventListener <|.. PartsStock
    WantedBar ..> WantedTier
```

| Événement | `DataCenter` | `WantedBar` | `PartsStock` |
| --- | --- | --- | --- |
| `RobotKilledEvent` | robots achevés + 1 | + 15 | + `partsFor(robot)` pièces (1 + niveau / 3) |
| `RobotReprogrammedEvent` | Aucun effet | − 5 | Aucun effet |
| `PilotNeutralizedEvent` | Aucun effet | + 10 | Aucun effet |

Le bonus du réseau vaut `min(MAX_BONUS, robotsAchevés / KILLS_PER_LEVEL)`, soit `min(5, robotsAchevés / 3)`, et la barre reste entre 0 et 100. Ses paliers : Inconnu (0), Repéré (25), Traqué (50), Ennemi public (75).

## 5. Partie, équipe et monde

`Game` est le point d'entrée du modèle : il possède l'équipe, la Home, la campagne et les abonnés de l'`EventBus`, et réagit à la fin de chaque combat (`onCombatEnd()`). Les zones ne sont pas des objets : ce sont des numéros (`Campaign.HOME`, `RESIDENTIAL`, `INDUSTRIAL`, `DATA_CENTER`).

`Team` contient 3 robots au plus ; la `Home` stocke les autres et les échange avec l'équipe. `RobotFactory` est le seul endroit qui fabrique les robots des rencontres, au niveau de la zone plus le bonus du réseau (`encounterLevel()`).

Côté monde, `TileMap` est chargée depuis un fichier texte ; `Player` s'y déplace case par case, et `EncounterTrigger` décide si une case de rencontre lance un combat (avec un `Random` injecté). C'est `GameController` qui relie le monde et la partie.

```mermaid
classDiagram
    direction TB

    class QuestContext {
        <<interface>>
    }

    class Game {
        +int STARTER_LEVEL$
        -Team team
        -Home home
        -Campaign campaign
        -int currentZone
        -EventBus events
        -WantedBar wantedBar
        -DataCenter dataCenter
        -PartsStock parts
        -GameStatus status
        +Game(Team team)
        +newGame()$ Game
        +onCombatEnd(EncounterResult result) void
        +enterZone(int zone) void
        +isZoneUnlocked(int zone) boolean
        +canChallengeChief(int zone) boolean
        +canChallengeBoss() boolean
        +restAtHome() void
        +isOver() boolean
        +getStatus() GameStatus
    }

    class GameStatus {
        <<enumeration>>
    }

    class Team {
        +int MAX_SIZE$
        -List~Robot~ robots
        +Team(Robot starter)
        +add(Robot robot) boolean
        +replace(Robot out, Robot in) void
        +isFull() boolean
        +getRobots() List~Robot~
        +getActive() Robot
        +getTeamLevel() int
        +getWeakest() Robot
        +hasFighterLeft() boolean
        +isAllUnderRepair() boolean
        +recordVictory() void
        +sendKnockedOutToRepair() void
        +healAll() void
    }

    class Home {
        -List~Robot~ stored
        +store(Robot robot) void
        +swap(Team team, Robot fromTeam, Robot fromHome) void
        +sendToTeam(Team team, Robot robot) boolean
        +fillTeam(Team team) void
        +recordVictory() void
        +hasNoFighter() boolean
        +getStored() List~Robot~
    }

    class RobotFactory {
        -Random random
        +RobotFactory(Random random)
        +encounterLevel(int zoneLevel, int networkBonus)$ int
        +create(RobotType type, int level) Robot
        +createEncounter(List~RobotType~ dominantTypes, int level) Robot
    }

    class TileMap {
        +char SPAWN_SYMBOL$
        +int NO_PORTAL$
        -Tile[][] tiles
        +load(String path)$ TileMap
        +parse(List~String~ rows)$ TileMap
        +getWidth() int
        +getHeight() int
        +getSpawnX() int
        +getSpawnY() int
        +contains(int x, int y) boolean
        +tileAt(int x, int y) Tile
        +isWalkable(int x, int y) boolean
        +portalZoneAt(int x, int y) int
    }

    class Tile {
        <<enumeration>>
        +isWalkable() boolean
        +fromSymbol(char symbol)$ Tile
    }

    class Player {
        -int x
        -int y
        -Direction facing
        +Player(int x, int y)
        +move(Direction direction, TileMap map) boolean
        +getX() int
        +getY() int
        +getFacing() Direction
    }

    class Direction {
        <<enumeration>>
        +getDx() int
        +getDy() int
    }

    class EncounterTrigger {
        -Random random
        -int oneIn
        +EncounterTrigger(Random random, int oneIn)
        +shouldTrigger(Tile tile) boolean
    }

    QuestContext <|.. Game
    Game *-- "1" Team
    Game *-- "1" Home
    Game *-- "1" Campaign
    Game *-- "1" EventBus
    Game *-- "1" WantedBar
    Game *-- "1" DataCenter
    Game *-- "1" PartsStock
    Game --> GameStatus
    Team o-- "1..3" Robot
    Home o-- "*" Robot
    RobotFactory ..> Robot : crée
    TileMap --> Tile
    Player ..> TileMap : se déplace sur
    Player --> Direction
    EncounterTrigger ..> Tile
```

## 6. Quêtes et campagne

La progression suit le pattern Composite : une `QuestGroup` (un chapitre) est elle-même une `Quest`, faite d'autres quêtes. La vue des quêtes affiche un chapitre et une quête simple de la même façon, sans savoir laquelle elle a en main.

Les quêtes ne connaissent pas `Game` : elles lisent la partie à travers l'interface `QuestContext` (victoires par zone, taille et niveau de l'équipe, chefs vaincus…), que `Game` implémente. `Campaign` décide quelles zones sont débloquées et quand on peut défier un chef ou le boss. Le data center est gardé par `DISCREET_GUARDS` (1) garde tant que la barre de recherche reste sous `DISCREET_ENTRY_LIMIT` (75), et par `MAIN_GUARDS` (3) au-delà (`guardsFor()`).

```mermaid
classDiagram
    direction TB

    class Quest {
        <<interface>>
        +getDescription() String
        +getProgress(QuestContext context) int
        +getTarget(QuestContext context) int
    }

    class QuestContext {
        <<interface>>
    }

    class QuestGroup {
        -String title
        -List~Quest~ quests
        +QuestGroup(String title, List~Quest~ quests)
        +isGuardianOpen(QuestContext context) boolean
        +getQuests() List~Quest~
    }

    class WinsQuest {
        +WinsQuest(int zone, String zoneName, int target)
    }

    class TeamSizeQuest {
        +TeamSizeQuest(int target)
    }

    class TeamLevelQuest {
        +TeamLevelQuest(int target)
    }

    class GuardsQuest {
        +GuardsQuest(int zone)
    }

    class ChiefQuest {
        +ChiefQuest(int zone)
    }

    class BossQuest {
        +BossQuest()
    }

    class Campaign {
        +int HOME$
        +int RESIDENTIAL$
        +int INDUSTRIAL$
        +int DATA_CENTER$
        +int DISCREET_ENTRY_LIMIT$
        +int DISCREET_GUARDS$
        +int MAIN_GUARDS$
        +int WINS_PER_ZONE$
        +int RESIDENTIAL_TEAM_LEVEL$
        +int INDUSTRIAL_TEAM_LEVEL$
        +guardsFor(int wanted)$ int
        +getChapters() List~QuestGroup~
        +isUnlocked(int zone, QuestContext context) boolean
        +canChallengeChief(int zone, QuestContext context) boolean
        +canChallengeBoss(QuestContext context) boolean
        +current(QuestContext context) Optional~QuestGroup~
    }

    Quest <|.. QuestGroup
    Quest <|.. WinsQuest
    Quest <|.. TeamSizeQuest
    Quest <|.. TeamLevelQuest
    Quest <|.. GuardsQuest
    Quest <|.. ChiefQuest
    Quest <|.. BossQuest
    QuestGroup o-- "*" Quest : chapitre
    Campaign *-- "*" QuestGroup
    Quest ..> QuestContext : lit
    Campaign ..> QuestContext : lit
```

## 7. Réglages et souvenirs

Les réglages du joueur (taille de fenêtre, plein écran, luminosité, touches) sont un modèle à part, enregistré sous forme de `Properties`. `MemoryBook` charge les souvenirs des robots depuis un fichier texte et en tire un au hasard selon le type du robot, pour l'écran de choix.

```mermaid
classDiagram
    direction TB

    class Settings {
        +List~WindowSize~ WINDOW_SIZES$
        +int MIN_BRIGHTNESS$
        +int MAX_BRIGHTNESS$
        -int windowSizeIndex
        -boolean fullscreen
        -int brightness
        -KeyBindings keys
        +cycleWindowSize(int delta) void
        +adjustBrightness(int delta) void
        +setFullscreen(boolean fullscreen) void
        +getWindowSize() WindowSize
        +isFullscreen() boolean
        +getBrightness() int
        +getKeys() KeyBindings
        +toProperties() Properties
        +fromProperties(Properties properties)$ Settings
    }

    class KeyBindings {
        +keyOf(Action action) String
        +actionOf(String key) Optional~Action~
        +bind(Action action, String key) void
        +resetToDefaults() void
    }

    class Action {
        <<enumeration>>
        +getLabel() String
    }

    class WindowSize {
        <<record>>
        +int width
        +int height
        +label() String
    }

    class MemoryBook {
        +String DEFAULT_PATH$
        +load(String path)$ MemoryBook
        +parse(List~String~ lines)$ MemoryBook
        +pick(RobotType type, Random random) String
        +count(RobotType type) int
    }

    Settings *-- "1" KeyBindings
    Settings ..> WindowSize
    KeyBindings ..> Action
    MemoryBook ..> RobotType
```

## 8. Séquence : un tour de combat

Le joueur choisit une compétence. Le plus rapide joue en premier, sauf si une compétence est prioritaire (Crochet). Si le premier met l'autre K.O., il n'y a pas de riposte.

```mermaid
sequenceDiagram
    actor Joueur
    participant Vue as CombatView (JavaFX)
    participant Combat
    participant IA as CombatStrategy
    participant Skill as Skill
    participant Calc as DamageCalculator
    participant Allie as Robot actif
    participant Ennemi as Combattant ennemi

    Joueur->>Vue: touche 1, 2 ou 3
    Vue->>Combat: useSkill(skill)
    Combat->>IA: chooseSkill(ennemi, compétences utilisables, robot actif)
    IA-->>Combat: compétence ennemie
    Note over Combat: ordre du tour : priorité, puis VIT

    Combat->>Skill: apply(premier, second)
    Skill->>Calc: compute(attaquant, défenseur, puissance, ignoresDefense)
    Calc-->>Skill: dégâts
    Skill->>Ennemi: takeDamage(dégâts)
    Skill-->>Combat: ligne du journal

    alt le second est encore debout
        Combat->>Skill: apply(second, premier)
        Skill->>Allie: takeDamage(dégâts)
        Skill-->>Combat: ligne du journal
    else l'ennemi est K.O.
        Combat->>Allie: gainXp(xp)
    end

    Combat->>Allie: endTurn()
    Combat->>Ennemi: endTurn()
    Combat-->>Vue: TurnResult (journal, combat fini ?)
    Vue-->>Joueur: PV et journal mis à jour
```

À la fin du combat, `Game.onCombatEnd()` enregistre la victoire ou la défaite ; les robots K.O. de l'équipe passent en réparation (`Team.sendKnockedOutToRepair()`, donc `handleKnockOut()` et le State de la section 2).

## 9. Séquence : reprogrammer ou achever

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

    Vue->>Choix: previewReprogram(robot) / previewFinish(robot)
    Choix-->>Vue: aperçu des conséquences
    Vue-->>Joueur: souvenir du robot (MemoryBook) et aperçu

    alt Reprogrammer
        Joueur->>Vue: bouton Reprogrammer
        Vue->>Choix: reprogram(robot)
        Choix->>Robot: reprogram()
        Note over Robot: HijackedState devient AllyState
        alt l'équipe a une place
            Choix->>Equipe: add(robot)
        else l'équipe est pleine, le robot a un niveau plus haut que le plus faible
            Choix->>Equipe: replace(plus faible, robot)
            Choix->>Home: store(plus faible)
        else l'équipe est pleine
            Choix->>Home: store(robot)
        end
        Choix->>Bus: publish(RobotReprogrammedEvent)
        Bus->>Barre: onRobotReprogrammed, barre − 5
    else Achever
        Joueur->>Vue: bouton Achever
        Vue->>Choix: finish(robot)
        Choix->>Bus: publish(RobotKilledEvent)
        Bus->>DC: onRobotKilled, robots achevés + 1
        Bus->>Barre: onRobotKilled, barre + 15
        Bus->>Pieces: onRobotKilled, + partsFor(robot) pièces
    end

    Choix-->>Vue: ChoiceOutcome (lignes à afficher)
    Vue-->>Joueur: retour à l'exploration
```

## 10. Enchaînement des écrans

La boucle de jeu, vue comme une suite d'écrans. Chaque écran est une vue de `com.reboot.view` ; `GameController` passe de l'une à l'autre. Options et Quêtes reviennent à l'écran d'où on les a ouverts.

`MenuView` n'est pas un écran : c'est la classe abstraite dont héritent `MainMenuView`, `PauseView`, `QuestsView`, `OptionsView` et `HomeView` (pattern Template Method). Elle gère la navigation au clavier, et chaque menu ne fournit que ses entrées.

```mermaid
stateDiagram-v2
    state "Menu principal (MainMenuView)" as MainMenu
    state "Options (OptionsView)" as Options
    state "Exploration (ExplorationView)" as Explore
    state "Home (HomeView)" as HomeScreen
    state "Pause (PauseView)" as Pause
    state "Quêtes (QuestsView)" as Quests
    state "Combat (CombatView)" as Fight
    state "Choix (ChoiceView)" as Choice
    state "Fin (EndView)" as Ending

    [*] --> MainMenu
    MainMenu --> Explore : nouvelle partie
    MainMenu --> Options
    Options --> MainMenu : retour
    MainMenu --> [*] : quitter
    Explore --> HomeScreen : poste de la Home
    HomeScreen --> Explore : retour
    Explore --> Pause : Échap
    Pause --> Explore : reprendre
    Pause --> Quests
    Quests --> Pause : retour
    Pause --> Options
    Options --> Pause : retour
    Pause --> MainMenu : abandonner la partie
    Explore --> Fight : rencontre
    Fight --> Choice : robot vaincu
    Fight --> Explore : exosquelette vaincu
    Fight --> Explore : défaite, retour à la Home soignée
    Fight --> Ending : chef de NEXUS vaincu, ou toute l'équipe en réparation
    Choice --> Explore : reprogrammer ou achever
    Ending --> MainMenu
```

## Ce qui n'est pas codé

Ces éléments du GDD n'existent pas dans le code, et n'apparaissent donc pas dans les diagrammes (ou y sont signalés) :

1. **`OffensiveStrategy`** (COULD) : une IA plus offensive quand la barre de recherche est haute. La Strategy de la section 3 permet de l'ajouter sans toucher à `Combat`.
2. **Les effets des paliers de la barre** (SHOULD) : le palier est calculé (`WantedTier`), mais seul « Ennemi public » a un effet : le data center est gardé par 3 gardes au lieu d'un (`Campaign.guardsFor()`).
3. **Pas de classe `Zone`** : une zone est un numéro de `Campaign`, sa carte une `TileMap`.

## Déjà tranché

- **Surcharge une fois par combat** : `OncePerCombatSkill` (pattern Decorator) marque la compétence, et `Combat` retient celles déjà lancées. N'importe quelle compétence peut devenir à usage unique sans toucher à `Exosquelette`.
- **`Boss` hérite d'`Exosquelette`**, et non de `Robot` : le chef de NEXUS est un humain en exosquelette, jamais reprogrammable et sans état « Détourné ». Il est créé au niveau fixe 10 et redéfinit `endsTheGame()`.
- **Trois événements** : `RobotKilledEvent`, `RobotReprogrammedEvent` et `PilotNeutralizedEvent`, parce que la barre de recherche bouge aussi quand on reprogramme (− 5) ou qu'on neutralise un pilote (+ 10).
- **Pas d'`instanceof` pour le bonus de type ni pour le choix** : `advantageAgainst()` / `weaknessTo()` et `isReprogrammable()` sont redéfinies par chaque branche.
