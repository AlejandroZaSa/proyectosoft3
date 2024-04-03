package co.edu.uniquindio.barberiavip.servicios.interfaces;

import co.edu.uniquindio.barberiavip.dto.barberia.LoginDTO;
import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.TokenDTO;

public interface AutenticacionServicio {
    TokenDTO login(LoginDTO loginDTO) throws Exception;

}
