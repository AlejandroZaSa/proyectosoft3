package co.edu.uniquindio.barberiavip.modelo.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("clientes")
public class ClienteMongo implements Serializable {

    @Transient
    public static final String SEQUENCE_NAME = "clientes_sequence";

    @Id
    private long id;

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

}
