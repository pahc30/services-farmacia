package pe.com.farmaciadey.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.producto.models.Categoria;


public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT o FROM Categoria o WHERE eliminado = 0")
    public List<Categoria> list();
    @Query("SELECT o FROM Categoria o WHERE o.id = ?1")
    public Categoria find(Integer id);
}
