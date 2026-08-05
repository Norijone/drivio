package drivio.utilitaires;

import java.util.ArrayList;

import drivio.modeles.Vehicule;
import drivio.gestion.StatistiquesFlotte;
import drivio.gestion.GestionnaireAlertes;

public class GenerateurRapport {

    public static String genererContenu(ArrayList<Vehicule> vehicules, StatistiquesFlotte stats,
                                         GestionnaireAlertes alertes) {
        String contenu = "=== RAPPORT DRIVIO - FLOTTE DE VEHICULES ===\n\n";
        contenu = contenu + "Nombre total de vehicules : " + vehicules.size() + "\n";
        contenu = contenu + "Revenu total genere : " + stats.revenuTotal() + "$\n";
        contenu = contenu + "Kilometrage moyen : " + stats.kilometrageMoyen() + " km\n";

        contenu = contenu + "\n--- Taux d'utilisation par type ---\n";
        ArrayList<String> types = stats.getTypesUniques();
        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            contenu = contenu + type + " : " + stats.tauxUtilisationPourType(type) + " jours\n";
        }

        contenu = contenu + "\n--- Repartition par zone ---\n";
        ArrayList<String> zones = stats.getZonesUniques();
        for (int i = 0; i < zones.size(); i++) {
            String zone = zones.get(i);
            contenu = contenu + zone + " : " + stats.nombreVehiculesDansZone(zone) + " vehicule(s)\n";
        }

        contenu = contenu + "\n--- Vehicules necessitant un entretien ---\n";
        ArrayList<Vehicule> aEntretenir = stats.vehiculesNecessitantEntretien();
        for (int i = 0; i < aEntretenir.size(); i++) {
            Vehicule v = aEntretenir.get(i);
            contenu = contenu + v.getId() + " (" + v.getModele() + ")\n";
        }

        contenu = contenu + "\n--- Alertes actives ---\n";
        ArrayList<String> listeAlertes = alertes.genererAlertes();
        for (int i = 0; i < listeAlertes.size(); i++) {
            contenu = contenu + listeAlertes.get(i) + "\n";
        }

        return contenu;
    }
}
