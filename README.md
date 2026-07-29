# Drivio — Gestion de flotte de véhicules de livraison

Projet réalisé dans le cadre du cours **Programmation Avancée** (Soumaya Elhosni),
basé sur le sujet "Projet 3 : Gestion d'une flotte de véhicules".

## Membre(s)

- Yacine

## Contexte

Drivio est une application de gestion de flotte pensée pour les entreprises de
livraison. Elle permet de cataloguer les véhicules, gérer les locations et
retours, suivre l'entretien, générer des alertes et produire des statistiques
sur l'utilisation de la flotte.

## Fonctionnalités développées

- Chargement des données initiales depuis un fichier CSV (`data/vehicules.csv`),
  avec validation et gestion des lignes invalides
- Hiérarchie de véhicules (héritage + classe abstraite) :
  `Vehicule` → `CamionnetteUtilitaire`, `Minivan`, `CamionCube`
- Interfaces `GestionLocation` et `Entretenable`
- Redéfinition de méthode : `calculerTarif()` propre à chaque type de véhicule
- Exceptions personnalisées : `VehiculeIndisponibleException`,
  `KilometrageInvalideException`, `DonneeInvalideException`
- **Interface graphique (Swing)** : ajout, modification et retrait de véhicules,
  location et retour (mise à jour du statut et de l'historique), signalement
  d'entretien — avec affichage des erreurs de validation dans des boîtes de
  dialogue
- Location, retour et renouvellement de location avec règles métier
- Signalement et planification d'entretien
- Statistiques : revenu total, kilométrage moyen, taux d'utilisation par type,
  véhicules les plus utilisés, véhicules nécessitant un entretien, répartition
  par zone (Rive-Nord, Rive-Sud, Montréal)
- Génération d'alertes (entretien à venir, panne signalée)
- Génération d'un rapport texte (`data/rapport_flotte.txt`)
- Persistance : toute modification via la GUI (ajout/modif/retrait/location/
  retour) réécrit `data/vehicules.csv`

## Principes SOLID appliqués

- **SRP** : chaque classe a une seule responsabilité — `LecteurCSV` (lecture/
  écriture), `VehiculeFactory` (création/validation), `GestionnaireFlotte`
  (opérations de location), `StatistiquesFlotte` (analyse),
  `GestionnaireAlertes` (alertes), `RapportGenerateur` (mise en forme du rapport)
- **OCP** : ajouter un nouveau type de véhicule ne nécessite qu'une nouvelle
  sous-classe de `Vehicule` implémentant `calculerTarif()`/`getCodeCsv()` et un
  nouveau cas dans `VehiculeFactory` — aucun code existant (CSV, GUI,
  statistiques) n'a besoin d'être modifié

## Structure du projet

```
drivio/
├── src/            # Code source Java
├── data/           # Fichier CSV d'entrée + rapport généré
├── README.md
└── contributions.txt
```

## Exécution

Version console (démo automatique) :
```bash
javac -encoding UTF-8 -d bin src/*.java
java -Dfile.encoding=UTF-8 -cp bin Main
```

Version graphique (interactive) :
```bash
javac -encoding UTF-8 -d bin src/*.java
java -Dfile.encoding=UTF-8 -cp bin DrivioGUI
```

## Roadmap (hors barème du cours)

Idées pour une future version étendue de Drivio : interface graphique,
géolocalisation GPS en temps réel, gestion complète des employés/permis de
conduire, génération automatisée de documents d'assurance, calendrier
d'entretien interactif, tableau de bord visuel avec code couleur.
