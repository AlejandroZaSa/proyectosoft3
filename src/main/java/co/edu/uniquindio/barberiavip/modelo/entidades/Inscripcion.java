package co.edu.uniquindio.barberiavip.modelo.entidades;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inscripcion {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate fechaInscripcion;

    @Column(nullable = false)
    private float costo;

    @Column(nullable = false)
    private Estado estado;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Curso curso;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Cliente cliente;

    @OneToOne(mappedBy = "inscripcion")
    private Pago pago;

}
