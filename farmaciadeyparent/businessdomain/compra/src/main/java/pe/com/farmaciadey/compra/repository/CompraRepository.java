package pe.com.farmaciadey.compra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.compra.models.Compra;


public interface CompraRepository extends JpaRepository<Compra, Integer> {
    @Query("SELECT o FROM Compra o WHERE eliminado = 0 AND usuarioId = ?1 ORDER BY o.fecha DESC")
    List<Compra> list(Integer usuarioId);
}
