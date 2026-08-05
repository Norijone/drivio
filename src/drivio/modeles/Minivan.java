package drivio.modeles;

public class Minivan extends Vehicule {

    private static final double COEFFICIENT_TARIF = 0.85;

    public Minivan(String id, String modele, String typeEnergie, double capaciteKg,
                    int kilometrage, String statut, String zone,
                    String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    public double calculerTarif(int joursLocation) {
        return tarifBase * joursLocation * COEFFICIENT_TARIF;
    }

    public String getTypeVehicule() {
        return "Minivan";
    }

    public String getCodeCsv() {
        return "MINIVAN";
    }
}
