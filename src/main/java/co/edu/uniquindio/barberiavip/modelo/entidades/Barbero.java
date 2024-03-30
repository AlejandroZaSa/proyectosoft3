package co.edu.uniquindio.barberiavip.modelo.entidades;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Barbero {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 10, unique = true)
    private String cedula;

    @Column(nullable = false, length = 30)
    private String nombreCompleto;

    @Column(nullable = false, length = 10)
    private String telefono;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    @Lob
    private String direccion;

    @OneToMany(mappedBy = "barbero")
    private List<Agenda> agendas;
}
