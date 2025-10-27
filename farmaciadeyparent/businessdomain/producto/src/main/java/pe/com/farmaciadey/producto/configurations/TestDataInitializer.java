package pe.com.farmaciadey.producto.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.com.farmaciadey.producto.models.Categoria;
import pe.com.farmaciadey.producto.models.Producto;
import pe.com.farmaciadey.producto.repository.CategoriaRepository;
import pe.com.farmaciadey.producto.repository.ProductoRepository;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void run(String... args) {
        Categoria cat = categoriaRepository.findAll().stream().findFirst().orElse(null);
        if (cat == null) {
            cat = new Categoria();
            cat.setNombre("Test Categoria");
            cat.setDescripcion("Categoria creada para pruebas");
            categoriaRepository.save(cat);
        }

        if (productoRepository.list().isEmpty()) {
            Producto p = new Producto();
            p.setCodigo("TEST-PROD-1");
            p.setNombre("Producto de Prueba");
            p.setDescripcion("Descripción de producto de prueba");
            p.setPrecio(9.99);
            p.setStock(100);
            p.setUrl("http://localhost/test-image.png");
            p.setCategoria(cat);
            productoRepository.save(p);
        }
    }
}
