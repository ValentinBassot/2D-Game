# Game Design Document Reboot

Auteurs : Paul & Valentin  « 2D Game » (Java / POO)
Statut : brouillon, mis à jour après la réunion de périmètre du 2026-10-02 (tâches et planning dans le Notion du projet).
Tout ce qui est marqué « proposition » reste modifiable. Les étiquettes **MUST / SHOULD / COULD** renvoient au [périmètre POC](#périmètre-du-poc-3-semaines).

## Contraintes du projet

- Langage : **Java**, interface graphique **JavaFX** (bootstrap fourni).
- Objectif noté : **mise en pratique de la POO** (héritage, polymorphisme, design patterns).
- Livrables : GDD, diagrammes UML avec design patterns, code + GUI, tests avec couverture **JaCoCo**, **Javadoc** complète, soutenance avec démo jouable.
- C'est un **POC** : gameplay avant graphismes, périmètre réduit.
- Délai : **3 semaines**, environ 25 h par semaine et par personne (≈ 150 h au total).

## Présentation

Reboot est un RPG futuriste en vue de dessus, à Néo-Lyon : le joueur bat des robots piratés, puis choisit de les reprogrammer ou de les achever, et ce choix change le monde autour de lui.

| | |
| --- | --- |
| Genre | RPG tour par tour, collection de robots |
| Inspiration | Pokémon (éditions DS, notamment Noir et Blanc) |
| Vue | 2D de dessus, carte en tuiles ; combat vu de derrière son robot |
| Plateforme | PC, Java + JavaFX |
| Durée d'une partie (POC) | Environ 15 à 20 minutes (proposition) |

**Les 3 piliers du jeu :**

1. **Le choix moral** : reprogrammer ou achever, avec une vraie conséquence dans les deux cas.
2. **Un monde qui réagit** : la barre de recherche et le niveau du réseau changent les rencontres.
3. **L'équipe** : les robots reprogrammés deviennent les combattants du joueur, et vivent à la Home.

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
Home -> Portail -> Explorer une zone -> Rencontre -> Combat au tour par tour -> Gagné ?
   non -> retour à la Home -> Explorer
   oui -> Choix :
          Reprogrammer : +1 robot (équipe ou Home), barre de recherche ↓
          Achever      : pièces de réparation, niveau du réseau ↑, barre de recherche ↑
       -> retour à l'exploration
```

Quand une zone est terminée (son chef de zone est battu), le portail de la suivante s'allume dans la Home, jusqu'au data center.

## Joueur, contrôles et équipe

Le joueur incarne Léon. Il ne se bat jamais lui-même : il dirige une équipe de 3 robots maximum.

**Départ** (proposition) : le joueur commence avec un seul robot, un robot de service qu'il a réparé lui-même, donc non piraté.

**Équipe :**

- 3 robots maximum dans l'équipe active. Les robots reprogrammés en plus sont envoyés à la **Home**, où le joueur peut changer sa composition.
- En combat, un seul robot est actif. On peut changer de robot, mais ça coûte le tour.
- Un robot K.O. est renvoyé **en réparation à la Home** et n'est plus disponible pendant son délai de réparation.
- Si toute l'équipe est K.O., le joueur retourne à la Home.

**Réparation (MUST)** (proposition) :

- Le délai se compte **en combats gagnés**, pas en temps réel : un robot K.O. est réparé après 3 victoires.
- Chaque **pièce** dépensée retire 1 victoire au délai.
- Les pièces s'obtiennent uniquement en **achevant** un robot (voir plus bas).
- À trancher : que se passe-t-il si plus aucun robot n'est disponible (tous en réparation) ? Piste : une réparation d'urgence à la Home qui remet un robot sur pied.

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
| HealerBot | Patch réseau | Soigne 15 PV à toute l'équipe active |
| HealerBot | Nanobots corrosifs | Dégâts, puissance 12, ignore la DEF |
| InfighterBot | Rafale de coups | Dégâts, puissance 16 |
| InfighterBot | Crochet | Dégâts, puissance 12, joue toujours en premier |

Côté code, chaque compétence est un objet `Skill` : on peut en ajouter une sans toucher aux classes de robots.

## Combat au tour par tour

Un robot contre un robot, vu de derrière le robot du joueur (l'ennemi en face), comme dans Pokémon. Le plus rapide (VIT) joue en premier, et le combat s'arrête quand un camp n'a plus de robot debout.

**À chaque tour, le joueur choisit une action :**

1. **Attaquer** : utiliser une des compétences de son robot actif.
2. **Changer de robot** : le nouveau robot entre, mais le tour est perdu.

La fuite est **retirée du POC** (WON'T).

**Formule des dégâts** (proposition) :

```
dégâts = max(1, puissance + ATK_attaquant - DEF_défenseur)
```

Exemple : l'InfighterBot (ATK 14) lance Rafale de coups (puissance 16) sur un TankBot (DEF 12). Il fait 16 + 14 − 12 = 18 dégâts, donc il faut 4 coups pour vaincre le TankBot (60 PV).

**Faiblesses entre types :** Infighter bat Healer (il frappe trop vite pour être soigné), Healer bat Tank (le Tank tape trop doucement face aux soins), Tank bat Infighter (son armure absorbe le corps-à-corps). Le type fort inflige × 1,5 dégâts au type qu'il bat (arrondi à l'entier inférieur). Exemple : l'InfighterBot fait 16 + 14 − 8 = 22 dégâts à un HealerBot, soit 33 avec le bonus.

**IA ennemie :** pour le POC, l'ennemi choisit une compétence au hasard parmi les siennes (générateur aléatoire injecté pour les tests). Une IA par type, plus offensive selon la barre de recherche, est un bonus (COULD).

**Fin du combat :**

- Robot ennemi vaincu : écran de choix, reprogrammer ou achever.
- Exosquelette vaincu : le pilote est neutralisé, sans choix.
- Toute l'équipe K.O. : retour à la Home.

Les dégâts sont déterministes : la variation aléatoire de ± 10 % est retirée du POC (WON'T).

**Animations (COULD)** : sprite touché qui tremble, flash blanc, barre de PV qui descend (`Timeline` JavaFX). Le combat doit d'abord fonctionner sans elles.

## Reprogrammer ou achever

Achever, c'est sacrifier sa discrétion maintenant pour réparer son équipe tout de suite et avoir des recrues plus fortes plus tard : c'est tout le dilemme du jeu. C'est **la scène phare** du POC, à montrer en direct à la soutenance.

| | Reprogrammer | Achever |
| --- | --- | --- |
| Effet immédiat | Le robot rejoint l'équipe avec son niveau (réussite garantie) | Le robot est détruit |
| Pièces | Rien | Pièces de réparation, d'autant plus que le robot est de haut niveau (proposition : 1 + niveau / 3) |
| Barre de recherche | − 5 | + 15 |
| Réseau | Rien | Compte pour le bonus du réseau |
| Si l'équipe est pleine | Le robot part à la Home | Toujours possible |

**L'écran de choix (MUST) :**

- Le texte raconte la vie du robot qui défile dans ses yeux : un souvenir des personnes qu'il a aidées, propre à son type (ex. HealerBot : « a soigné une petite fille au quartier résidentiel »). Les souvenirs sont dans un fichier texte et tirés au hasard.
- Le robot **glitche** en boucle tant que le joueur n'a pas choisi : teinte rouge et ligne hostile (« ÉLIMINER. CIBLE. »), puis teinte bleue et ligne bienveillante (« …je voulais juste aider… »).
- L'écran réutilise le décor du combat, avec une surcouche.

**Après le choix (SHOULD) :**

- Survoler un bouton affiche ses conséquences (`+15 recherche · +2 pièces · réseau +1`).
- **Achever** : image du robot étalé au sol, pièces récupérées et temps de réparation gagné, puis en rouge : « Le réseau NEXUS a enregistré sa défaite… ».
- **Reprogrammer** : Léon près du robot, un PC affichant des lignes de code vertes.

**XP des robots du joueur (SHOULD)** (proposition) :

- Chaque victoire donne 10 × le niveau de l'ennemi en XP au robot actif.
- Pour passer au niveau suivant, il faut 50 × le niveau actuel en XP.

**Niveau des ennemis :**

```
niveau ennemi = niveau de la zone + min(5, robotsAcheves / 3)   // division entière
```

Le bonus du réseau monte de 1 tous les 3 robots achevés, plafonné à + 5 pour que le jeu ne devienne jamais impossible. Un robot reprogrammé garde ce niveau : c'est la récompense de ceux qui achèvent beaucoup.

Pour le POC, le compteur du réseau est tenu et affiché ; son effet réel sur le niveau des ennemis est un bonus (COULD).

## Barre de recherche

Une jauge de 0 à 100 qui mesure à quel point le groupe radical traque le joueur. La valeur, son affichage dans le HUD et ce qui la fait bouger sont **MUST** ; les effets des paliers sont **SHOULD**.

| Palier | Valeur | Effet sur le jeu |
| --- | --- | --- |
| Inconnu | 0 – 24 | Rencontres normales, on peut éviter certains robots |
| Repéré | 25 – 49 | Plus de rencontres sur les routes |
| Traqué | 50 – 74 | Ennemis plus agressifs (IA offensive, COULD) |
| Ennemi public | 75 – 100 | Des exosquelettes patrouillent, l'entrée discrète du data center est fermée |

**Ce qui la fait bouger** (proposition) : achever un robot + 15, reprogrammer − 5, neutraliser un exosquelette + 10.

**Entrée dans le data center :**

- **Sous 75** : par l'entrée discrète, avec moins de gardes avant le boss.
- **À 75 ou plus** : par l'entrée principale, avec plus de combats avant le boss.

Les deux chemins mènent au boss : aucun style de jeu n'est bloqué.

## Le monde

Une **Home** et **trois zones**. Chaque zone de combat est gardée par un exosquelette : le battre allume le portail suivant.

| # | Zone (noms provisoires) | Niveau de la zone | Robots dominants | Chef de zone |
| --- | --- | --- | --- | --- |
| 0 | Home | — | Les robots du joueur | — |
| 1 | Quartier résidentiel | 2 | HealerBot | Exosquelette niveau 4 |
| 2 | Zone industrielle | 5 | TankBot et InfighterBot | Exosquelette niveau 7 |
| 3 | Data center | 8 (proposition) | Tous les types | Le chef de NEXUS (niveau fixe) |

**La Home** remplace les bornes de recharge et l'écran Équipe :

- Elle soigne l'équipe et sert de point de retour après une défaite.
- Tous les robots reprogrammés y sont stockés ; le joueur y choisit son équipe de 3.
- Les robots K.O. y sont en réparation.

**Les portails** : depuis la Home, une porte-tunnel avec un halo de lumière par zone, comme dans Pokémon Noir et Blanc. Halo éteint : zone verrouillée. Il s'allume quand le chef de la zone précédente est battu.

La carte est une grille de tuiles (ex. 20 × 15 cases de 32 px) décrite dans un fichier texte par zone : modifier une carte ne demande pas de toucher au code. Un portail est une tuile spéciale du fichier (ex. `1`, `2`, `3`) qui charge la carte correspondante.

## Exosquelettes et boss final

Les ennemis spéciaux ne suivent pas le niveau du réseau, et on ne peut jamais les reprogrammer.

**Exosquelettes (chefs des zones 1 et 2)**

- Pilotés par des membres du groupe radical, donc humains.
- Niveau fixe par zone, un peu au-dessus des robots de la zone.
- Une fois vaincu, le pilote est neutralisé : pas de choix, barre de recherche + 10.
- Compétence propre : Surcharge, puissance 18, une fois par combat.

**Boss final : le chef de NEXUS**

- Le **chef du groupe radical**, dans son exosquelette, au cœur du data center. Un seul combat. Niveau fixe 10 (proposition).
- Une fois le chef vaincu, le data center est détruit : fin du jeu, tous les robots redeviennent bienveillants.
- Les **3 gardiens** du data center (un par type) n'existent pas en jeu pour le POC (WON'T) : ils apparaissent seulement dans la vidéo de fin.

Côté code, `Boss` redéfinit `getLevel()` pour renvoyer son niveau fixe (polymorphisme).

## Interface

Le HUD d'exploration affiche toujours l'équipe et la barre de recherche.

| Écran | Ce qu'on y voit | On y arrive depuis |
| --- | --- | --- |
| Menu principal | Nouvelle partie, charger la partie démo, quitter | Lancement du jeu |
| Exploration | Carte de la zone ou de la Home, joueur + HUD (équipe, PV, barre de recherche, zone) | Menu, fin de combat, portail |
| Combat | Le robot du joueur de dos, l'ennemi de face, PV et niveaux, boutons d'attaque, changer | Une rencontre |
| Choix | Le robot vaincu qui glitche, son souvenir, 2 boutons Reprogrammer / Achever | Victoire contre un robot |
| Équipe (à la Home) | Les robots stockés, l'équipe de 3, stats, XP, compétences, réparations en cours, pièces | Interaction à la Home |
| Fin (SHOULD) | Résumé : robots reprogrammés, achevés, barre finale | Défaite du chef |

Graphismes : sprites générés par IA, avec un prompt de style unique réutilisé pour tous les assets et une taille fixe (tuiles de 32 px). Liste figée : 3 robots × 2 vues (dos et face), exosquelette, chef de NEXUS, Léon, tuiles de chaque zone, portail. Un sprite manquant s'affiche comme un carré de couleur, jamais comme une erreur.

**Sauvegarde démo (MUST)** : un fichier préparé (équipe de 3, barre de recherche vers 70, Léon devant le portail du data center) chargé depuis le menu. Il permet de démarrer la soutenance sur une partie avancée et de relancer en quelques secondes en cas de crash.

## Architecture visée (pour l'étape architecture BMAD)

- `Combattant` (abstraite) → `Robot` (abstraite) → `TankBot`, `HealerBot`, `InfighterBot`, `Boss` ; `Combattant` → `Exosquelette`.
- **State** : `RobotState` (Détourné / Allié / En réparation) — un ennemi et un allié sont la même classe.
- **Strategy** : `CombatStrategy`, IA de combat ; aléatoire pour le POC, par type et selon la barre de recherche en bonus.
- **Factory** : `RobotFactory` crée les rencontres d'une zone (niveau = zone + bonus réseau).
- **Observer** : `RobotKilledEvent` → `DataCenter` (niveau du réseau), `WantedBar` et le stock de pièces.
- **Composition** : `Player` possède une `Team` de robots ; la `Home` stocke les autres.
- Séparation modèle / vue JavaFX pour tester la logique sans lancer l'interface. La frontière entre la carte et le combat est une interface (ex. `CombatLauncher`) définie dès le premier jour.

## Périmètre du POC (3 semaines)

Issu du brainstorming du 2026-10-02.

- **MUST** (le socle, la démo tourne quoi qu'il arrive) : Home + 3 zones + portails ; combat 1 contre 1, 3 types, plusieurs attaques, faiblesses ; changer de robot ; écran de choix (souvenirs + glitch) ; barre de recherche (valeur, HUD, + 15 / − 5 / + 10) ; équipe de 3, stockage, soin et retour à la Home ; pièces et réparation ; exosquelettes des zones 1 et 2 et combat contre le chef de NEXUS ; sauvegarde démo ; sprites IA statiques ; menu principal minimal ; UML, tests JUnit, couverture JaCoCo, Javadoc.
- **SHOULD** : XP et niveaux ; effets des 4 paliers de la barre ; aperçu des conséquences au survol ; images après le choix ; écran de fin ; vidéo de fin avec les gardiens.
- **COULD** : animations de combat ; IA ennemie par Strategy, plus offensive selon la barre ; effet réel du niveau du réseau.
- **WON'T** (pas cette fois) : fuite, variation aléatoire de ± 10 %, gardiens jouables, quatrième zone de combat.

**Déjà tranché :**

- Héros : **Léon**. Titre : **Reboot**. Ville : **Néo-Lyon**. Groupe radical : **NEXUS** (Network for Extreme Unity and Suppression).
- 3 types de robots, la sainte trinité (TankBot, HealerBot, InfighterBot), avec faiblesses entre types (× 1,5).
- Le monde : une Home et 3 zones (résidentiel, industriel, data center), reliées par des portails.
- Boss final : le chef de NEXUS dans un exosquelette, en un seul combat. Les gardiens n'apparaissent que dans la vidéo de fin.
- Achever rapporte des pièces de réparation et compte pour le bonus du réseau (remplace l'ancienne décision « pas de pièces »).
- La reprogrammation réussit toujours.
