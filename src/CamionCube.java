
public class CamionCube extends Vehicule {

    private static final double SURCHARGE_PAR_500KG = 5.0; // $ par tranche de 500 kg de capacité

    public CamionCube(String id, String modele, TypeEnergie typeEnergie, double capaciteKg,
                       int kilometrage, StatutVehicule statut, Zone zone,
                       String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    @Override
    public double calculerTarif(int joursLocation) {
        double surcharge = (capaciteKg / 500.0) * SURCHARGE_PAR_500KG;
        return (tarifBase + surcharge) * joursLocation;
    }

    @Override
    public String getTypeVehicule() {
        return "Camion cube";
    }

    @Override
    public String getCodeCsv() {
        return "CUBE";
    }
}
