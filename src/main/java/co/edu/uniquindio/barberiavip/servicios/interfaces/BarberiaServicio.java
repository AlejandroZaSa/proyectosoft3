package co.edu.uniquindio.barberiavip.servicios.interfaces;

import co.edu.uniquindio.barberiavip.dto.barberia.ItemCursoDTO;
import co.edu.uniquindio.barberiavip.dto.barberia.ItemServicioDTO;
import co.edu.uniquindio.barberiavip.modelo.enums.Estado;

import java.util.List;

public interface BarberiaServicio {

    List<Estado> cargarListaEstados();

    List<ItemCursoDTO> listarCursos() throws Exception;

    List<ItemServicioDTO> listarServicios() throws Exception;
}
