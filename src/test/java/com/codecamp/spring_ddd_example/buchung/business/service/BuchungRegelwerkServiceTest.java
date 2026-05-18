package com.codecamp.spring_ddd_example.buchung.business.service;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuchungRegelwerkServiceTest {

    @Mock
    private BuchungRepository buchungRepository;

    @InjectMocks
    private BuchungRegelwerkService service;

    @Test
    void wirdNichtGeworfen_wennKeineSitzplaetzeGebucht() {
        UUID sitzplatzId = UUID.randomUUID();
        when(buchungRepository.existsAktiveBuchungFuerSitzplatz(sitzplatzId)).thenReturn(false);

        assertThatCode(() -> service.pruefeKeineAktiveBuchungFuerSitzplaetze(Set.of(sitzplatzId)))
                .doesNotThrowAnyException();
    }

    @Test
    void wirftException_wennSitzplatzBereitsGebucht() {
        UUID sitzplatzId = UUID.randomUUID();
        when(buchungRepository.existsAktiveBuchungFuerSitzplatz(sitzplatzId)).thenReturn(true);

        assertThatThrownBy(() -> service.pruefeKeineAktiveBuchungFuerSitzplaetze(Set.of(sitzplatzId)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Sitzplatz ist bereits gebucht");
    }
}
