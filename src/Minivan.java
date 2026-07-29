/**
 * Minivan pour livraisons légères (ex. Dodge Caravan, Honda Odyssey, Toyota Sienna).
 * Tarif réduit car capacité de charge plus faible que les utilitaires.
 */
public class Minivan extends Vehicule {

    private static final double COEFFICIENT_TARIF = 0.85; // -15% : véhicule plus léger

    public Minivan(String id, String modele, TypeEnergie typeEnergie, double capaciteKg,
                    int kilometrage, StatutVehicule statut, Zone zone,
                    String chauffeurAssigne, double tarifBase) {
        super(id, modele, typeEnergie, capaciteKg, kilometrage, statut, zone, chauffeurAssigne, tarifBase);
    }

    @Override
    public double calculerTarif(int joursLocation) {
        return tarifBase * joursLocation * COEFFICIENT_TARIF;
    }

    @Override
    public String getTypeVehicule() {
        return "Minivan";
    }

    @Override
    public String getCodeCsv() {
        return "MINIVAN";
    }
}
