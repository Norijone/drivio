import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Force la sortie console en UTF-8 pour un affichage correct des accents.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("=============================================");
        System.out.println("   DRIVIO - Gestion de flotte de véhicules  ");
        System.out.println("   de livraison");
        System.out.println("=============================================\n");

        LecteurCSV lecteurCSV = new LecteurCSV();
        List<String> erreursChargement = new ArrayList<>();
        List<Vehicule> vehicules;

        try {
            vehicules = lecteurCSV.chargerVehicules("data/vehicules.csv", erreursChargement);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier CSV : " + e.getMessage());
            return;
        }

        System.out.println("Véhicules chargés avec succès : " + vehicules.size());
        if (!erreursChargement.isEmpty()) {
            System.out.println("\nLignes ignorées lors du chargement :");
            erreursChargement.forEach(err -> System.out.println("  - " + err));
        }

        GestionnaireFlotte gestionnaire = new GestionnaireFlotte(vehicules);

        System.out.println("\n--- Liste des véhicules ---");
        for (Vehicule v : vehicules) {
            System.out.println(v);
        }

        // ---- Démonstration des opérations de location ----
        System.out.println("\n--- Démonstration : location / retour / entretien ---");

        try {
            Vehicule v1 = gestionnaire.trouverParId("V001");
            if (v1 != null) {
                gestionnaire.louer(v1, "Karim", 3);
                System.out.println("Location réussie : " + v1.getId() + " -> tarif = "
                        + String.format("%.2f$", v1.calculerTarif(3)));
                gestionnaire.retourner(v1, v1.getKilometrage() + 350);
                System.out.println("Retour enregistré : " + v1.getId() + " -> " + v1.getKilometrage() + " km");
            }
        } catch (VehiculeIndisponibleException | KilometrageInvalideException e) {
            System.out.println("Erreur de location : " + e.getMessage());
        }

        // Démonstration de la gestion des erreurs : location d'un véhicule déjà loué
        try {
            Vehicule v2 = gestionnaire.trouverParId("V002");
            if (v2 != null) {
                gestionnaire.louer(v2, "Sami", 2);
                gestionnaire.louer(v2, "Autre chauffeur", 1); // devrait échouer
            }
        } catch (VehiculeIndisponibleException e) {
            System.out.println("Erreur attendue capturée : " + e.getMessage());
        }

        Vehicule vEntretien = gestionnaire.trouverParId("V003");
        if (vEntretien != null) {
            vEntretien.signalerEntretien("Bruit suspect au freinage");
            vEntretien.planifierEntretien("2026-08-10");
            System.out.println("Entretien signalé et planifié pour " + vEntretien.getId());
        }

        // ---- Statistiques ----
        System.out.println("\n--- Statistiques de la flotte ---");
        StatistiquesFlotte stats = new StatistiquesFlotte(vehicules);
        System.out.printf("Revenu total généré : %.2f$%n", stats.revenuTotal());
        System.out.printf("Kilométrage moyen : %.1f km%n", stats.kilometrageMoyen());

        System.out.println("Taux d'utilisation par type (jours loués / véhicule) :");
        for (Map.Entry<String, Double> entry : stats.tauxUtilisationParType().entrySet()) {
            System.out.printf("  - %s : %.1f jours%n", entry.getKey(), entry.getValue());
        }

        System.out.println("Répartition par zone :");
        for (Map.Entry<String, Long> entry : stats.repartitionParZone().entrySet()) {
            System.out.println("  - " + entry.getKey() + " : " + entry.getValue() + " véhicule(s)");
        }

        System.out.println("Véhicules les plus utilisés :");
        for (Vehicule v : stats.vehiculesLesPlusUtilises(3)) {
            System.out.println("  - " + v.getId() + " (" + v.getModele() + ") : " + v.getNombreLocations() + " location(s)");
        }

        System.out.println("Véhicules nécessitant un entretien :");
        List<Vehicule> aEntretenir = stats.vehiculesNecessitantEntretien();
        if (aEntretenir.isEmpty()) {
            System.out.println("  Aucun.");
        } else {
            aEntretenir.forEach(v -> System.out.println("  - " + v.getId() + " (" + v.getModele() + ")"));
        }

        // ---- Alertes ----
        System.out.println("\n--- Alertes actives ---");
        GestionnaireAlertes alertes = new GestionnaireAlertes(vehicules);
        alertes.genererAlertes().forEach(a -> System.out.println("  ! " + a));

        // ---- Génération du rapport ----
        String rapport = RapportGenerateur.genererContenu(vehicules, stats, alertes);
        try {
            lecteurCSV.ecrireRapport("data/rapport_flotte.txt", rapport);
            System.out.println("\nRapport généré : data/rapport_flotte.txt");
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture du rapport : " + e.getMessage());
        }
    }
}
