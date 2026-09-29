# Game Design Document Reboot

Auteurs : Paul & Valentin  « 2D Game » (Java / POO)
Statut : brouillon à affiner avec l'agent Game Designer de BMAD (`/bmgd-gdd`).
Tout ce qui est marqué « proposition » reste modifiable.

## Contraintes du projet

- Langage : **Java**, interface graphique **JavaFX** (bootstrap fourni).
- Objectif noté : **mise en pratique de la POO** (héritage, polymorphisme, design patterns).
- Livrables : GDD, diagrammes UML avec design patterns, code + GUI, tests avec couverture **JaCoCo**, **Javadoc** complète, soutenance avec démo jouable.
- C'est un **POC** : gameplay avant graphismes, périmètre réduit.

## Présentation

Reboot est un RPG futuriste en vue de dessus, à Néo-Lyon : le joueur bat des robots piratés, puis choisit de les reprogrammer ou de les achever, et ce choix change le monde autour de lui.

| | |
| --- | --- |
| Genre | RPG tour par tour, collection de robots |
| Inspiration | Pokémon (éditions DS) |
| Vue | 2D de dessus, carte en tuiles |
| Plateforme | PC, Java + JavaFX |
| Durée d'une partie (POC) | Environ 20 à 30 minutes (proposition) |

**Les 3 piliers du jeu :**

1. **Le choix moral** : reprogrammer ou achever, avec une vraie conséquence dans les deux cas.
2. **Un monde qui réagit** : la barre de recherche et le niveau du réseau changent les rencontres.
3. **L'équipe** : les robots reprogrammés deviennent les combattants du joueur.

## Univers et lore

NEXUS (Network for Extreme Unity and Suppression), un groupe radical, a piraté le data center de Néo-Lyon qui relie toutes les IA : les robots, bienveillants à l'origine, sont devenus hostiles.

- **Avant** : chaque robot a une IA intégrée et bienveillante. Tous partagent leurs connaissances via un data center central.
- **L'attaque** : le groupe radical lance une cyberattaque sur le data center. Le réseau diffuse un comportement malveillant à tous les robots connectés.
- **Le but du groupe** : renverser le gouvernement grâce à cette armée de robots.
- **Les exosquelettes** : les membres du groupe se battent eux-mêmes dans des exosquelettes. Ils sont humains, donc impossibles à reprogrammer.
- **Le réseau apprend** : quand un robot est détruit, le data center enregistre sa défaite et rend tous les autres plus forts.
- **La fin** : détruire le data center coupe le lien. Les robots retrouvent leur programmation d'origine.

Le groupe s'appelle NEXUS et la ville Néo-Lyon. Le héros s'appelle Léon.

## Boucle de jeu

```
Explorer une zone -> Rencontre -> Combat au tour par tour -> Gagné ?
   non -> retour à la dernière borne de recharge -> Explorer
   oui -> Choix :
          Reprogrammer : +1 robot dans l'équipe, barre de recherche ↓
          Achever      : niveau du réseau ↑, barre de recherche ↑
       -> retour à l'exploration
```

Quand une zone est terminée (son chef de zone est battu), la suivante s'ouvre, jusqu'au data center.

## Joueur, contrôles et équipe

Le joueur incarne Léon. Il ne se bat jamais lui-même : il dirige une équipe de 3 robots maximum.

**Départ** (proposition) : le joueur commence avec un seul robot, un robot de service qu'il a réparé lui-même, donc non piraté.

**Équipe :**

- 3 robots maximum. Pour en reprogrammer un 4e, il faut en libérer un.
- En combat, un seul robot est actif. On peut changer de robot, mais ça coûte le tour.
- Si toute l'équipe est K.O., le joueur retourne à la dernière borne de recharge, avec ses robots soignés.

**Contrôles** (proposition) :

| Touche | Action |
| --- | --- |
| ZQSD ou flèches | Se déplacer |
| Entrée | Interagir, valider |
| Échap | Menu (équipe, pause) |
| 1, 2, 3 | Choisir une attaque en combat |

## Les robots

Trois types pour le POC, selon la « sainte trinité » des jeux d'équipe : Tank, Healer, Infighter. Chacun hérite de la base `Robot` (stats + attaque commune) et ajoute deux compétences à lui. Tous les chiffres sont des propositions à équilibrer en jouant.

**4 stats pour tous les robots :** PV (points de vie), ATK (attaque), DEF (défense), VIT (vitesse : qui joue en premier).

| Type | Rôle | PV | ATK | DEF | VIT |
| --- | --- | --- | --- | --- | --- |
| TankBot | Encaisse les coups | 60 | 8 | 12 | 4 |
| HealerBot | Soigne toute l'équipe | 45 | 7 | 8 | 9 |
| InfighterBot | Corps-à-corps, gros dégâts, frappe en premier | 45 | 14 | 6 | 10 |

