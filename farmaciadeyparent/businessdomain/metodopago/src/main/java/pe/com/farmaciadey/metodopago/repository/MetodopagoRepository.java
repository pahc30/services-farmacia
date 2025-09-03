package pe.com.farmaciadey.metodopago.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pe.com.farmaciadey.metodopago.models.Metodopago;

public interface MetodopagoRepository extends JpaRepository<Metodopago, Integer> {
    @Query("SELECT o FROM Metodopago o WHERE eliminado = 0")
    public List<Metodopago> list();
    @Query("SELECT o FROM Metodopago o WHERE o.id = ?1")
    public Metodopago find(Integer id);
}
