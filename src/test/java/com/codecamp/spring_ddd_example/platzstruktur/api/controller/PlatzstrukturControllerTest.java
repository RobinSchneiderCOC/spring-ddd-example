package com.codecamp.spring_ddd_example.platzstruktur.api.controller;

import com.codecamp.spring_ddd_example.platzstruktur.business.usecase.SitzplatzVerwaltenUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = PlatzstrukturController.class)
class PlatzstrukturControllerTest {

    RestTestClient client;

    @MockitoBean
    private SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void legtSitzplatzAn() {
        UUID sitzplatzId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        when(sitzplatzVerwaltenUseCase.legeSitzplatzAn(any(), eq("1B"))).thenReturn(sitzplatzId);

        client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", "VORNE", "nummer", "1B"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.sitzplatzId").isEqualTo("11111111-1111-1111-1111-111111111111");
    }

    @Test
    void sperrtSitzplatz() {
        UUID sitzplatzId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        client.post().uri("/platzstruktur/sitzplaetze/{sitzplatzId}/sperren", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(sitzplatzVerwaltenUseCase).sperreSitzplatz(sitzplatzId);
    }

    @Test
    void gibtSitzplatzFrei() {
        UUID sitzplatzId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        client.post().uri("/platzstruktur/sitzplaetze/{sitzplatzId}/freigeben", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(sitzplatzVerwaltenUseCase).gibSitzplatzFrei(sitzplatzId);
    }

    @Test
    void entferntSitzplatz() {
        UUID sitzplatzId = UUID.fromString("44444444-4444-4444-4444-444444444444");

        client.post().uri("/platzstruktur/sitzplaetze/{sitzplatzId}/entfernen", sitzplatzId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(sitzplatzVerwaltenUseCase).entferneSitzplatz(sitzplatzId);
    }
}
