package pe.com.farmaciadey.producto.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.farmaciadey.producto.models.Producto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductoCajaGrisIT {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCrearYListarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Caja Gris");
        producto.setDescripcion("Test");
        producto.setPrecio(10.0);
        producto.setStock(100);
        producto.setEliminado(0);

        // Crear producto
        webTestClient.post().uri("/api/producto/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(producto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.dato.nombre").isEqualTo("Caja Gris");

        // Listar productos y verificar que existe
        webTestClient.post().uri("/api/producto/list")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.dato[?(@.nombre=='Caja Gris')]").exists();
    }
}
