package co.edu.uniquindio.barberiavip.controladores;

import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.MensajeDTO;
import co.edu.uniquindio.barberiavip.dto.cliente.*;
import co.edu.uniquindio.barberiavip.servicios.interfaces.ClienteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<MensajeDTO<String>> inscribirCurso(@Valid @RequestBody InscripcionCursoDTO inscripcionCursoDTO) throws Exception {
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

    @PostMapping("/pagar-cita")
    public ResponseEntity<MensajeDTO<String>> pagarCita(@Valid @RequestBody PagoCitaDTO pagoCitaDTO) throws Exception {
        clienteServicio.pagoCita(pagoCitaDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Pago realizado con éxito"));
    }

    @GetMapping ("/filtrar-barbero-cita/{fecha}")
    public ResponseEntity<MensajeDTO<List<ItemBarberoCitaDTO>>> pagar(@PathVariable LocalDate fecha) throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false, clienteServicio.filtrarBarberoCita(fecha)));
    }


    @PostMapping("/pagar-inscripcion")
    public ResponseEntity<MensajeDTO<String>> pagarInscripcion(@Valid @RequestBody PagoInscripcionDTO pagoInscripcionDTO) throws Exception {
        clienteServicio.pagoInscripcion(pagoInscripcionDTO);
        return ResponseEntity.ok().body( new MensajeDTO<>(false, "Pago realizado con éxito"));
    }

}
