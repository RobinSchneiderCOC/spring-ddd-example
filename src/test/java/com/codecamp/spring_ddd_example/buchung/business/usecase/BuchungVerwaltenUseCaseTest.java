package com.codecamp.spring_ddd_example.buchung.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.buchung.business.service.BuchungRegelwerkService;
import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzplatzAbfrageApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuchungVerwaltenUseCaseTest {

    @Mock
    private BuchungRepository buchungRepository;

    @Mock
    private SitzplatzAbfrageApi sitzplatzAbfrageApi;

    @Mock
    private BuchungRegelwerkService buchungRegelwerkService;

    @InjectMocks
    private BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @Test
    void buchtSitzplaetzeUndGibtIdZurueck() {
        UUID sitzplatzId = UUID.randomUUID();
        UUID buchungId = UUID.randomUUID();
        Buchung buchungMock = mock(Buchung.class);
        when(buchungMock.getId()).thenReturn(buchungId);
        when(buchungRepository.save(any(Buchung.class))).thenReturn(buchungMock);

        UUID result = buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(sitzplatzId));

        assertThat(result).isEqualTo(buchungId);
        verify(sitzplatzAbfrageApi).pruefeSitzplaetzeNichtGesperrt(Set.of(sitzplatzId));
        verify(buchungRegelwerkService).pruefeKeineAktiveBuchungFuerSitzplaetze(Set.of(sitzplatzId));
        verify(buchungRepository).save(any(Buchung.class));
    }

    @Test
    void wirftException_wennSitzplatzGesperrt() {
        UUID sitzplatzId = UUID.randomUUID();
        doThrow(new DomainException("Gesperrte Sitze können nicht gebucht werden"))
                .when(sitzplatzAbfrageApi).pruefeSitzplaetzeNichtGesperrt(Set.of(sitzplatzId));

        assertThatThrownBy(() -> buchungVerwaltenUseCase.bucheSitzplaetze(Set.of(sitzplatzId)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Gesperrte Sitze können nicht gebucht werden");
    }

    @Test
    void storniertBuchungErfolgreich() {
        UUID buchungId = UUID.randomUUID();
        Buchung buchung = Buchung.neu(Set.of(UUID.randomUUID()));
        when(buchungRepository.findById(buchungId)).thenReturn(Optional.of(buchung));

        buchungVerwaltenUseCase.storniereBuchung(buchungId);

        assertThat(buchung.istAktiv()).isFalse();
        verify(buchungRepository).save(buchung);
    }

    @Test
    void wirftException_wennBuchungNichtGefunden() {
        UUID buchungId = UUID.randomUUID();
        when(buchungRepository.findById(buchungId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buchungVerwaltenUseCase.storniereBuchung(buchungId))
                .isInstanceOf(DomainException.class)
                .hasMessage("Buchung nicht gefunden");
    }
}
