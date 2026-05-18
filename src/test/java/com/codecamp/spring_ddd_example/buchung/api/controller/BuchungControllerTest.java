package com.codecamp.spring_ddd_example.buchung.api.controller;

import com.codecamp.spring_ddd_example.buchung.business.usecase.BuchungVerwaltenUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BuchungController.class)
class BuchungControllerTest {

    RestTestClient client;

    @MockitoBean
    private BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void buchtSitzplaetzeUndGibtBuchungIdZurueck() {
        UUID sitzplatzId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID buchungId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        when(buchungVerwaltenUseCase.bucheSitzplaetze(any())).thenReturn(buchungId);

        client.post().uri("/buchungen/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("sitzplatzIds", List.of(sitzplatzId.toString())))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.buchungId").isEqualTo("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    }

    @Test
    void storniertBuchung() {
        UUID buchungId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

        client.post().uri("/buchungen/{buchungId}/stornieren", buchungId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(buchungVerwaltenUseCase).storniereBuchung(buchungId);
    }
}
