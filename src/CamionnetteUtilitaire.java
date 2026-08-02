
public class CamionnetteUtilitaire extends Vehicule {

    private static final double RABAIS_ELECTRIQUE = 0.90; // -10% pour véhicules électriques

    public CamionnetteUtilitaire(String id, String modele, TypeEnergie typeEnergie, double capaciteKg,
                                  int kilometrage, StatutVehicule statut, Zone zone,
                                  String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    @Override
    public double calculerTarif(int joursLocation) {
        double tarif = tarifBase * joursLocation;
        if (typeEnergie == TypeEnergie.ELECTRIQUE) {
            tarif *= RABAIS_ELECTRIQUE;
        }
        return tarif;
    }

    @Override
    public String getTypeVehicule() {
        return "Camionnette utilitaire";
    }

    @Override
    public String getCodeCsv() {
        return "CAMIONNETTE";
    }
}
