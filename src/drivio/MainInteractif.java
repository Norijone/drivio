package drivio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import drivio.modeles.Vehicule;
import drivio.gestion.GestionnaireFlotte;
import drivio.gestion.StatistiquesFlotte;
import drivio.gestion.GestionnaireAlertes;
import drivio.utilitaires.GestionnaireFichierCSV;
import drivio.utilitaires.ConstructeurVehicule;
import drivio.utilitaires.CatalogueModeles;
import drivio.utilitaires.GenerateurRapport;
import drivio.exceptions.DonneeInvalideException;
import drivio.exceptions.VehiculeIndisponibleException;
import drivio.exceptions.KilometrageInvalideException;

public class MainInteractif {

    private static final String CHEMIN_CSV = "data/vehicules.csv";
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final GestionnaireFichierCSV GESTIONNAIRE_CSV = new GestionnaireFichierCSV();
    private static GestionnaireFlotte gestionnaire;

    public static void main(String[] args) throws IOException {

        ArrayList<String> erreurs = new ArrayList<>();
        ArrayList<Vehicule> vehicules = GESTIONNAIRE_CSV.chargerVehicules(CHEMIN_CSV, erreurs);
        gestionnaire = new GestionnaireFlotte(vehicules);

        System.out.println("=============================================");
        System.out.println("   DRIVIO - Mode interactif (console)");
        System.out.println("=============================================");
        System.out.println(vehicules.size() + " vehicule(s) charge(s).");

        if (!erreurs.isEmpty()) {
            System.out.println("\n--- Erreurs detectees dans le fichier CSV ---");
            for (int i = 0; i < erreurs.size(); i++) {
                System.out.println("  ! " + erreurs.get(i));
            }
        }

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = SCANNER.nextLine().trim();

            if (choix.equals("1")) {
                afficherVehicules();
            } else if (choix.equals("2")) {
                ajouterVehicule();
            } else if (choix.equals("3")) {
                modifierVehicule();
            } else if (choix.equals("4")) {
                retirerVehicule();
            } else if (choix.equals("5")) {
                louerVehicule();
            } else if (choix.equals("6")) {
                retournerVehicule();
            } else if (choix.equals("7")) {
                signalerEntretien();
            } else if (choix.equals("8")) {
                afficherStatistiques();
            } else if (choix.equals("9")) {
                afficherAlertes();
            } else if (choix.equals("10")) {
                genererRapport();
            } else if (choix.equals("0")) {
                continuer = false;
            } else {
                System.out.println("Choix invalide, reessaie.");
            }
        }
        System.out.println("Fermeture de Drivio. A bientot !");
    }

    private static void afficherMenu() {
        System.out.println("\n--- MENU DRIVIO ---");
        System.out.println("1. Afficher les vehicules");
        System.out.println("2. Ajouter un vehicule");
        System.out.println("3. Modifier un vehicule");
        System.out.println("4. Retirer un vehicule");
        System.out.println("5. Louer un vehicule");
        System.out.println("6. Retourner un vehicule");
        System.out.println("7. Signaler un entretien");
        System.out.println("8. Statistiques");
        System.out.println("9. Alertes");
        System.out.println("10. Generer le rapport");
        System.out.println("0. Quitter");
        System.out.print("Choix : ");
    }

    private static String demander(String message) {
        System.out.print(message);
        return SCANNER.nextLine().trim();
    }

    private static void sauvegarder() {
        try {
            GESTIONNAIRE_CSV.ecrireVehicules(CHEMIN_CSV, gestionnaire.getFlotte());
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du CSV : " + e.getMessage());
        }
    }

    private static void afficherVehicules() {
        if (gestionnaire.getFlotte().isEmpty()) {
            System.out.println("Aucun vehicule dans la flotte.");
            return;
        }
        for (int i = 0; i < gestionnaire.getFlotte().size(); i++) {
            System.out.println(gestionnaire.getFlotte().get(i));
        }
    }

    private static void ajouterVehicule() {
        System.out.println("\n-- Ajouter un vehicule --");

        ArrayList<String> typesVehicule = new ArrayList<>();
        typesVehicule.add("CAMIONNETTE");
        typesVehicule.add("MINIVAN");
        typesVehicule.add("CUBE");
        String type = demanderChoixParmi("Type de vehicule :", typesVehicule);

        String modele = demanderChoixParmi("Modele :", CatalogueModeles.getModeles(type));

        ArrayList<String> typesEnergie = new ArrayList<>();
        typesEnergie.add("ESSENCE");
        typesEnergie.add("DIESEL");
        typesEnergie.add("ELECTRIQUE");
        String energie = demanderChoixParmi("Energie :", typesEnergie);

        String capacite = demander("Capacite (kg) : ");
        String km = demander("Kilometrage initial : ");

        ArrayList<String> zones = new ArrayList<>();
        zones.add("MONTREAL");
        zones.add("RIVE_NORD");
        zones.add("RIVE_SUD");
        String zone = demanderChoixParmi("Zone :", zones);

        String chauffeur = demander("Chauffeur assigne (laisser vide si aucun) : ");
        String tarif = demander("Tarif de base ($) : ");

        String id = gestionnaire.genererProchainId();

        try {
            Vehicule nouveau = ConstructeurVehicule.creerVehicule(
                    id, modele, type, energie, capacite, km, "DISPONIBLE", zone, chauffeur, tarif);
            gestionnaire.ajouterVehicule(nouveau);
            sauvegarder();
            System.out.println("Vehicule " + nouveau.getId() + " ajoute avec succes.");
        } catch (DonneeInvalideException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
        }
    }

    private static String demanderChoixParmi(String titre, ArrayList<String> options) {
        System.out.println(titre);
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + options.get(i));
        }
        while (true) {
            String choix = demander("Choix (1-" + options.size() + ") : ");
            try {
                int index = Integer.parseInt(choix.trim());
                if (index >= 1 && index <= options.size()) {
                    return options.get(index - 1);
                }
            } catch (NumberFormatException e) {
            }
            System.out.println("Choix invalide, reessaie.");
        }
    }

    private static void modifierVehicule() {
        System.out.println("\n-- Modifier un vehicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) {
            return;
        }

        System.out.println("Laisse vide pour garder la valeur actuelle.");
        String modele = demander("Modele [" + v.getModele() + "] : ");
        String energie = demander("Energie [" + v.getTypeEnergie() + "] : ");
        String capacite = demander("Capacite (kg) [" + v.getCapaciteKg() + "] : ");
        String km = demander("Kilometrage [" + v.getKilometrage() + "] : ");
        String zone = demander("Zone [" + v.getZone() + "] : ");
        String chauffeurActuel = v.getChauffeurAssigne() == null ? "" : v.getChauffeurAssigne();
        String chauffeur = demander("Chauffeur [" + chauffeurActuel + "] : ");
        String tarif = demander("Tarif de base [" + v.getTarifBase() + "] : ");

        try {
            Vehicule valide = ConstructeurVehicule.creerVehicule(
                    v.getId(),
                    modele.isBlank() ? v.getModele() : modele,
                    v.getCodeCsv(),
                    energie.isBlank() ? v.getTypeEnergie() : energie,
                    capacite.isBlank() ? String.valueOf(v.getCapaciteKg()) : capacite,
                    km.isBlank() ? String.valueOf(v.getKilometrage()) : km,
                    v.getStatut(),
                    zone.isBlank() ? v.getZone() : zone,
                    chauffeur.isBlank() ? v.getChauffeurAssigne() : chauffeur,
                    tarif.isBlank() ? String.valueOf(v.getTarifBase()) : tarif);

            v.setModele(valide.getModele());
            v.setTypeEnergie(valide.getTypeEnergie());
            v.setCapaciteKg(valide.getCapaciteKg());
            v.setKilometrage(valide.getKilometrage());
            v.setZone(valide.getZone());
            v.setChauffeurAssigne(valide.getChauffeurAssigne());
            v.setTarifBase(valide.getTarifBase());

            sauvegarder();
            System.out.println("Vehicule " + v.getId() + " modifie avec succes.");
        } catch (DonneeInvalideException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
        }
    }

    private static void retirerVehicule() {
        System.out.println("\n-- Retirer un vehicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) {
            return;
        }

        String confirmation = demander("Confirmer le retrait de " + v.getId() + " ? (o/n) : ");
        if (!confirmation.equalsIgnoreCase("o")) {
            System.out.println("Retrait annule.");
            return;
        }
        gestionnaire.retirerVehicule(v.getId());
        sauvegarder();
        System.out.println("Vehicule " + v.getId() + " retire.");
    }

    private static void louerVehicule() {
        System.out.println("\n-- Louer un vehicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) {
            return;
        }

        String chauffeur = demander("Chauffeur : ");
        String joursStr = demander("Nombre de jours : ");
        try {
            int jours = Integer.parseInt(joursStr.trim());
            gestionnaire.louer(v, chauffeur, jours);
            sauvegarder();
            System.out.println("Vehicule " + v.getId() + " loue a " + chauffeur + " pour " + jours
                    + " jour(s) - tarif : " + v.calculerTarif(jours) + "$");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le nombre de jours doit etre un entier.");
        } catch (VehiculeIndisponibleException e) {
            System.out.println("Location impossible : " + e.getMessage());
        }
    }

    private static void retournerVehicule() {
        System.out.println("\n-- Retourner un vehicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) {
            return;
        }

        String kmStr = demander("Kilometrage affiche a l'odometre du vehicule [actuel : "
                + v.getKilometrage() + " km] : ");
        try {
            int nouveauKm = Integer.parseInt(kmStr.trim());
            gestionnaire.retourner(v, nouveauKm);
            sauvegarder();
            System.out.println("Vehicule " + v.getId() + " retourne - statut : DISPONIBLE, kilometrage : "
                    + v.getKilometrage() + " km.");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le kilometrage doit etre un entier.");
        } catch (KilometrageInvalideException e) {
            System.out.println("Retour impossible : " + e.getMessage());
        } catch (VehiculeIndisponibleException e) {
            System.out.println("Retour impossible : " + e.getMessage());
        }
    }

    private static void signalerEntretien() {
        System.out.println("\n-- Signaler un entretien --");
        Vehicule v = trouverParIdDemande();
        if (v == null) {
            return;
        }

        String description = demander("Description du probleme : ");
        v.signalerEntretien(description);
        v.setStatut("EN_ENTRETIEN");
        sauvegarder();
        System.out.println("Entretien signale pour " + v.getId() + ".");
    }

    private static void afficherStatistiques() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        System.out.println("\n--- Statistiques ---");
        System.out.println("Revenu total genere : " + stats.revenuTotal() + "$");
        System.out.println("Kilometrage moyen : " + stats.kilometrageMoyen() + " km");

        System.out.println("Taux d'utilisation par type :");
        ArrayList<String> types = stats.getTypesUniques();
        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            System.out.println("  " + type + " : " + stats.tauxUtilisationPourType(type) + " jours");
        }

        System.out.println("Repartition par zone :");
        ArrayList<String> zones = stats.getZonesUniques();
        for (int i = 0; i < zones.size(); i++) {
            String zone = zones.get(i);
            System.out.println("  " + zone + " : " + stats.nombreVehiculesDansZone(zone) + " vehicule(s)");
        }

        System.out.println("Vehicules necessitant un entretien :");
        ArrayList<Vehicule> aEntretenir = stats.vehiculesNecessitantEntretien();
        if (aEntretenir.isEmpty()) {
            System.out.println("  Aucun.");
        } else {
            for (int i = 0; i < aEntretenir.size(); i++) {
                Vehicule v = aEntretenir.get(i);
                System.out.println("  " + v.getId() + " (" + v.getModele() + ")");
            }
        }
    }

    private static void afficherAlertes() {
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        System.out.println("\n--- Alertes actives ---");
        ArrayList<String> liste = alertes.genererAlertes();
        for (int i = 0; i < liste.size(); i++) {
            System.out.println("  ! " + liste.get(i));
        }
    }

    private static void genererRapport() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        String contenu = GenerateurRapport.genererContenu(gestionnaire.getFlotte(), stats, alertes);
        try {
            GESTIONNAIRE_CSV.ecrireRapport("data/rapport_flotte.txt", contenu);
            System.out.println("Rapport genere : data/rapport_flotte.txt");
        } catch (IOException e) {
            System.out.println("Erreur lors de la generation du rapport : " + e.getMessage());
        }
    }

    private static Vehicule trouverParIdDemande() {
        String id = demander("ID du vehicule : ");
        Vehicule v = gestionnaire.trouverParId(id);
        if (v == null) {
            System.out.println("Aucun vehicule trouve avec l'id \"" + id + "\".");
        }
        return v;
    }
}
