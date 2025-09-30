package pe.com.farmaciadey.compra.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.farmaciadey.compra.models.Compra;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompraCajaGrisIT {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCrearYListarCompra() {
        Compra compra = new Compra();
        compra.setEstado("Caja Gris");
        compra.setTotal(100.0);
        compra.setEliminado(0);

        // Crear compra
        webTestClient.post().uri("/api/compra/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(compra)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.dato.estado").isEqualTo("Caja Gris");

        // Listar compras y verificar que existe
        webTestClient.post().uri("/api/compra/list")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.dato[?(@.estado=='Caja Gris')]").exists();
    }
}
