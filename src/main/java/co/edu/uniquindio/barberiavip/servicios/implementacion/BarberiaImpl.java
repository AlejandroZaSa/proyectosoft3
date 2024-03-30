package co.edu.uniquindio.barberiavip.servicios.implementacion;

import co.edu.uniquindio.barberiavip.dto.administrador.CursoDTO;
import co.edu.uniquindio.barberiavip.dto.administrador.ServicioDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemCursoDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemServicioDTO;
import co.edu.uniquindio.barberiavip.modelo.entidades.Curso;
import co.edu.uniquindio.barberiavip.modelo.entidades.Servicio;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import co.edu.uniquindio.barberiavip.repositorios.CursoRepository;
import co.edu.uniquindio.barberiavip.repositorios.ServicioRepository;
import co.edu.uniquindio.barberiavip.servicios.interfaces.BarberiaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BarberiaImpl implements BarberiaServicio {

    private final CursoRepository cursoRepository;
    private final ServicioRepository servicioRepository;

    @Override
    public List<Estado> cargarListaEstados() {
        return List.of(Estado.values());
    }

    @Override
    public List<ItemCursoDTO> listarCursos() throws Exception {

        List<Curso> cursos = cursoRepository.findAllActivos(true);

        if (cursos.isEmpty()) {
            throw new Exception("No hay cursos registrados");
        }

        List<ItemCursoDTO> listaCursos = new ArrayList<>();

        for (Curso c : cursos) {

            listaCursos.add(new ItemCursoDTO(

                    c.getId(),
                    c.getCosto(),
                    c.getNombre(),
                    c.getDescripcion(),
                    c.getFechaInicio(),
                    c.getFechaFin()
            ));

        }

        return listaCursos;
    }

    @Override
    public List<ItemServicioDTO> listarServicios() throws Exception {

        List<Servicio> servicios = servicioRepository.findAllActivos(true);

        if (servicios.isEmpty()) {
            throw new Exception("No hay servicios registrados");
        }

        List<ItemServicioDTO> listaServicios = new ArrayList<>();

        for (Servicio s : servicios) {

            listaServicios.add(new ItemServicioDTO(

                    s.getId(),
                    s.getDescripcion(),
                    s.getCosto()
            ));

        }

        return listaServicios;
    }
}
