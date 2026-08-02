
public interface GestionLocation {
    void louer(Vehicule vehicule, String chauffeur, int joursLocation) throws VehiculeIndisponibleException;

    /** nouveauKilometrage = valeur lue directement à l'odomètre du véhicule (pas un delta). */
    void retourner(Vehicule vehicule, int nouveauKilometrage) throws KilometrageInvalideException, VehiculeIndisponibleException;

    void renouveler(Vehicule vehicule, int joursSupplementaires) throws VehiculeIndisponibleException;
}
