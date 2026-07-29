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

        return VehiculeFactory.creerVehicule(
                champs[0].trim(), champs[1].trim(), champs[2].trim(), champs[3].trim(),
                champs[4].trim(), champs[5].trim(), champs[6].trim(), champs[7].trim(),
                champs[8].trim(), champs[9].trim());
    }

    /** Réécrit entièrement le fichier CSV à partir de l'état courant de la flotte (persistance). */
    public void ecrireVehicules(String cheminFichier, List<Vehicule> vehicules) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,modele,typeVehicule,typeEnergie,capaciteKg,kilometrage,statut,zone,chauffeurAssigne,tarifBase\n");
        for (Vehicule v : vehicules) {
            sb.append(v.toCsvLigne()).append("\n");
        }
        try (Writer ecrivain = new OutputStreamWriter(new FileOutputStream(cheminFichier), StandardCharsets.UTF_8)) {
            ecrivain.write(sb.toString());
        }
    }

    /** Écrit le rapport final (stats + alertes) dans un fichier texte. */
    public void ecrireRapport(String cheminFichier, String contenu) throws IOException {
        try (Writer ecrivain = new OutputStreamWriter(new FileOutputStream(cheminFichier), StandardCharsets.UTF_8)) {
            ecrivain.write(contenu);
        }
    }
}
