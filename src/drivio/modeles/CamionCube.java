package drivio.modeles;

public class CamionCube extends Vehicule {

    private static final double SURCHARGE_PAR_500KG = 5.0;

    public CamionCube(String id, String modele, String typeEnergie, double capaciteKg,
                       int kilometrage, String statut, String zone,
                       String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    public double calculerTarif(int joursLocation) {
        double surcharge = (capaciteKg / 500.0) * SURCHARGE_PAR_500KG;
        return (tarifBase + surcharge) * joursLocation;
    }

    public String getTypeVehicule() {
        return "Camion cube";
    }

    public String getCodeCsv() {
        return "CUBE";
    }
}
