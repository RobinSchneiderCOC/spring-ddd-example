package com.codecamp.spring_ddd_example.buchung.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import com.codecamp.spring_ddd_example.platzstruktur.business.usecase.SitzplatzVerwaltenUseCase;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BuchungVerwaltenUseCaseIntegrationTest {

    private static final UUID SITZPLATZ_FREI_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SITZPLATZ_FREI_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID SITZPLATZ_GESPERRT = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Autowired
    private BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @Test
    void buchtMehrereFreieSitzplaetzeErfolgreich() {
        UUID buchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1, SITZPLATZ_FREI_2));

        Buchung buchung = buchungRepository.findById(buchungId).orElseThrow();
        assertThat(buchung.istAktiv()).isTrue();
        assertThat(buchung.sitzplatzIds()).containsExactlyInAnyOrder(SITZPLATZ_FREI_1, SITZPLATZ_FREI_2);
    }

    @Test
    void verhindertBuchungGesperrterSitzplaetze() {
        assertThatThrownBy(() -> buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_GESPERRT)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Gesperrte Sitze können nicht gebucht werden");
    }

    @Test
    void verhindertDoppelbuchungEinesSitzplatzes() {
        buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1));

        assertThatThrownBy(() -> buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Sitzplatz ist bereits gebucht");
    }

    @Test
    void stornierenSetzsBuchungAufStorniert() {
        UUID buchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1));

        buchungVerwaltenUseCase.storniereBuchung(buchungId);

        assertThat(buchungRepository.findById(buchungId).orElseThrow().istAktiv()).isFalse();
    }

    @Test
    void nachStornierungKannSitzplatzWiederGebuchtWerden() {
        UUID buchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1));
        buchungVerwaltenUseCase.storniereBuchung(buchungId);

        UUID neueBuchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(SITZPLATZ_FREI_1));

        assertThat(buchungRepository.findById(neueBuchungId).orElseThrow().istAktiv()).isTrue();
    }

    @Test
    void erstelltNeuenSitzplatzUndBuchtIhn() {
        UUID neuerSitzplatzId = sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.MITTE, "9Z");

        UUID buchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(neuerSitzplatzId));

        assertThat(buchungRepository.findById(buchungId).orElseThrow().istAktiv()).isTrue();
    }
}
