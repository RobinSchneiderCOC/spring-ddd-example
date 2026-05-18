package com.codecamp.spring_ddd_example.across.exception;

import com.codecamp.spring_ddd_example.platzstruktur.api.controller.PlatzstrukturController;
import com.codecamp.spring_ddd_example.platzstruktur.business.usecase.SitzplatzVerwaltenUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@WebMvcTest(controllers = PlatzstrukturController.class)
class GlobalExceptionHandlerTest {

    RestTestClient client;

    @MockitoBean
    SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @BeforeEach
    void setUp(WebApplicationContext context) {
        client = RestTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void mapptDomainExceptionAuf400MitProblemDetail() {
        doThrow(new DomainException("Sitzplatz nicht gefunden"))
                .when(sitzplatzVerwaltenUseCase).sperreSitzplatz(any());

        client.post().uri("/platzstruktur/sitzplaetze/{id}/sperren", UUID.randomUUID())
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.title").isEqualTo("Domain validation failed")
                .jsonPath("$.detail").isEqualTo("Sitzplatz nicht gefunden");
    }

    @Test
    void mapptDomainExceptionMitKorrekterFehlermeldung() {
        doThrow(new DomainException("Die Sitznummer existiert in diesem Bereich bereits"))
                .when(sitzplatzVerwaltenUseCase).legeSitzplatzAn(any(), any());

        client.post().uri("/platzstruktur/sitzplaetze/anlegen")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("bereichTyp", "MITTE", "nummer", "1A"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Domain validation failed")
                .jsonPath("$.detail").isEqualTo("Die Sitznummer existiert in diesem Bereich bereits");
    }
}
