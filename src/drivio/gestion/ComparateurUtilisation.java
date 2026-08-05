package drivio.gestion;

import java.util.Comparator;

import drivio.modeles.Vehicule;

public class ComparateurUtilisation implements Comparator<Vehicule> {

    public int compare(Vehicule v1, Vehicule v2) {
        return Integer.compare(v2.getNombreLocations(), v1.getNombreLocations());
    }
}
