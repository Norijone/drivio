import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un véhicule de la flotte Drivio.
 * Factorise les attributs et comportements communs à tous les types
 * de véhicules ; chaque sous-classe redéfinit calculerTarif() selon
 * ses propres règles de tarification (redéfinition de méthode).
 */
public abstract class Vehicule implements Entretenable {

    protected String id;
    protected String modele;
    protected TypeEnergie typeEnergie;
    protected double capaciteKg;
    protected int kilometrage;
    protected StatutVehicule statut;
    protected Zone zone;
    protected String chauffeurAssigne;
    protected double tarifBase;
    protected int joursLouesCumules;
    protected int nombreLocations;

    protected final List<String> historiqueEntretien = new ArrayList<>();
    protected final List<String> historiqueLocation = new ArrayList<>();
    protected String entretienPlanifie;

    public Vehicule(String id, String modele, TypeEnergie typeEnergie, double capaciteKg,
                     int kilometrage, StatutVehicule statut, Zone zone,
                     String chauffeurAssigne, double tarifBase) {
        this.id = id;
        this.modele = modele;
        this.typeEnergie = typeEnergie;
        this.capaciteKg = capaciteKg;
        this.kilometrage = kilometrage;
        this.statut = statut;
        this.zone = zone;
        this.chauffeurAssigne = chauffeurAssigne;
        this.tarifBase = tarifBase;
    }

    /**
     * Calcule le tarif de location pour un nombre de jours donné.
     * Chaque type de véhicule applique sa propre règle (redéfinition).
     */
    public abstract double calculerTarif(int joursLocation);

    /** Nom lisible du type de véhicule, utilisé pour les stats. */
    public abstract String getTypeVehicule();

    // ----- Entretenable -----

    @Override
    public void signalerEntretien(String description) {
        historiqueEntretien.add("Panne/réparation signalée : " + description);
    }

    @Override
    public void planifierEntretien(String dateEntretien) {
        this.entretienPlanifie = dateEntretien;
        historiqueEntretien.add("Entretien planifié pour le " + dateEntretien);
    }

    @Override
    public List<String> getHistoriqueEntretien() {
        return historiqueEntretien;
    }

    @Override
    public boolean necessiteEntretien() {
        // Règle simple : tous les 15 000 km, un entretien est requis.
        return kilometrage > 0 && kilometrage % 15000 < 500;
    }

    // ----- Location -----

    public void enregistrerLocation(String chauffeur, int jours) {
        this.chauffeurAssigne = chauffeur;
        this.statut = StatutVehicule.LOUE;
        this.joursLouesCumules += jours;
        this.nombreLocations++;
        historiqueLocation.add("Loué à " + chauffeur + " pour " + jours + " jour(s)");
    }

    public void enregistrerRetour(int kmParcourus) {
        this.kilometrage += kmParcourus;
        this.statut = StatutVehicule.DISPONIBLE;
        historiqueLocation.add("Retourné après " + kmParcourus + " km parcourus");
    }

    // ----- Getters / Setters -----

    public String getId() { return id; }
    public String getModele() { return modele; }
    public TypeEnergie getTypeEnergie() { return typeEnergie; }
    public double getCapaciteKg() { return capaciteKg; }
    public int getKilometrage() { return kilometrage; }
    public StatutVehicule getStatut() { return statut; }
    public void setStatut(StatutVehicule statut) { this.statut = statut; }
    public Zone getZone() { return zone; }
    public String getChauffeurAssigne() { return chauffeurAssigne; }
    public double getTarifBase() { return tarifBase; }
    public int getJoursLouesCumules() { return joursLouesCumules; }
    public int getNombreLocations() { return nombreLocations; }
    public List<String> getHistoriqueLocation() { return historiqueLocation; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %s) - %s - %.0f km - Zone: %s",
                id, modele, getTypeVehicule(), typeEnergie, statut, (double) kilometrage, zone);
    }
}
