package co.edu.uniquindio.barberiavip.repositorios;

import co.edu.uniquindio.barberiavip.modelo.entidades.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Integer> {

    @Query("select a from Agenda a where a.barbero.id = :idBarbero")
    List<Agenda> buscarAgendaBarbero(int idBarbero);
}
