package com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SitzplatzTest {

    @Test
    void erstelltNeuenSitzplatzUnguesperrt() {
        Sitzplatz sitzplatz = Sitzplatz.neu(BereichTyp.VORNE, new SitzplatzNummer(3, "A"));

        assertThat(sitzplatz.getId()).isNull();
        assertThat(sitzplatz.istGesperrt()).isFalse();
        assertThat(sitzplatz.getNummer()).isEqualTo(new SitzplatzNummer(3, "A"));
    }

    @Test
    void sperrtUndGibtSitzplatzWiederFrei() {
        Sitzplatz sitzplatz = new Sitzplatz(UUID.randomUUID(), BereichTyp.MITTE, new SitzplatzNummer(4, "C"), false);

        sitzplatz.sperren();
        assertThat(sitzplatz.istGesperrt()).isTrue();

        sitzplatz.freigeben();
        assertThat(sitzplatz.istGesperrt()).isFalse();
    }

    @Test
    void erlaubtBuchungWennNichtGesperrt() {
        Sitzplatz sitzplatz = new Sitzplatz(UUID.randomUUID(), BereichTyp.HINTEN, new SitzplatzNummer(5, "D"), false);

        assertThatCode(sitzplatz::pruefeIstBuchbar).doesNotThrowAnyException();
    }

    @Test
    void verbietetBuchungWennGesperrt() {
        Sitzplatz sitzplatz = new Sitzplatz(UUID.randomUUID(), BereichTyp.HINTEN, new SitzplatzNummer(5, "D"), true);

        assertThatThrownBy(sitzplatz::pruefeIstBuchbar)
                .isInstanceOf(DomainException.class)
                .hasMessage("Gesperrte Sitze können nicht gebucht werden");
    }
}
