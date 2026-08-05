package drivio.utilitaires;

import drivio.modeles.Vehicule;
import drivio.modeles.CamionnetteUtilitaire;
import drivio.modeles.Minivan;
import drivio.modeles.CamionCube;
import drivio.exceptions.DonneeInvalideException;

public class ConstructeurVehicule {

    public static Vehicule creerVehicule(String id, String modele, String typeVehicule,
                                          String typeEnergie, String capaciteStr, String kilometrageStr,
                                          String statut, String zone, String chauffeurAssigne,
                                          String tarifBaseStr) throws DonneeInvalideException {

        if (id == null || id.isBlank() || modele == null || modele.isBlank()) {
            throw new DonneeInvalideException("L'id et le modele sont obligatoires.");
        }

        double capaciteKg;
        try {
            capaciteKg = Double.parseDouble(capaciteStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Capacite invalide : \"" + capaciteStr + "\" n'est pas un nombre.");
        }

        int kilometrage;
        try {
            kilometrage = Integer.parseInt(kilometrageStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Kilometrage invalide : \"" + kilometrageStr + "\" n'est pas un nombre entier.");
        }

        double tarifBase;
        try {
            tarifBase = Double.parseDouble(tarifBaseStr.trim());
        } catch (NumberFormatException e) {
            throw new DonneeInvalideException("Tarif de base invalide : \"" + tarifBaseStr + "\" n'est pas un nombre.");
        }

        if (kilometrage < 0) {
            throw new DonneeInvalideException("Le kilometrage ne peut pas etre negatif (" + kilometrage + ").");
        }
        if (capaciteKg <= 0) {
            throw new DonneeInvalideException("La capacite doit etre superieure a 0 (" + capaciteKg + ").");
        }
        if (tarifBase <= 0) {
            throw new DonneeInvalideException("Le tarif de base doit etre superieur a 0 (" + tarifBase + ").");
        }

        String energieValide = typeEnergie == null ? "" : typeEnergie.trim().toUpperCase();
        if (!energieValide.equals("ESSENCE") && !energieValide.equals("DIESEL") && !energieValide.equals("ELECTRIQUE")) {
            throw new DonneeInvalideException("Type d'energie inconnu : \"" + typeEnergie + "\" (attendu : ESSENCE, DIESEL, ELECTRIQUE).");
        }

        String statutValide = statut == null ? "" : statut.trim().toUpperCase();
        if (!statutValide.equals("DISPONIBLE") && !statutValide.equals("LOUE") && !statutValide.equals("EN_ENTRETIEN")) {
            throw new DonneeInvalideException("Statut inconnu : \"" + statut + "\" (attendu : DISPONIBLE, LOUE, EN_ENTRETIEN).");
        }

        String zoneValide = zone == null ? "" : zone.trim().toUpperCase();
        if (!zoneValide.equals("MONTREAL") && !zoneValide.equals("RIVE_NORD") && !zoneValide.equals("RIVE_SUD")) {
            throw new DonneeInvalideException("Zone inconnue : \"" + zone + "\" (attendu : MONTREAL, RIVE_NORD, RIVE_SUD).");
        }

        String typeValide = typeVehicule == null ? "" : typeVehicule.trim().toUpperCase();

        if (typeValide.equals("CAMIONNETTE")) {
            return new CamionnetteUtilitaire(id.trim(), modele.trim(), energieValide, capaciteKg,
                    kilometrage, statutValide, zoneValide, chauffeurAssigne, tarifBase);
        } else if (typeValide.equals("MINIVAN")) {
            return new Minivan(id.trim(), modele.trim(), energieValide, capaciteKg,
                    kilometrage, statutValide, zoneValide, chauffeurAssigne, tarifBase);
        } else if (typeValide.equals("CUBE")) {
            return new CamionCube(id.trim(), modele.trim(), energieValide, capaciteKg,
                    kilometrage, statutValide, zoneValide, chauffeurAssigne, tarifBase);
        } else {
            throw new DonneeInvalideException("Type de vehicule inconnu : \"" + typeVehicule + "\" (attendu : CAMIONNETTE, MINIVAN, CUBE).");
        }
    }
}
