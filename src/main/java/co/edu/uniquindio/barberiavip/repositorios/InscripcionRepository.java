package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Integer> {

    @Query("select i from Inscripcion i where i.cliente.id=:codigoCliente")
    List<Inscripcion> buscarInscripcionesCliente(int codigoCliente);
}
