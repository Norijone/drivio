import java.util.List;
import java.util.Map;

/**
 * Construit le contenu texte du rapport de flotte. Séparée de Main et de la
 * GUI (SRP) pour éviter la duplication entre la version console et la GUI.
 */
public class RapportGenerateur {

    public static String genererContenu(List<Vehicule> vehicules, StatistiquesFlotte stats,
                                         GestionnaireAlertes alertes) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPPORT DRIVIO - FLOTTE DE VEHICULES ===\n\n");
        sb.append("Nombre total de véhicules : ").append(vehicules.size()).append("\n");
        sb.append(String.format("Revenu total généré : %.2f$%n", stats.revenuTotal()));
        sb.append(String.format("Kilométrage moyen : %.1f km%n", stats.kilometrageMoyen()));

        sb.append("\n--- Taux d'utilisation par type ---\n");
        for (Map.Entry<String, Double> entry : stats.tauxUtilisationParType().entrySet()) {
            sb.append(String.format("%s : %.1f jours%n", entry.getKey(), entry.getValue()));
        }

        sb.append("\n--- Répartition par zone ---\n");
        for (Map.Entry<String, Long> entry : stats.repartitionParZone().entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" véhicule(s)\n");
        }

        sb.append("\n--- Véhicules nécessitant un entretien ---\n");
        for (Vehicule v : stats.vehiculesNecessitantEntretien()) {
            sb.append(v.getId()).append(" (").append(v.getModele()).append(")\n");
        }

        sb.append("\n--- Alertes actives ---\n");
        for (String a : alertes.genererAlertes()) {
            sb.append(a).append("\n");
        }

        return sb.toString();
    }
}
