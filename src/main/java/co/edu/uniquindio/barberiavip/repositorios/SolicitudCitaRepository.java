package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Inscripcion;
import co.edu.uniquindio.barberiavip.modelo.entidades.SolicitudCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudCitaRepository extends JpaRepository<SolicitudCita, Integer> {

    @Query("select s from SolicitudCita s where s.cliente.id=:codigoCliente")
    List<SolicitudCita> buscarCitasCliente(int codigoCliente);
}
