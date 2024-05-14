package co.edu.uniquindio.barberiavip.modelo.entidades;

import co.edu.uniquindio.barberiavip.modelo.enums.Estado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SolicitudCita {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private float costo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @Column(nullable = false)
    private Estado estado;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Cliente cliente;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Barbero barbero;

    @ManyToMany
    private Set<Servicio> servicios;

    @OneToOne
    private Pago pago;

}
