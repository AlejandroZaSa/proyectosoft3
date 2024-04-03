package co.edu.uniquindio.barberiavip.modelo.entidades;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cliente {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false)
    @Lob
    private String password;

    @Column(nullable = false)
    private boolean activo;

    @OneToMany(mappedBy = "cliente")
    private List<SolicitudCita> citas;

    @OneToMany(mappedBy = "cliente")
    private List<Inscripcion> inscripciones;

}
