package pe.com.farmaciadey.producto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pe.com.farmaciadey.producto.models.Categoria;
import pe.com.farmaciadey.producto.models.Producto;
import pe.com.farmaciadey.producto.repository.CategoriaRepository;
import pe.com.farmaciadey.producto.repository.ProductoRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear categorías si no existen
        Categoria medicamentos = categoriaRepository.findAll().stream()
                .filter(c -> "Medicamentos".equals(c.getNombre()))
                .findFirst().orElse(null);
        
        if (medicamentos == null) {
            medicamentos = new Categoria();
            medicamentos.setNombre("Medicamentos");
            medicamentos.setDescripcion("Medicamentos y productos farmacéuticos");
            medicamentos.setEliminado(0);
            categoriaRepository.save(medicamentos);
            System.out.println("✅ Categoría Medicamentos creada");
        }

        Categoria cuidadoPersonal = categoriaRepository.findAll().stream()
                .filter(c -> "Cuidado Personal".equals(c.getNombre()))
                .findFirst().orElse(null);
        
        if (cuidadoPersonal == null) {
            cuidadoPersonal = new Categoria();
            cuidadoPersonal.setNombre("Cuidado Personal");
            cuidadoPersonal.setDescripcion("Productos de higiene y cuidado personal");
            cuidadoPersonal.setEliminado(0);
            categoriaRepository.save(cuidadoPersonal);
            System.out.println("✅ Categoría Cuidado Personal creada");
        }

        // Crear productos si no existen
        if (productoRepository.list().isEmpty()) {
            // Productos de Medicamentos
            Producto paracetamol = new Producto();
            paracetamol.setCodigo("MED-001");
            paracetamol.setNombre("Paracetamol 500mg");
            paracetamol.setDescripcion("Analgésico y antipirético para el alivio del dolor y la fiebre");
            paracetamol.setPrecio(8.50);
            paracetamol.setStock(50);
            paracetamol.setUrl("https://via.placeholder.com/200x200/87CEEB/000000?text=Paracetamol");
            paracetamol.setCategoria(medicamentos);
            paracetamol.setEliminado(0);
            productoRepository.save(paracetamol);

            Producto ibuprofeno = new Producto();
            ibuprofeno.setCodigo("MED-002");
            ibuprofeno.setNombre("Ibuprofeno 400mg");
            ibuprofeno.setDescripcion("Antiinflamatorio no esteroideo para dolor e inflamación");
            ibuprofeno.setPrecio(12.80);
            ibuprofeno.setStock(30);
            ibuprofeno.setUrl("https://via.placeholder.com/200x200/98FB98/000000?text=Ibuprofeno");
            ibuprofeno.setCategoria(medicamentos);
            ibuprofeno.setEliminado(0);
            productoRepository.save(ibuprofeno);

            Producto aspirina = new Producto();
            aspirina.setCodigo("MED-003");
            aspirina.setNombre("Aspirina 100mg");
            aspirina.setDescripcion("Ácido acetilsalicílico para prevención cardiovascular");
            aspirina.setPrecio(15.20);
            aspirina.setStock(25);
            aspirina.setUrl("https://via.placeholder.com/200x200/FFB6C1/000000?text=Aspirina");
            aspirina.setCategoria(medicamentos);
            aspirina.setEliminado(0);
            productoRepository.save(aspirina);

            // Productos de Cuidado Personal
            Producto shampoo = new Producto();
            shampoo.setCodigo("CP-001");
            shampoo.setNombre("Shampoo Anticaspa");
            shampoo.setDescripcion("Shampoo especializado para el tratamiento de la caspa");
            shampoo.setPrecio(25.90);
            shampoo.setStock(40);
            shampoo.setUrl("https://via.placeholder.com/200x200/FFD700/000000?text=Shampoo");
            shampoo.setCategoria(cuidadoPersonal);
            shampoo.setEliminado(0);
            productoRepository.save(shampoo);

            Producto pastaDental = new Producto();
            pastaDental.setCodigo("CP-002");
            pastaDental.setNombre("Pasta Dental Blanqueadora");
            pastaDental.setDescripcion("Pasta dental con acción blanqueadora y protección total");
            pastaDental.setPrecio(18.50);
            pastaDental.setStock(60);
            pastaDental.setUrl("https://via.placeholder.com/200x200/E6E6FA/000000?text=Pasta+Dental");
            pastaDental.setCategoria(cuidadoPersonal);
            pastaDental.setEliminado(0);
            productoRepository.save(pastaDental);

            Producto protectorSolar = new Producto();
            protectorSolar.setCodigo("CP-003");
            protectorSolar.setNombre("Protector Solar FPS 50");
            protectorSolar.setDescripcion("Protector solar de amplio espectro para todo tipo de piel");
            protectorSolar.setPrecio(35.90);
            protectorSolar.setStock(20);
            protectorSolar.setUrl("https://via.placeholder.com/200x200/FFA500/000000?text=Protector+Solar");
            protectorSolar.setCategoria(cuidadoPersonal);
            protectorSolar.setEliminado(0);
            productoRepository.save(protectorSolar);

            System.out.println("✅ Productos de prueba creados: 6 productos en 2 categorías");
        } else {
            System.out.println("✅ Productos ya existen en la base de datos");
        }
    }
}