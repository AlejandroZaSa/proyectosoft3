package co.edu.uniquindio.barberiavip.controladores;

import co.edu.uniquindio.barberiavip.dto.administrador.*;
import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.MensajeDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemCursoDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemServicioDTO;
import co.edu.uniquindio.barberiavip.servicios.interfaces.AdministradorServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/administrador")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorServicio administradorServicio;

    @PostMapping("/crear-curso")
    public ResponseEntity<MensajeDTO<String>> crearCurso(@Valid @RequestBody CursoDTO cursoDTO) throws Exception {
        administradorServicio.crearCurso(cursoDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Curso registrado correctamente"));
    }

    @PostMapping("/crear-servicio")
    public ResponseEntity<MensajeDTO<String>> crearServicio(@Valid @RequestBody ServicioDTO servicioDTO) throws Exception {
        administradorServicio.crearServicio(servicioDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Servicio registrado correctamente"));
    }

    @PutMapping("/actualizar-curso/{codigoCurso}")
    public ResponseEntity<MensajeDTO<String>> actualizarCurso(@PathVariable int codigoCurso, @Valid @RequestBody CursoDTO cursoDTO) throws Exception {
        administradorServicio.actualizarCurso(codigoCurso, cursoDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Curso actualizado correctamente"));
    }

    @PutMapping("/actualizar-servicio/{codigoServicio}")
    public ResponseEntity<MensajeDTO<String>> actualizarServicio(@PathVariable int codigoServicio, @Valid @RequestBody ServicioDTO servicioDTO) throws Exception {
        administradorServicio.actualizarServicio(codigoServicio, servicioDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Servicio actualizado correctamente"));
    }

    @DeleteMapping("/eliminar-curso/{codigoCurso}")
    public ResponseEntity<MensajeDTO<String>> eliminarCurso(@PathVariable int codigoCurso) throws Exception {
        administradorServicio.eliminarCurso(codigoCurso);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Curso eliminado correctamente"));
    }

    @DeleteMapping("/eliminar-servicio/{codigoServicio}")
    public ResponseEntity<MensajeDTO<String>> eliminarServicio(@PathVariable int codigoServicio) throws Exception {
        administradorServicio.eliminarServicio(codigoServicio);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Servicio eliminado correctamente"));
    }

    @GetMapping("/obtener-curso/{codigoCurso}")
    public ResponseEntity<MensajeDTO<CursoDTO>> obtenerCurso(@PathVariable int codigoCurso) throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, administradorServicio.obtenerCurso(codigoCurso)));
    }

    @PostMapping("/actualizar-agenda/{codigoBarbero}")
    public ResponseEntity<MensajeDTO<String>> actualizarAgenda(@PathVariable int codigoBarbero, @Valid @RequestBody List<ItemAgendaDTO> agenda) throws Exception {
        administradorServicio.actualizarAgenda(codigoBarbero, agenda);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Agenda actualizado correctamente"));
    }

    @GetMapping("/obtener-servicio/{codigoServicio}")
    public ResponseEntity<MensajeDTO<ServicioDTO>> obtenerServicio(@PathVariable int codigoServicio) throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, administradorServicio.obtenerServicio(codigoServicio)));
    }

    @PutMapping("/cambiar-estado-inscripcion")
    public ResponseEntity<MensajeDTO<String>> cambiarEstadoInscripcion(@Valid @RequestBody EstadoInscripcionDTO estadoInscripcionDTO) throws Exception {
        administradorServicio.cambiarEstadoInscripcion(estadoInscripcionDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Estado actualizado correctamente"));
    }

    @PutMapping("/cambiar-estado-cita")
    public ResponseEntity<MensajeDTO<String>> cambiarEstadoServicio(@Valid @RequestBody EstadoCitaDTO estadoCitaDTO) throws Exception {
        administradorServicio.cambiarEstadoCita(estadoCitaDTO);
        return ResponseEntity.ok().body(new MensajeDTO<>(false, "Estado actualizado correctamente"));
    }

    @GetMapping("/cargar-agenda/{idBarbero}")
    public ResponseEntity<MensajeDTO<List<ItemAgendaDTO>>> cargarAgenda(@PathVariable int idBarbero) throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, administradorServicio.cargarAgenda(idBarbero)));
    }

    @GetMapping("/listar-servicios")
    public ResponseEntity<MensajeDTO<List<ItemServicioDTO>>> cargarServicios() throws Exception {
        return ResponseEntity.ok().body(new MensajeDTO<>(false, administradorServicio.listarServicios()));
    }

    @GetMapping("/listar-cursos")
    public ResponseEntity<MensajeDTO<List<ItemCursoDTO>>> cargarCursos() throws Exception {
        return ResponseEntity.ok().body( new MensajeDTO<>(false, administradorServicio.listarCursos()));
    }
}
