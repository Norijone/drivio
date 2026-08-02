import java.util.ArrayList;
import java.util.List;


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

    
    public abstract double calculerTarif(int joursLocation);

    public abstract String getTypeVehicule();

    public abstract String getCodeCsv();


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
        return kilometrage > 0 && kilometrage % 15000 < 500;
    }


    public void enregistrerLocation(String chauffeur, int jours) {
        this.chauffeurAssigne = chauffeur;
        this.statut = StatutVehicule.LOUE;
        this.joursLouesCumules += jours;
        this.nombreLocations++;
        historiqueLocation.add("Loué à " + chauffeur + " pour " + jours + " jour(s)");
    }

   
    public void enregistrerRetour(int nouveauKilometrage) {
        int kilometrageAvant = this.kilometrage;
        this.kilometrage = nouveauKilometrage;
        this.statut = StatutVehicule.DISPONIBLE;
        int parcouru = nouveauKilometrage - kilometrageAvant;
        historiqueLocation.add("Retourné à " + nouveauKilometrage + " km à l'odomètre (" + parcouru + " km parcourus)");
    }

    // ----- Getters / Setters -----

    public String getId() { return id; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public TypeEnergie getTypeEnergie() { return typeEnergie; }
    public void setTypeEnergie(TypeEnergie typeEnergie) { this.typeEnergie = typeEnergie; }
    public double getCapaciteKg() { return capaciteKg; }
    public void setCapaciteKg(double capaciteKg) { this.capaciteKg = capaciteKg; }
    public int getKilometrage() { return kilometrage; }
    public void setKilometrage(int kilometrage) { this.kilometrage = kilometrage; }
    public StatutVehicule getStatut() { return statut; }
    public void setStatut(StatutVehicule statut) { this.statut = statut; }
    public Zone getZone() { return zone; }
    public void setZone(Zone zone) { this.zone = zone; }
    public String getChauffeurAssigne() { return chauffeurAssigne; }
    public void setChauffeurAssigne(String chauffeurAssigne) { this.chauffeurAssigne = chauffeurAssigne; }
    public double getTarifBase() { return tarifBase; }
    public void setTarifBase(double tarifBase) { this.tarifBase = tarifBase; }
    public int getJoursLouesCumules() { return joursLouesCumules; }
    public int getNombreLocations() { return nombreLocations; }
    public List<String> getHistoriqueLocation() { return historiqueLocation; }

    public String toCsvLigne() {
        String chauffeur = chauffeurAssigne == null ? "" : chauffeurAssigne;
        return String.join(",", id, modele, getCodeCsv(), typeEnergie.toString(),
                String.valueOf(capaciteKg), String.valueOf(kilometrage), statut.toString(),
                zone.toString(), chauffeur, String.valueOf(tarifBase));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %s) - %s - %.0f km - Zone: %s",
                id, modele, getTypeVehicule(), typeEnergie, statut, (double) kilometrage, zone);
    }
}