Stats au niveau 1. À chaque niveau, chaque stat gagne 10 % de sa valeur de base.

**Compétences :**

| Qui | Compétence | Effet |
| --- | --- | --- |
| Tous (héritée de `Robot`) | Frappe | Dégâts, puissance 10 |
| TankBot | Blindage | DEF × 2 pendant 2 tours |
| TankBot | Écrasement | Dégâts, puissance 16 |
| HealerBot | Patch réseau | Soigne 15 PV à toute l'équipe, robots du banc compris |
| HealerBot | Nanobots corrosifs | Dégâts, puissance 12, ignore la DEF |
| InfighterBot | Rafale de coups | Dégâts, puissance 16 |
| InfighterBot | Crochet | Dégâts, puissance 12, joue toujours en premier |

Côté code, chaque compétence est un objet `Skill` : on peut en ajouter une sans toucher aux classes de robots.

## Combat au tour par tour

Un robot contre un robot. Le plus rapide (VIT) joue en premier, et le combat s'arrête quand un camp n'a plus de robot debout.

**À chaque tour, le joueur choisit une action :**

1. **Attaquer** : utiliser une des compétences de son robot actif.
2. **Changer de robot** : le nouveau robot entre, mais le tour est perdu.
3. **Fuir** : possible contre les robots normaux, avec 1 chance sur 2. Impossible contre un exosquelette ou un boss.

**Formule des dégâts** (proposition) :

```
dégâts = max(1, puissance + ATK_attaquant - DEF_défenseur)
```

Exemple : l'InfighterBot (ATK 14) lance Rafale de coups (puissance 16) sur un TankBot (DEF 12). Il fait 16 + 14 − 12 = 18 dégâts, donc il faut 4 coups pour vaincre le TankBot (60 PV).

**Faiblesses entre types :** Infighter bat Healer (il frappe trop vite pour être soigné), Healer bat Tank (le Tank tape trop doucement face aux soins), Tank bat Infighter (son armure absorbe le corps-à-corps). Le type fort inflige × 1,5 dégâts au type qu'il bat (arrondi à l'entier inférieur). Exemple : l'InfighterBot fait 16 + 14 − 8 = 22 dégâts à un HealerBot, soit 33 avec le bonus.

**Fin du combat :**

- Robot ennemi vaincu : écran de choix, reprogrammer ou achever.
- Exosquelette vaincu : le pilote est neutralisé, sans choix.
- Toute l'équipe K.O. : retour à la dernière borne de recharge.

Option : petite variation aléatoire (± 10 %). Dans ce cas, le générateur aléatoire est injecté dans le combat pour pouvoir le fixer dans les tests.

## Reprogrammer ou achever

Achever, c'est sacrifier sa discrétion maintenant pour avoir des recrues plus fortes plus tard : c'est tout le dilemme du jeu.

| | Reprogrammer | Achever |
| --- | --- | --- |
| Effet immédiat | Le robot rejoint l'équipe avec son niveau (réussite garantie) | Le robot est détruit |
| Barre de recherche | − 5 | + 15 |
| Réseau | Rien | Compte pour le bonus du réseau |
| Si l'équipe est pleine | Libérer un robot, ou renoncer | Toujours possible |

**XP des robots du joueur** (proposition) :

- Chaque victoire donne 10 × le niveau de l'ennemi en XP au robot actif.
- Pour passer au niveau suivant, il faut 50 × le niveau actuel en XP.

**Niveau des ennemis :**

```
niveau ennemi = niveau de la zone + min(5, robotsAcheves / 3)   // division entière
```

Le bonus du réseau monte de 1 tous les 3 robots achevés, plafonné à + 5 pour que le jeu ne devienne jamais impossible. Un robot reprogrammé garde ce niveau : c'est la récompense de ceux qui achèvent beaucoup.

## Barre de recherche

Une jauge de 0 à 100 qui mesure à quel point le groupe radical traque le joueur. 4 paliers, chacun change le monde.

| Palier | Valeur | Effet sur le jeu |
| --- | --- | --- |
| Inconnu | 0 – 24 | Rencontres normales, on peut éviter certains robots |
| Repéré | 25 – 49 | Plus de rencontres sur les routes |
| Traqué | 50 – 74 | Ennemis plus agressifs (IA offensive), fuite impossible |
| Ennemi public | 75 – 100 | Des exosquelettes patrouillent, l'entrée discrète du data center est fermée |

**Ce qui la fait bouger** (proposition) : achever un robot + 15, reprogrammer − 5, neutraliser un exosquelette + 10.

**Entrée dans le data center :**

