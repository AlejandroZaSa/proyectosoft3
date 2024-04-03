package co.edu.uniquindio.barberiavip.controladores;

import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.MensajeDTO;
import co.edu.uniquindio.barberiavip.dto.cliente.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteServicio clienteServicio;

    @PostMapping("/solicitar-cita")
    public ResponseEntity<MensajeDTO<String>> agendarCita(@Valid @RequestBody SolicitudCitaDTO solicitudCitaDTO) throws Exception {
        clienteServicio.solicitarCita(solicitudCitaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Solicitud de cita creada con éxito"));
    }

    @PostMapping("/inscribir-curso")
    public ResponseEntity<MensajeDTO<String>> agendarCita(@Valid @RequestBody InscripcionCursoDTO inscripcionCursoDTO) throws Exception {
        clienteServicio.inscribirCurso(inscripcionCursoDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Inscripcion creada con éxito"));
    }

    @GetMapping("/cargar-inscripciones/{idCliente}")
    public ResponseEntity<MensajeDTO<List<ItemInscripcionCursoDTO>>> cargarInscripciones(@PathVariable int idCliente) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false, clienteServicio.cargarInscripciones(idCliente)));
    }

    @GetMapping("/cargar-citas/{idCliente}")
    public ResponseEntity<MensajeDTO<List<ItemSolicitudCitaDTO>>> cargarSolicitudesCitas(@PathVariable int idCliente) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false, clienteServicio.cargarCitas(idCliente)));
    }

    @PostMapping("/pagar")
    public ResponseEntity<MensajeDTO<String>> pagar(@Valid @RequestBody MetodoPagoDTO metodoPagoDTO) throws Exception {
        clienteServicio.pagar(metodoPagoDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Pago realizado con éxito"));
    }

}
