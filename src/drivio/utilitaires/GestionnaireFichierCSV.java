package drivio.utilitaires;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import drivio.modeles.Vehicule;
import drivio.exceptions.DonneeInvalideException;

public class GestionnaireFichierCSV {

    public ArrayList<Vehicule> chargerVehicules(String cheminFichier, ArrayList<String> erreurs) throws IOException {
        ArrayList<Vehicule> vehicules = new ArrayList<>();
        BufferedReader lecteur = new BufferedReader(new FileReader(cheminFichier));

        try {
            String ligne;
            boolean premiereLigne = true;
            int numeroLigne = 0;

            while ((ligne = lecteur.readLine()) != null) {
                numeroLigne++;
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }
                if (ligne.isBlank()) {
                    continue;
                }
                try {
                    vehicules.add(parserLigne(ligne));
                } catch (DonneeInvalideException e) {
                    erreurs.add("Ligne " + numeroLigne + " ignoree : " + e.getMessage());
                }
            }
        } finally {
            lecteur.close();
        }
        return vehicules;
    }

    private Vehicule parserLigne(String ligne) throws DonneeInvalideException {
        String[] champs = ligne.split(",", -1);
        if (champs.length != 10) {
            throw new DonneeInvalideException("Nombre de colonnes invalide (" + champs.length + " au lieu de 10) -> " + ligne);
        }

        return ConstructeurVehicule.creerVehicule(
                champs[0].trim(), champs[1].trim(), champs[2].trim(), champs[3].trim(),
                champs[4].trim(), champs[5].trim(), champs[6].trim(), champs[7].trim(),
                champs[8].trim(), champs[9].trim());
    }

    public void ecrireVehicules(String cheminFichier, ArrayList<Vehicule> vehicules) throws IOException {
        String contenu = "id,modele,typeVehicule,typeEnergie,capaciteKg,kilometrage,statut,zone,chauffeurAssigne,tarifBase\n";
        for (int i = 0; i < vehicules.size(); i++) {
            contenu = contenu + vehicules.get(i).toCsvLigne() + "\n";
        }
        FileWriter ecrivain = new FileWriter(cheminFichier);
        try {
            ecrivain.write(contenu);
        } finally {
            ecrivain.close();
        }
    }

    public void ecrireRapport(String cheminFichier, String contenu) throws IOException {
        FileWriter ecrivain = new FileWriter(cheminFichier);
        try {
            ecrivain.write(contenu);
        } finally {
            ecrivain.close();
        }
    }
}
