# Drivio

Projet réalisé dans le cadre du cours Programmation Avancée, basé sur le sujet "Projet 3 : Gestion d'une flotte de véhicules".

## Membre(s)

* Yacine
* Kousseila

## Contexte

Drivio est une petite application de gestion de flotte pensée pour une entreprise de livraison. Elle permet de cataloguer les véhicules, gérer les locations et les retours, suivre l'entretien, générer des alertes et sortir quelques statistiques sur l'utilisation de la flotte, le tout depuis un menu en mode console.

## Structure du projet

* `drivio` — point d'entrée du programme (`MainInteractif`, le menu interactif)
* `drivio.modeles` — les véhicules : `Vehicule` (classe abstraite) et ses sous-classes `CamionnetteUtilitaire`, `Minivan`, `CamionCube`, plus l'interface `Entretenable`
* `drivio.gestion` — la logique de gestion de la flotte : `GestionnaireFlotte`, `StatistiquesFlotte`, `GestionnaireAlertes`, `ComparateurUtilisation`, et l'interface `GestionLocation`
* `drivio.utilitaires` — les outils autour des véhicules : `ConstructeurVehicule` (construit et valide un véhicule à partir de texte), `GestionnaireFichierCSV` (lecture et écriture du fichier CSV), `CatalogueModeles` (liste des modèles disponibles par type), `GenerateurRapport`
* `drivio.exceptions` — les exceptions propres au projet : `VehiculeIndisponibleException`, `KilometrageInvalideException`, `DonneeInvalideException`
* `data/` — le fichier `vehicules.csv` (données de la flotte) et le rapport généré `rapport_flotte.txt`

## Fonctionnalités développées

* Chargement des véhicules depuis `data/vehicules.csv` au démarrage, avec validation ligne par ligne : les lignes invalides sont ignorées et toutes les erreurs trouvées sont affichées directement au lancement du programme
* Hiérarchie de véhicules par héritage : `Vehicule` (classe abstraite) → `CamionnetteUtilitaire`, `Minivan`, `CamionCube`
* Interfaces `Entretenable` (signaler/planifier un entretien) et `GestionLocation` (louer/retourner/renouveler)
* Redéfinition de méthode : `calculerTarif()` propre à chaque type de véhicule (rabais électrique pour les camionnettes, coefficient réduit pour les minivans, surcharge selon la capacité pour les camions cube)
* Exceptions personnalisées : `VehiculeIndisponibleException`, `KilometrageInvalideException`, `DonneeInvalideException`
* Menu console interactif avec 10 options :
  1. Afficher les véhicules
  2. Ajouter un véhicule (choix guidé par menu numéroté, ID généré automatiquement au format V001, V002...)
  3. Modifier un véhicule
  4. Retirer un véhicule (avec confirmation)
  5. Louer un véhicule
  6. Retourner un véhicule (par lecture directe de l'odomètre, pas un delta de kilomètres)
  7. Signaler un entretien
  8. Statistiques
  9. Alertes
  10. Générer le rapport
* Location, retour et renouvellement de location avec quelques règles de base (véhicule disponible, kilométrage cohérent, nombre de jours positif)
* Statistiques : revenu total, kilométrage moyen, taux d'utilisation par type de véhicule, véhicules les plus utilisés, véhicules qui approchent d'un entretien, répartition de la flotte par zone (Montréal, Rive-Nord, Rive-Sud)
* Génération d'alertes simples (entretien à venir, panne signalée)
* Génération d'un rapport texte dans `data/rapport_flotte.txt`
* Sauvegarde automatique dans `data/vehicules.csv` après chaque ajout, modification, retrait, location ou retour

## Roadmap (hors barème du cours)

Des idées pour continuer le projet plus tard, si on a le temps : une vraie interface graphique, la géolocalisation des véhicules en temps réel, une gestion complète des employés et de leurs permis de conduire, un calendrier d'entretien interactif, et un petit tableau de bord visuel avec des couleurs pour repérer les alertes plus vite.
