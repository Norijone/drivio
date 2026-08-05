package drivio.gestion;

import java.util.ArrayList;
import java.util.Collections;

import drivio.modeles.Vehicule;

public class StatistiquesFlotte {

    private ArrayList<Vehicule> flotte;

    public StatistiquesFlotte(ArrayList<Vehicule> flotte) {
        this.flotte = flotte;
    }

    public double revenuTotal() {
        double total = 0;
        for (int i = 0; i < flotte.size(); i++) {
            Vehicule v = flotte.get(i);
            total = total + v.calculerTarif(v.getJoursLouesCumules());
        }
        return total;
    }

    public double kilometrageMoyen() {
        if (flotte.isEmpty()) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < flotte.size(); i++) {
            total = total + flotte.get(i).getKilometrage();
        }
        return (double) total / flotte.size();
    }

    public ArrayList<String> getTypesUniques() {
        ArrayList<String> types = new ArrayList<>();
        for (int i = 0; i < flotte.size(); i++) {
            String type = flotte.get(i).getTypeVehicule();
            if (!types.contains(type)) {
                types.add(type);
            }
        }
        return types;
    }

    public double tauxUtilisationPourType(String type) {
        int joursTotal = 0;
        int nombre = 0;
        for (int i = 0; i < flotte.size(); i++) {
            Vehicule v = flotte.get(i);
            if (v.getTypeVehicule().equals(type)) {
                joursTotal = joursTotal + v.getJoursLouesCumules();
                nombre = nombre + 1;
            }
        }
        if (nombre == 0) {
            return 0;
        }
        return (double) joursTotal / nombre;
    }

    public ArrayList<String> getZonesUniques() {
        ArrayList<String> zones = new ArrayList<>();
        for (int i = 0; i < flotte.size(); i++) {
            String zone = flotte.get(i).getZone();
            if (!zones.contains(zone)) {
                zones.add(zone);
            }
        }
        return zones;
    }

    public int nombreVehiculesDansZone(String zone) {
        int nombre = 0;
        for (int i = 0; i < flotte.size(); i++) {
            if (flotte.get(i).getZone().equals(zone)) {
                nombre = nombre + 1;
            }
        }
        return nombre;
    }

    public ArrayList<Vehicule> vehiculesLesPlusUtilises(int top) {
        ArrayList<Vehicule> copie = new ArrayList<>(flotte);
        Collections.sort(copie, new ComparateurUtilisation());
        ArrayList<Vehicule> resultat = new ArrayList<>();
        for (int i = 0; i < copie.size() && i < top; i++) {
            resultat.add(copie.get(i));
        }
        return resultat;
    }

    public ArrayList<Vehicule> vehiculesNecessitantEntretien() {
        ArrayList<Vehicule> resultat = new ArrayList<>();
        for (int i = 0; i < flotte.size(); i++) {
            if (flotte.get(i).necessiteEntretien()) {
                resultat.add(flotte.get(i));
            }
        }
        return resultat;
    }
}
