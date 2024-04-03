package co.edu.uniquindio.barberiavip.dto.administrador;

import co.edu.uniquindio.barberiavip.modelo.enums.Dia;

import java.time.LocalTime;

public record ItemAgendaDTO(

        int idAgenda,

        Dia dia,

        LocalTime horaEntrada,

        LocalTime horaSalida
) {
}
