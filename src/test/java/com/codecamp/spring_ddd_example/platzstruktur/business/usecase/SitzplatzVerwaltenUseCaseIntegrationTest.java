package com.codecamp.spring_ddd_example.platzstruktur.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SitzplatzVerwaltenUseCaseIntegrationTest {

    @Autowired
    private SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @Autowired
    private SitzplatzRepository sitzplatzRepository;

    @Test
    void verwaltetSitzplatzUeberDenGesamtenLebenszyklus() {
        UUID sitzplatzId = sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.VORNE, "9Z");

        Sitzplatz angelegterSitz = sitzplatzRepository.findById(sitzplatzId).orElseThrow();
        assertThat(angelegterSitz.istGesperrt()).isFalse();
        assertThat(angelegterSitz.getNummer()).isEqualTo(new SitzplatzNummer(9, "Z"));

        sitzplatzVerwaltenUseCase.sperreSitzplatz(sitzplatzId);
        assertThat(sitzplatzRepository.findById(sitzplatzId).orElseThrow().istGesperrt()).isTrue();

        sitzplatzVerwaltenUseCase.gibSitzplatzFrei(sitzplatzId);
        assertThat(sitzplatzRepository.findById(sitzplatzId).orElseThrow().istGesperrt()).isFalse();

        sitzplatzVerwaltenUseCase.entferneSitzplatz(sitzplatzId);
        assertThat(sitzplatzRepository.findById(sitzplatzId)).isEmpty();
    }

    @Test
    void verhindertDoppelteSitznummerImSelbenBereich() {
        sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.MITTE, "8C");

        assertThatThrownBy(() -> sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.MITTE, "8C"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Sitznummer existiert in diesem Bereich bereits");
    }
}
