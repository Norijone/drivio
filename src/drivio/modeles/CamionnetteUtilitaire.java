package drivio.modeles;

public class CamionnetteUtilitaire extends Vehicule {

    private static final double RABAIS_ELECTRIQUE = 0.90;

    public CamionnetteUtilitaire(String id, String modele, String typeEnergie, double capaciteKg,
                                  int kilometrage, String statut, String zone,
                                  String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    public double calculerTarif(int joursLocation) {
        double tarif = tarifBase * joursLocation;
        if (typeEnergie.equals("ELECTRIQUE")) {
            tarif = tarif * RABAIS_ELECTRIQUE;
        }
        return tarif;
    }

    public String getTypeVehicule() {
        return "Camionnette utilitaire";
    }

    public String getCodeCsv() {
        return "CAMIONNETTE";
    }
}
