package co.edu.uniquindio.barberiavip.servicios.interfaces;

import co.edu.uniquindio.barberiavip.dto.barberia.EmailDTO;

public interface EmailServicio {
    void enviarEmail(EmailDTO emailDTO) throws Exception;
}
