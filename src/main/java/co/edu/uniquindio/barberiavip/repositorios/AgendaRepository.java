package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Agenda;
import co.edu.uniquindio.barberiavip.modelo.enums.Dia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    @Query("select a from Agenda a where a.barbero.id = :idBarbero")
    List<Agenda> buscarAgendaBarbero(int idBarbero);

    List<Agenda> findAllByBarberoId(int id);

    @Query("select a from Agenda a where a.barbero.id = :codBarbero and a.dia = :dia")
    Agenda obtenerAgendaFecha(int codBarbero, Dia dia);
}
