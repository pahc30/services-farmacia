package pe.com.farmaciadey.usuario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.usuario.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    @Query("SELECT o FROM Usuario o WHERE eliminado = 0")
    List<Usuario> list();
    Usuario findByUsername(String username);
    Usuario findByIdentificacion(String identificacion);
    @Query("SELECT o FROM Usuario o WHERE o.id = ?1")
    public Usuario find(Integer id);
}
