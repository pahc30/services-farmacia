package pe.com.farmaciadey.metodopago.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.farmaciadey.metodopago.models.Metodopago;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MetodopagoCajaGrisIT {
    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCrearYListarMetodopago() {
        Metodopago metodopago = new Metodopago();
    metodopago.setTipo("Caja Gris");
        metodopago.setDescripcion("Test");
        metodopago.setEliminado(0);

        // Crear metodopago
        webTestClient.post().uri("/api/metodopago/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(metodopago)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.dato.nombre").isEqualTo("Caja Gris");

        // Listar metodopagos y verificar que existe
        webTestClient.post().uri("/api/metodopago/list")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.dato[?(@.nombre=='Caja Gris')]").exists();
    }
}
