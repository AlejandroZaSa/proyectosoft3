package co.edu.uniquindio.barberiavip.dto.cliente;

import java.time.LocalTime;

public record ItemBarberoCitaDTO(int codigoBarbero, String nombreBarbero, LocalTime hora) {
}
