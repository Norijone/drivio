package drivio.modeles;

import java.util.ArrayList;

public abstract class Vehicule implements Entretenable {

    protected String id;
    protected String modele;
    protected String typeEnergie;
    protected double capaciteKg;
    protected int kilometrage;
    protected String statut;
    protected String zone;
    protected String chauffeurAssigne;
    protected double tarifBase;
    protected int joursLouesCumules;
    protected int nombreLocations;

    protected ArrayList<String> historiqueEntretien = new ArrayList<>();
    protected ArrayList<String> historiqueLocation = new ArrayList<>();
    protected String entretienPlanifie;

    public Vehicule(String id, String modele, String typeEnergie, double capaciteKg,
                     int kilometrage, String statut, String zone,
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

    public abstract double calculerTarif(int joursLocation);

    public abstract String getTypeVehicule();

    public abstract String getCodeCsv();

    public void signalerEntretien(String description) {
        historiqueEntretien.add("Panne ou reparation signalee : " + description);
    }

    public void planifierEntretien(String dateEntretien) {
        this.entretienPlanifie = dateEntretien;
        historiqueEntretien.add("Entretien planifie pour le " + dateEntretien);
    }

    public ArrayList<String> getHistoriqueEntretien() {
        return historiqueEntretien;
    }

    public boolean necessiteEntretien() {
        return kilometrage > 0 && kilometrage % 15000 < 500;
    }

    public void enregistrerLocation(String chauffeur, int jours) {
        this.chauffeurAssigne = chauffeur;
        this.statut = "LOUE";
        this.joursLouesCumules = this.joursLouesCumules + jours;
        this.nombreLocations = this.nombreLocations + 1;
        historiqueLocation.add("Loue a " + chauffeur + " pour " + jours + " jour(s)");
    }

    public void enregistrerRetour(int nouveauKilometrage) {
        int kilometrageAvant = this.kilometrage;
        this.kilometrage = nouveauKilometrage;
        this.statut = "DISPONIBLE";
        int parcouru = nouveauKilometrage - kilometrageAvant;
        historiqueLocation.add("Retourne a " + nouveauKilometrage + " km a l'odometre (" + parcouru + " km parcourus)");
    }

    public String getId() {
        return id;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getTypeEnergie() {
        return typeEnergie;
    }

    public void setTypeEnergie(String typeEnergie) {
        this.typeEnergie = typeEnergie;
    }

    public double getCapaciteKg() {
        return capaciteKg;
    }

    public void setCapaciteKg(double capaciteKg) {
        this.capaciteKg = capaciteKg;
    }

    public int getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(int kilometrage) {
        this.kilometrage = kilometrage;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getChauffeurAssigne() {
        return chauffeurAssigne;
    }

    public void setChauffeurAssigne(String chauffeurAssigne) {
        this.chauffeurAssigne = chauffeurAssigne;
    }

    public double getTarifBase() {
        return tarifBase;
    }

    public void setTarifBase(double tarifBase) {
        this.tarifBase = tarifBase;
    }

    public int getJoursLouesCumules() {
        return joursLouesCumules;
    }

    public void ajouterJoursLoues(int jours) {
        this.joursLouesCumules = this.joursLouesCumules + jours;
    }

    public int getNombreLocations() {
        return nombreLocations;
    }

    public ArrayList<String> getHistoriqueLocation() {
        return historiqueLocation;
    }

    public String toCsvLigne() {
        String chauffeur = chauffeurAssigne == null ? "" : chauffeurAssigne;
        return id + "," + modele + "," + getCodeCsv() + "," + typeEnergie + "," + capaciteKg + ","
                + kilometrage + "," + statut + "," + zone + "," + chauffeur + "," + tarifBase;
    }

    public String toString() {
        return "[" + id + "] " + modele + " (" + getTypeVehicule() + ", " + typeEnergie + ") - "
                + statut + " - " + kilometrage + " km - Zone: " + zone;
    }
}
