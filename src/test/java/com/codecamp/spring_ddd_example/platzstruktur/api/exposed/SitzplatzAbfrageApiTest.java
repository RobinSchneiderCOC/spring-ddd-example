package com.codecamp.spring_ddd_example.platzstruktur.api.exposed;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SitzplatzAbfrageApiTest {

    @Mock
    private SitzplatzRepository sitzplatzRepository;

    @InjectMocks
    private SitzplatzAbfrageApi sitzplatzAbfrageApi;

    @Test
    void prueftFreieSitzplaetzeOhneAusnahme() {
        UUID id = UUID.randomUUID();
        Sitzplatz frei = new Sitzplatz(id, BereichTyp.MITTE, new SitzplatzNummer(1, "A"), false);
        when(sitzplatzRepository.findAllById(Set.of(id))).thenReturn(List.of(frei));

        assertThatNoException().isThrownBy(() ->
                sitzplatzAbfrageApi.pruefeSitzplaetzeNichtGesperrt(Set.of(id)));
    }

    @Test
    void wirftExceptionFuerGesperrtenSitzplatz() {
        UUID id = UUID.randomUUID();
        Sitzplatz gesperrt = new Sitzplatz(id, BereichTyp.VORNE, new SitzplatzNummer(2, "B"), true);
        when(sitzplatzRepository.findAllById(Set.of(id))).thenReturn(List.of(gesperrt));

        assertThatThrownBy(() -> sitzplatzAbfrageApi.pruefeSitzplaetzeNichtGesperrt(Set.of(id)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Gesperrte Sitze können nicht gebucht werden");
    }

    @Test
    void wirftExceptionWennSitzplatzNichtGefunden() {
        UUID id = UUID.randomUUID();
        when(sitzplatzRepository.findAllById(Set.of(id))).thenReturn(List.of());

        assertThatThrownBy(() -> sitzplatzAbfrageApi.pruefeSitzplaetzeNichtGesperrt(Set.of(id)))
                .isInstanceOf(DomainException.class)
                .hasMessage("Sitzplatz nicht gefunden");
    }

    @Test
    void prueftMehrereFrSitzplaetzeOhneAusnahme() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Sitzplatz s1 = new Sitzplatz(id1, BereichTyp.HINTEN, new SitzplatzNummer(3, "C"), false);
        Sitzplatz s2 = new Sitzplatz(id2, BereichTyp.HINTEN, new SitzplatzNummer(3, "D"), false);
        when(sitzplatzRepository.findAllById(Set.of(id1, id2))).thenReturn(List.of(s1, s2));

        assertThatNoException().isThrownBy(() ->
                sitzplatzAbfrageApi.pruefeSitzplaetzeNichtGesperrt(Set.of(id1, id2)));
    }
}
