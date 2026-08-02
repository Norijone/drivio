
public class VehiculeFactory {

    public static Vehicule creerVehicule(String id, String modele, String typeVehiculeStr,
                                          String typeEnergieStr, String capaciteStr, String kilometrageStr,
                                          String statutStr, String zoneStr, String chauffeurAssigne,
                                          String tarifBaseStr) throws DonneeInvalideException {

        if (id == null || id.isBlank() || modele == null || modele.isBlank()) {
            throw new DonneeInvalideException("L'id et le modèle sont obligatoires.");
        }

        double capaciteKg;
        int kilometrage;
        double tarifBase;
        try {
            capaciteKg = Double.parseDouble(capaciteStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Capacité invalide : \"" + capaciteStr + "\" n'est pas un nombre.");
        }
        try {
            kilometrage = Integer.parseInt(kilometrageStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Kilométrage invalide : \"" + kilometrageStr + "\" n'est pas un nombre entier.");
        }
        try {
            tarifBase = Double.parseDouble(tarifBaseStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Tarif de base invalide : \"" + tarifBaseStr + "\" n'est pas un nombre.");
        }

        if (kilometrage < 0) {
            throw new DonneeInvalideException("Le kilométrage ne peut pas être négatif (" + kilometrage + ").");
        }
        if (capaciteKg <= 0) {
            throw new DonneeInvalideException("La capacité doit être supérieure à 0 (" + capaciteKg + ").");
        }
        if (tarifBase <= 0) {
            throw new DonneeInvalideException("Le tarif de base doit être supérieur à 0 (" + tarifBase + ").");
        }

        TypeEnergie typeEnergie;
        StatutVehicule statut;
        Zone zone;
        try {
            typeEnergie = TypeEnergie.valueOf(typeEnergieStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DonneeInvalideException("Type d'énergie inconnu : \"" + typeEnergieStr + "\" (attendu : ESSENCE, DIESEL, ELECTRIQUE).");
        }
        try {
            statut = StatutVehicule.valueOf(statutStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DonneeInvalideException("Statut inconnu : \"" + statutStr + "\" (attendu : DISPONIBLE, LOUE, EN_ENTRETIEN).");
        }
        try {
            zone = Zone.valueOf(zoneStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DonneeInvalideException("Zone inconnue : \"" + zoneStr + "\" (attendu : MONTREAL, RIVE_NORD, RIVE_SUD).");
        }

        String typeCode = typeVehiculeStr == null ? "" : typeVehiculeStr.trim().toUpperCase();
        return switch (typeCode) {
            case "CAMIONNETTE" -> new CamionnetteUtilitaire(id.trim(), modele.trim(), typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            case "MINIVAN" -> new Minivan(id.trim(), modele.trim(), typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            case "CUBE" -> new CamionCube(id.trim(), modele.trim(), typeEnergie, capaciteKg,
                    kilometrage, statut, zone, chauffeurAssigne, tarifBase);
            default -> throw new DonneeInvalideException(
                    "Type de véhicule inconnu : \"" + typeVehiculeStr + "\" (attendu : CAMIONNETTE, MINIVAN, CUBE).");
        };
    }
}
