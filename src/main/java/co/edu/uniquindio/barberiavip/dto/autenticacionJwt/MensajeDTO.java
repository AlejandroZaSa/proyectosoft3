package co.edu.uniquindio.barberiavip.dto.autenticacionJwt;


public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}