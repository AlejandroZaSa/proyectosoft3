package co.edu.uniquindio.barberiavip.controladores;

import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.TokenDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.CambioPasswordDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.LoginDTO;
import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.MensajeDTO;
import co.edu.uniquindio.barberiavip.dto.cliente.RegistroClienteDTO;
import co.edu.uniquindio.barberiavip.servicios.interfaces.AutenticacionServicio;
import co.edu.uniquindio.barberiavip.servicios.interfaces.BarberiaServicio;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AutenticacionController {

    private final ClienteServicio clienteServicio;
    private final AutenticacionServicio autenticacionServicio;
    private final BarberiaServicio barberiaServicio;

    @PostMapping("/login")
    public ResponseEntity<MensajeDTO<TokenDTO>> login(@Valid @RequestBody LoginDTO loginDTO)
            throws Exception {
        TokenDTO tokenDTO = autenticacionServicio.login(loginDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, tokenDTO));
    }

    @PostMapping("/registrar-cliente")
    public ResponseEntity<MensajeDTO<String>> registrarCliente(@Valid @RequestBody RegistroClienteDTO cliente) throws Exception{

        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Cliente registrado correctamente con el código " + clienteServicio.registrarse(cliente)));
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambioPasswordDTO cambioPasswordDTO) throws Exception{
        barberiaServicio.cambiarPassword(cambioPasswordDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Contraseña actualizada con éxito"));
    }
}
