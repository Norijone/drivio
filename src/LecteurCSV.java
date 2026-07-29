import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsable exclusivement de la lecture et de l'écriture des fichiers CSV (SRP).
 * Format attendu :
 * id,modele,typeVehicule,typeEnergie,capaciteKg,kilometrage,statut,zone,chauffeurAssigne,tarifBase
 */
public class LecteurCSV {

    /**
     * Charge les véhicules depuis un fichier CSV. Les lignes invalides sont
     * ignorées et rapportées dans la liste erreurs plutôt que de faire
     * planter le chargement complet.
     */
    public List<Vehicule> chargerVehicules(String cheminFichier, List<String> erreurs) throws IOException {
        List<Vehicule> vehicules = new ArrayList<>();

        try (BufferedReader lecteur = new BufferedReader(
                new InputStreamReader(new FileInputStream(cheminFichier), StandardCharsets.UTF_8))) {
            String ligne;
            boolean premiereLigne = true;
            int numeroLigne = 0;

            while ((ligne = lecteur.readLine()) != null) {
                numeroLigne++;
                if (premiereLigne) {
                    premiereLigne = false;
                    continue; // sauter l'en-tête
                }
                if (ligne.isBlank()) {
                    continue;
                }

                try {
                    vehicules.add(parserLigne(ligne));
                } catch (DonneeInvalideException e) {
                    erreurs.add("Ligne " + numeroLigne + " ignorée : " + e.getMessage());
                }
            }
        }
        return vehicules;
    }

    private Vehicule parserLigne(String ligne) throws DonneeInvalideException {
        String[] champs = ligne.split(",", -1);
        if (champs.length != 10) {
            throw new DonneeInvalideException("Nombre de colonnes invalide (" + champs.length + " au lieu de 10) -> " + ligne);
        }

        String id = champs[0].trim();
        String modele = champs[1].trim();
        String typeVehiculeStr = champs[2].trim().toUpperCase();
        String typeEnergieStr = champs[3].trim().toUpperCase();
        String capaciteStr = champs[4].trim();
        String kilometrageStr = champs[5].trim();
        String statutStr = champs[6].trim().toUpperCase();
        String zoneStr = champs[7].trim().toUpperCase();
        String chauffeurAssigne = champs[8].trim();
        String tarifBaseStr = champs[9].trim();

        if (id.isEmpty() || modele.isEmpty()) {
            throw new DonneeInvalideException("Id ou modèle manquant -> " + ligne);
        }

        double capaciteKg;
        int kilometrage;
        double tarifBase;
        try {
            capaciteKg = Double.parseDouble(capaciteStr);
            kilometrage = Integer.parseInt(kilometrageStr);
            tarifBase = Double.parseDouble(tarifBaseStr);
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Valeur numérique invalide -> " + ligne);
        }

        if (kilometrage < 0) {
            throw new DonneeInvalideException("Kilométrage négatif -> " + ligne);
        }
        if (capaciteKg <= 0) {
            throw new DonneeInvalideException("Capacité invalide -> " + ligne);
        }
        if (tarifBase <= 0) {
            throw new DonneeInvalideException("Tarif de base invalide -> " + ligne);
        }

        TypeEnergie typeEnergie;
        StatutVehicule statut;
        Zone zone;
        try {
            typeEnergie = TypeEnergie.valueOf(typeEnergieStr);
            statut = StatutVehicule.valueOf(statutStr);
            zone = Zone.valueOf(zoneStr);
        } catch (IllegalArgumentException e) {
            throw new DonneeInvalideException("Type d'énergie, statut ou zone inconnu -> " + ligne);
        }

        return switch (typeVehiculeStr) {
            case "CAMIONNETTE" -> new CamionnetteUtilitaire(id, modele, typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            case "MINIVAN" -> new Minivan(id, modele, typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            case "CUBE" -> new CamionCube(id, modele, typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            default -> throw new DonneeInvalideException("Type de véhicule inconnu : " + typeVehiculeStr + " -> " + ligne);
        };
    }

    /** Écrit le rapport final (stats + alertes) dans un fichier texte. */
    public void ecrireRapport(String cheminFichier, String contenu) throws IOException {
        try (Writer ecrivain = new OutputStreamWriter(new FileOutputStream(cheminFichier), StandardCharsets.UTF_8)) {
            ecrivain.write(contenu);
        }
    }
}
