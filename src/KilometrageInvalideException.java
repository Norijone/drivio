/**
 * Levée lorsqu'un kilométrage négatif, nul de façon incohérente,
 * ou inférieur au kilométrage déjà enregistré est fourni.
 */
public class KilometrageInvalideException extends Exception {
    public KilometrageInvalideException(String message) {
        super(message);
    }
}
