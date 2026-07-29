/**
 * Levée lorsqu'une opération de location est tentée sur un véhicule
 * qui n'est pas DISPONIBLE (déjà loué ou en entretien).
 */
public class VehiculeIndisponibleException extends Exception {
    public VehiculeIndisponibleException(String message) {
        super(message);
    }
}
