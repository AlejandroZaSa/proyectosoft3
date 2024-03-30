package co.edu.uniquindio.barberiavip.controladores;

import co.edu.uniquindio.barberiavip.dto.autenticacionJwt.MensajeDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemCursoDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemServicioDTO;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.servicios.interfaces.BarberiaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/barberia")
@RequiredArgsConstructor
public class BarberiaController {

    private final BarberiaServicio barberiaServicio;
    @GetMapping("/estados")
    public ResponseEntity<MensajeDTO<List<Estado>>> cargarListaEstadosPqrs(){
        return ResponseEntity.ok().body( new MensajeDTO<>(false, barberiaServicio.cargarListaEstados()));
    }

    @GetMapping("/listar-cursos")
    public ResponseEntity<MensajeDTO<List<ItemCursoDTO>>> listarCursos() throws Exception{
        return ResponseEntity.ok().body(new MensajeDTO<>(false, barberiaServicio.listarCursos()));
    }

    @GetMapping("/listar-servicio")
    public ResponseEntity<MensajeDTO<List<ItemServicioDTO>>> listarServicios() throws Exception{
        return ResponseEntity.ok().body(new MensajeDTO<>(false, barberiaServicio.listarServicios()));
    }

}
