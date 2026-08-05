package drivio.gestion;

import drivio.modeles.Vehicule;
import drivio.exceptions.VehiculeIndisponibleException;
import drivio.exceptions.KilometrageInvalideException;

public interface GestionLocation {

    void louer(Vehicule vehicule, String chauffeur, int joursLocation) throws VehiculeIndisponibleException;

    void retourner(Vehicule vehicule, int nouveauKilometrage) throws KilometrageInvalideException, VehiculeIndisponibleException;

    void renouveler(Vehicule vehicule, int joursSupplementaires) throws VehiculeIndisponibleException;
}
