package pe.com.farmaciadey.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.producto.models.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    @Query("SELECT o FROM Producto o WHERE eliminado = 0 AND o.categoria.eliminado = 0")
    public List<Producto> list();
    @Query("SELECT o FROM Producto o WHERE o.id = ?1")
    public Producto find(Integer id);
}
