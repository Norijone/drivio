/**
 * Levée lors du chargement du CSV lorsqu'une ligne contient
 * des données manquantes, mal formées ou incohérentes.
 */
public class DonneeInvalideException extends Exception {
    public DonneeInvalideException(String message) {
        super(message);
    }
}
