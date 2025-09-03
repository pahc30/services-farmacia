package pe.com.farmaciadey.usuario.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Usuario {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    @Column(nullable=false, length = 20)
    private String identificacion;
    @Column(nullable=false, length = 45)
    private String nombres;
    @Column(nullable=false, length = 45)
    private String apellidos;
    @Column(length = 45)
    private String telefono;
    @Column(length = 45)
    private String email;
    @Column(length = 100)
    private String direccion;
    @Column(nullable=false, length = 45)
    private String rol;
    @Column(unique = true, nullable = false, length = 45)
    private String username;
    @Column(nullable=false, length = 200)
    private String password;
    @Column(nullable=false)
    private Integer eliminado = 0;
}
