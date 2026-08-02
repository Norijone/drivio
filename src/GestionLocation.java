/**
 * Contrat pour la gestion des opérations de location de véhicules.
 * Toute classe qui gère un parc de véhicules peut implémenter cette
 * interface sans dépendre des détails internes de Vehicule (OCP).
 */
public interface GestionLocation {
    void louer(Vehicule vehicule, String chauffeur, int joursLocation) throws VehiculeIndisponibleException;

    /** nouveauKilometrage = valeur lue directement à l'odomètre du véhicule (pas un delta). */
    void retourner(Vehicule vehicule, int nouveauKilometrage) throws KilometrageInvalideException, VehiculeIndisponibleException;

    void renouveler(Vehicule vehicule, int joursSupplementaires) throws VehiculeIndisponibleException;
}
