import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Version console interactive de Drivio (menu texte).
 * Permet d'ajouter, modifier, retirer, louer et retourner des véhicules
 * sans interface graphique, en réutilisant exactement la même logique
 * métier que la GUI (VehiculeFactory, GestionnaireFlotte, etc.).
 */
public class MainInteractif {

    private static final String CHEMIN_CSV = "data/vehicules.csv";
    private static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8);
    private static final LecteurCSV LECTEUR_CSV = new LecteurCSV();
    private static GestionnaireFlotte gestionnaire;

    public static void main(String[] args) throws IOException {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        List<String> erreurs = new ArrayList<>();
        List<Vehicule> vehicules = LECTEUR_CSV.chargerVehicules(CHEMIN_CSV, erreurs);
        gestionnaire = new GestionnaireFlotte(vehicules);
        // Les lignes CSV invalides sont ignorées silencieusement au démarrage :
        // les erreurs ne sont affichées que lors de l'ajout d'un véhicule par l'utilisateur.

        System.out.println("=============================================");
        System.out.println("   DRIVIO - Mode interactif (console)");
        System.out.println("=============================================");
        System.out.println(vehicules.size() + " véhicule(s) chargé(s).");

        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = SCANNER.nextLine().trim();
            switch (choix) {
                case "1" -> afficherVehicules();
                case "2" -> ajouterVehicule();
                case "3" -> modifierVehicule();
                case "4" -> retirerVehicule();
                case "5" -> louerVehicule();
                case "6" -> retournerVehicule();
                case "7" -> signalerEntretien();
                case "8" -> afficherStatistiques();
                case "9" -> afficherAlertes();
                case "10" -> genererRapport();
                case "0" -> continuer = false;
                default -> System.out.println("Choix invalide, réessaie.");
            }
        }
        System.out.println("Fermeture de Drivio. À bientôt !");
    }

    private static void afficherMenu() {
        System.out.println("\n--- MENU DRIVIO ---");
        System.out.println("1. Afficher les véhicules");
        System.out.println("2. Ajouter un véhicule");
        System.out.println("3. Modifier un véhicule");
        System.out.println("4. Retirer un véhicule");
        System.out.println("5. Louer un véhicule");
        System.out.println("6. Retourner un véhicule");
        System.out.println("7. Signaler un entretien");
        System.out.println("8. Statistiques");
        System.out.println("9. Alertes");
        System.out.println("10. Générer le rapport");
        System.out.println("0. Quitter");
        System.out.print("Choix : ");
    }

    private static String demander(String message) {
        System.out.print(message);
        return SCANNER.nextLine().trim();
    }

    private static void sauvegarder() {
        try {
            LECTEUR_CSV.ecrireVehicules(CHEMIN_CSV, gestionnaire.getFlotte());
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde du CSV : " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------

    private static void afficherVehicules() {
        if (gestionnaire.getFlotte().isEmpty()) {
            System.out.println("Aucun véhicule dans la flotte.");
            return;
        }
        for (Vehicule v : gestionnaire.getFlotte()) {
            System.out.println(v);
        }
    }

    private static void ajouterVehicule() {
        System.out.println("\n-- Ajouter un véhicule --");

        String type = demanderChoixParmi("Type de véhicule :", List.of("CAMIONNETTE", "MINIVAN", "CUBE"));
        String modele = demanderChoixParmi("Modèle :", CatalogueModeles.MODELES_PAR_TYPE.get(type));
        String energie = demanderChoixParmi("Énergie :", List.of("ESSENCE", "DIESEL", "ELECTRIQUE"));
        String capacite = demander("Capacité (kg) : ");
        String km = demander("Kilométrage initial : ");
        String zone = demanderChoixParmi("Zone :", List.of("MONTREAL", "RIVE_NORD", "RIVE_SUD"));
        String chauffeur = demander("Chauffeur assigné (laisser vide si aucun) : ");
        String tarif = demander("Tarif de base ($) : ");

        String id = gestionnaire.genererProchainId();

        try {
            Vehicule nouveau = VehiculeFactory.creerVehicule(
                    id, modele, type, energie, capacite, km, "DISPONIBLE", zone, chauffeur, tarif);
            gestionnaire.ajouterVehicule(nouveau);
            sauvegarder();
            System.out.println("Véhicule " + nouveau.getId() + " ajouté avec succès.");
        } catch (DonneeInvalideException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
        }
    }

    /** Affiche une liste numérotée et redemande tant que le choix n'est pas valide. */
    private static String demanderChoixParmi(String titre, List<String> options) {
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
            } catch (NumberFormatException ignored) {
                // on retombe sur le message d'erreur ci-dessous
            }
            System.out.println("Choix invalide, réessaie.");
        }
    }

    private static void modifierVehicule() {
        System.out.println("\n-- Modifier un véhicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) return;

        System.out.println("Laisse vide pour garder la valeur actuelle.");
        String modele = demander("Modèle [" + v.getModele() + "] : ");
        String energie = demander("Énergie [" + v.getTypeEnergie() + "] : ");
        String capacite = demander("Capacité (kg) [" + v.getCapaciteKg() + "] : ");
        String km = demander("Kilométrage [" + v.getKilometrage() + "] : ");
        String zone = demander("Zone [" + v.getZone() + "] : ");
        String chauffeur = demander("Chauffeur [" + (v.getChauffeurAssigne() == null ? "" : v.getChauffeurAssigne()) + "] : ");
        String tarif = demander("Tarif de base [" + v.getTarifBase() + "] : ");

        try {
            Vehicule valide = VehiculeFactory.creerVehicule(
                    v.getId(), modele.isBlank() ? v.getModele() : modele, v.getCodeCsv(),
                    energie.isBlank() ? v.getTypeEnergie().toString() : energie,
                    capacite.isBlank() ? String.valueOf(v.getCapaciteKg()) : capacite,
                    km.isBlank() ? String.valueOf(v.getKilometrage()) : km,
                    v.getStatut().toString(),
                    zone.isBlank() ? v.getZone().toString() : zone,
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
            System.out.println("Véhicule " + v.getId() + " modifié avec succès.");
        } catch (DonneeInvalideException e) {
            System.out.println("Erreur de validation : " + e.getMessage());
        }
    }

    private static void retirerVehicule() {
        System.out.println("\n-- Retirer un véhicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) return;

        String confirmation = demander("Confirmer le retrait de " + v.getId() + " ? (o/n) : ");
        if (!confirmation.equalsIgnoreCase("o")) {
            System.out.println("Retrait annulé.");
            return;
        }
        gestionnaire.retirerVehicule(v.getId());
        sauvegarder();
        System.out.println("Véhicule " + v.getId() + " retiré.");
    }

    private static void louerVehicule() {
        System.out.println("\n-- Louer un véhicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) return;

        String chauffeur = demander("Chauffeur : ");
        String joursStr = demander("Nombre de jours : ");
        try {
            int jours = Integer.parseInt(joursStr.trim());
            gestionnaire.louer(v, chauffeur, jours);
            sauvegarder();
            System.out.printf("Véhicule %s loué à %s pour %d jour(s) — tarif : %.2f$%n",
                    v.getId(), chauffeur, jours, v.calculerTarif(jours));
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le nombre de jours doit être un entier.");
        } catch (VehiculeIndisponibleException e) {
            System.out.println("Location impossible : " + e.getMessage());
        }
    }

    private static void retournerVehicule() {
        System.out.println("\n-- Retourner un véhicule --");
        Vehicule v = trouverParIdDemande();
        if (v == null) return;

        String kmStr = demander("Kilométrage affiché à l'odomètre du véhicule [actuel : "
                + v.getKilometrage() + " km] : ");
        try {
            int nouveauKm = Integer.parseInt(kmStr.trim());
            gestionnaire.retourner(v, nouveauKm);
            sauvegarder();
            System.out.println("Véhicule " + v.getId() + " retourné — statut : DISPONIBLE, kilométrage : " + v.getKilometrage() + " km.");
        } catch (NumberFormatException e) {
            System.out.println("Erreur : le kilométrage doit être un entier.");
        } catch (KilometrageInvalideException | VehiculeIndisponibleException e) {
            System.out.println("Retour impossible : " + e.getMessage());
        }
    }

    private static void signalerEntretien() {
        System.out.println("\n-- Signaler un entretien --");
        Vehicule v = trouverParIdDemande();
        if (v == null) return;

        String description = demander("Description du problème : ");
        v.signalerEntretien(description);
        v.setStatut(StatutVehicule.EN_ENTRETIEN);
        sauvegarder();
        System.out.println("Entretien signalé pour " + v.getId() + ".");
    }

    private static void afficherStatistiques() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        System.out.println("\n--- Statistiques ---");
        System.out.printf("Revenu total généré : %.2f$%n", stats.revenuTotal());
        System.out.printf("Kilométrage moyen : %.1f km%n", stats.kilometrageMoyen());
        System.out.println("Taux d'utilisation par type :");
        for (Map.Entry<String, Double> e : stats.tauxUtilisationParType().entrySet()) {
            System.out.printf("  %s : %.1f jours%n", e.getKey(), e.getValue());
        }
        System.out.println("Répartition par zone :");
        for (Map.Entry<String, Long> e : stats.repartitionParZone().entrySet()) {
            System.out.println("  " + e.getKey() + " : " + e.getValue() + " véhicule(s)");
        }
        System.out.println("Véhicules nécessitant un entretien :");
        List<Vehicule> aEntretenir = stats.vehiculesNecessitantEntretien();
        if (aEntretenir.isEmpty()) {
            System.out.println("  Aucun.");
        } else {
            aEntretenir.forEach(v -> System.out.println("  " + v.getId() + " (" + v.getModele() + ")"));
        }
    }

    private static void afficherAlertes() {
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        System.out.println("\n--- Alertes actives ---");
        alertes.genererAlertes().forEach(a -> System.out.println("  ! " + a));
    }

    private static void genererRapport() {
        StatistiquesFlotte stats = new StatistiquesFlotte(gestionnaire.getFlotte());
        GestionnaireAlertes alertes = new GestionnaireAlertes(gestionnaire.getFlotte());
        String contenu = RapportGenerateur.genererContenu(gestionnaire.getFlotte(), stats, alertes);
        try {
            LECTEUR_CSV.ecrireRapport("data/rapport_flotte.txt", contenu);
            System.out.println("Rapport généré : data/rapport_flotte.txt");
        } catch (IOException e) {
            System.out.println("Erreur lors de la génération du rapport : " + e.getMessage());
        }
    }

    private static Vehicule trouverParIdDemande() {
        String id = demander("ID du véhicule : ");
        Vehicule v = gestionnaire.trouverParId(id);
        if (v == null) {
            System.out.println("Aucun véhicule trouvé avec l'id \"" + id + "\".");
        }
        return v;
    }
}
