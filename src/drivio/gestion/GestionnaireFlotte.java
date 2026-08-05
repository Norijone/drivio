package drivio.gestion;

import java.util.ArrayList;

import drivio.modeles.Vehicule;
import drivio.exceptions.VehiculeIndisponibleException;
import drivio.exceptions.KilometrageInvalideException;

public class GestionnaireFlotte implements GestionLocation {

    private ArrayList<Vehicule> flotte;

    public GestionnaireFlotte(ArrayList<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public ArrayList<Vehicule> getFlotte() {
        return flotte;
    }

    public void ajouterVehicule(Vehicule vehicule) {
        flotte.add(vehicule);
    }

    public void retirerVehicule(String id) {
        int index = -1;
        for (int i = 0; i < flotte.size(); i++) {
            if (flotte.get(i).getId().equals(id)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            flotte.remove(index);
        }
    }

    public Vehicule trouverParId(String id) {
        for (int i = 0; i < flotte.size(); i++) {
            if (flotte.get(i).getId().equals(id)) {
                return flotte.get(i);
            }
        }
        return null;
    }

    public String genererProchainId() {
        int max = 0;
        for (int i = 0; i < flotte.size(); i++) {
            String id = flotte.get(i).getId();
            if (id != null && id.startsWith("V")) {
                try {
                    int numero = Integer.parseInt(id.substring(1));
                    if (numero > max) {
                        max = numero;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        int prochain = max + 1;
        String numeroTexte = "" + prochain;
        while (numeroTexte.length() < 3) {
            numeroTexte = "0" + numeroTexte;
        }
        return "V" + numeroTexte;
    }

    public ArrayList<Vehicule> getVehiculesDisponibles() {
        ArrayList<Vehicule> disponibles = new ArrayList<>();
        for (int i = 0; i < flotte.size(); i++) {
            if (flotte.get(i).getStatut().equals("DISPONIBLE")) {
                disponibles.add(flotte.get(i));
            }
        }
        return disponibles;
    }

    public void louer(Vehicule vehicule, String chauffeur, int joursLocation) throws VehiculeIndisponibleException {
        if (!vehicule.getStatut().equals("DISPONIBLE")) {
            throw new VehiculeIndisponibleException(
                    "Le vehicule " + vehicule.getId() + " n'est pas disponible (statut : " + vehicule.getStatut() + ")");
        }
        if (joursLocation <= 0) {
            throw new VehiculeIndisponibleException("Le nombre de jours de location doit etre positif.");
        }
        vehicule.enregistrerLocation(chauffeur, joursLocation);
    }

    public void retourner(Vehicule vehicule, int nouveauKilometrage)
            throws KilometrageInvalideException, VehiculeIndisponibleException {
        if (!vehicule.getStatut().equals("LOUE")) {
            throw new VehiculeIndisponibleException(
                    "Le vehicule " + vehicule.getId() + " n'est pas actuellement loue.");
        }
        if (nouveauKilometrage < vehicule.getKilometrage()) {
            throw new KilometrageInvalideException(
                    "Le kilometrage a l'odometre (" + nouveauKilometrage
                            + ") ne peut pas etre inferieur au kilometrage actuel du vehicule ("
                            + vehicule.getKilometrage() + ").");
        }
        vehicule.enregistrerRetour(nouveauKilometrage);
    }

    public void renouveler(Vehicule vehicule, int joursSupplementaires) throws VehiculeIndisponibleException {
        if (!vehicule.getStatut().equals("LOUE")) {
            throw new VehiculeIndisponibleException(
                    "Impossible de renouveler : le vehicule " + vehicule.getId() + " n'est pas actuellement loue.");
        }
        if (joursSupplementaires <= 0) {
            throw new VehiculeIndisponibleException("Le nombre de jours supplementaires doit etre positif.");
        }
        vehicule.ajouterJoursLoues(joursSupplementaires);
        vehicule.getHistoriqueLocation().add("Location renouvelee pour " + joursSupplementaires + " jour(s) de plus");
    }
}