- **Sous 75** : par l'entrée discrète, avec moins de gardes avant le boss.
- **À 75 ou plus** : par l'entrée principale, avec plus de combats avant le boss.

Les deux chemins mènent au boss : aucun style de jeu n'est bloqué.

## Le monde

Quatre zones reliées, parcourues dans l'ordre. Chaque zone est gardée par un exosquelette : le battre ouvre la suivante.

| # | Zone (noms provisoires) | Niveau de la zone | Robots dominants | Chef de zone |
| --- | --- | --- | --- | --- |
| 1 | Quartier résidentiel | 2 | HealerBot | Exosquelette niveau 4 |
| 2 | Zone industrielle | 5 | TankBot | Exosquelette niveau 7 |
| 3 | Quartier du gouvernement | 8 | InfighterBot | Exosquelette niveau 10 |
| 4 | Data center | 11 | Tous les types | 3 gardiens + le chef (niveau fixe) |

Chaque zone a une borne de recharge, qui soigne l'équipe et sert de point de retour après une défaite.

La carte est une grille de tuiles (ex. 20 × 15 cases de 32 px) décrite dans un fichier texte par zone : modifier une carte ne demande pas de toucher au code.

## Exosquelettes et boss finaux

Les ennemis spéciaux ne suivent pas le niveau du réseau, et on ne peut jamais les reprogrammer.

**Exosquelettes (chefs de zone)**

- Pilotés par des membres du groupe radical, donc humains.
- Niveau fixe par zone, un peu au-dessus des robots de la zone.
- Une fois vaincu, le pilote est neutralisé : pas de choix, barre de recherche + 10.
- Compétence propre : Surcharge, puissance 18, une fois par combat.

**Boss finaux : les gardiens du data center**

- 3 gardiens, un par type (TankBot, HealerBot, InfighterBot), à battre à la suite, sans borne de recharge entre eux.
- Niveau fixe : 12 (proposition), réglé pour qu'une équipe qui n'a presque rien achevé puisse gagner.
- Puis le **chef du groupe radical**, dans son exosquelette : c'est le vrai boss final. Niveau fixe 13 (proposition), juste après les gardiens.
- Pas de fuite possible.
- Une fois le chef vaincu, le data center est détruit : fin du jeu, tous les robots redeviennent bienveillants.

Côté code, `Boss` redéfinit `getLevel()` pour renvoyer son niveau fixe (polymorphisme).

## Interface

Six écrans suffisent pour le POC. Le HUD d'exploration affiche toujours l'équipe et la barre de recherche.

| Écran | Ce qu'on y voit | On y arrive depuis |
| --- | --- | --- |
| Menu principal | Nouvelle partie, quitter | Lancement du jeu |
| Exploration | Carte de la zone, joueur + HUD (équipe, PV, barre de recherche, zone) | Menu, fin de combat |
| Combat | Les 2 robots, leurs PV et niveaux, 3 boutons d'attaque, changer, fuir | Une rencontre |
| Choix | Le robot vaincu, 2 boutons Reprogrammer / Achever, effet sur la barre | Victoire contre un robot |
| Équipe | Les 3 robots, leurs stats, XP et compétences, libérer un robot | Échap en exploration |
| Fin | Résumé : robots reprogrammés, achevés, barre finale | Défaite du chef |

Graphismes : formes colorées ou sprites gratuits. Le gameplay passe avant.

## Architecture visée (pour l'étape architecture BMAD)

- `Combattant` (abstraite) → `Robot` (abstraite) → `TankBot`, `HealerBot`, `InfighterBot`, `Boss` ; `Combattant` → `Exosquelette`.
- **State** : `RobotState` (Détourné / Allié) — un ennemi et un allié sont la même classe.
- **Strategy** : `CombatStrategy`, IA de combat par type, plus offensive selon la barre de recherche.
- **Factory** : `RobotFactory` crée les rencontres d'une zone (niveau = zone + bonus réseau).
- **Observer** : `RobotKilledEvent` → `DataCenter` (niveau du réseau) et `WantedBar`.
- **Composition** : `Player` possède une `Team` de robots.
- Séparation modèle / vue JavaFX pour tester la logique sans lancer l'interface.



**Déjà tranché :**

- Héros : **Léon**. Titre : **Reboot**. Ville : **Néo-Lyon**. Groupe radical : **NEXUS** (Network for Extreme Unity and Suppression).
- 3 types de robots, la sainte trinité (TankBot, HealerBot, InfighterBot), avec faiblesses entre types (× 1,5).
- Boss final : les 3 gardiens, puis le chef de NEXUS dans un exosquelette.
- Achever ne rapporte que le bonus du réseau, pas de pièces.
- La reprogrammation réussit toujours.