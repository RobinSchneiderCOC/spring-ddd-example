package com.codecamp.spring_ddd_example.platzstruktur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PlatzstrukturE2ETest {

    @LocalServerPort
    private int port;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void legtSitzplatzAn() {
        client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", "MITTE", "nummer", "91A"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(result ->
                        assertThat(result.getResponseBody()).containsKey("sitzplatzId"));
    }

    @Test
    void sperreSitzplatz() {
        UUID sitzplatzId = legeNeuenSitzplatzAn("VORNE", "92A");

        client.post().uri("/platzstruktur/sitzplaetze/{id}/sperren", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void gibSitzplatzFrei() {
        UUID sitzplatzId = legeNeuenSitzplatzAn("VORNE", "93B");
        client.post().uri("/platzstruktur/sitzplaetze/{id}/sperren", sitzplatzId)
                .exchange().expectStatus().isNoContent().expectBody().isEmpty();

        client.post().uri("/platzstruktur/sitzplaetze/{id}/freigeben", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void entferneSitzplatz() {
        UUID sitzplatzId = legeNeuenSitzplatzAn("HINTEN", "94A");

        client.post().uri("/platzstruktur/sitzplaetze/{id}/entfernen", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void verhindertDoppelteSitznummerImSelbenBereich() {
        legeNeuenSitzplatzAn("HINTEN", "96A");

        client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", "HINTEN", "nummer", "96A"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(Void.class);
    }

    // --- Hilfsmethoden ---

    @SuppressWarnings("unchecked")
    private UUID legeNeuenSitzplatzAn(String bereichTyp, String nummer) {
        Map<String, Object> body = client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", bereichTyp, "nummer", nummer))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody();
        return UUID.fromString((String) body.get("sitzplatzId"));
    }
}
