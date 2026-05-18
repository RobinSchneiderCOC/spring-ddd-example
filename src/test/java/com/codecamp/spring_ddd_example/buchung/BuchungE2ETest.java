package com.codecamp.spring_ddd_example.buchung;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BuchungE2ETest {

    @LocalServerPort
    private int port;

    private RestTestClient client;

    private static final AtomicInteger COUNTER = new AtomicInteger(200);

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void bucheSitzplatz() {
        UUID sitzplatzId = legeNeuenSitzplatzAn();

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId.toString())))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result ->
                        assertThat(result.getResponseBody()).containsKey("buchungId"));
    }

    @Test
    void storniereBuchung() {
        UUID sitzplatzId = legeNeuenSitzplatzAn();
        UUID buchungId = bucheSitzplaetzeUndHoleBuchungId(sitzplatzId);

        client.post().uri("/buchungen/{id}/stornieren", buchungId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void verhindertBuchungGesperrterSitzplaetze() {
        UUID sitzplatzId = legeNeuenSitzplatzAn();
        client.post().uri("/platzstruktur/sitzplaetze/{id}/sperren", sitzplatzId)
                .exchange().expectStatus().isNoContent().expectBody().isEmpty();

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId.toString())))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Void.class);
    }

    @Test
    void verhindertDoppelbuchungEinesSitzplatzes() {
        UUID sitzplatzId = legeNeuenSitzplatzAn();
        bucheSitzplaetzeUndHoleBuchungId(sitzplatzId);

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId.toString())))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Void.class);
    }

    @Test
    void nachStornierungKannSitzplatzWiederGebuchtWerden() {
        UUID sitzplatzId = legeNeuenSitzplatzAn();
        UUID buchungId = bucheSitzplaetzeUndHoleBuchungId(sitzplatzId);
        client.post().uri("/buchungen/{id}/stornieren", buchungId)
                .exchange().expectStatus().isNoContent().expectBody().isEmpty();

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId.toString())))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result ->
                        assertThat(result.getResponseBody()).containsKey("buchungId"));
    }

    @Test
    void buchtMehrereSitzplaetzeInEinerBuchung() {
        UUID sitzplatzId1 = legeNeuenSitzplatzAn();
        UUID sitzplatzId2 = legeNeuenSitzplatzAn();

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId1.toString(), sitzplatzId2.toString())))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result ->
                        assertThat(result.getResponseBody()).containsKey("buchungId"));
    }

    // --- Hilfsmethoden ---

    @SuppressWarnings("unchecked")
    private UUID legeNeuenSitzplatzAn() {
        int num = COUNTER.getAndIncrement();
        Map<String, Object> body = client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", "MITTE", "nummer", num + "A"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        return UUID.fromString((String) body.get("sitzplatzId"));
    }

    @SuppressWarnings("unchecked")
    private UUID bucheSitzplaetzeUndHoleBuchungId(UUID... sitzplatzIds) {
        List<String> ids = Arrays.stream(sitzplatzIds).map(UUID::toString).toList();
        Map<String, Object> body = client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", ids))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        return UUID.fromString((String) body.get("buchungId"));
    }
}
