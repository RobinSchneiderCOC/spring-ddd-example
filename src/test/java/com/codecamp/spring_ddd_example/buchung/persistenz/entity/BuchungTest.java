package com.codecamp.spring_ddd_example.buchung.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungTest {

    private static final UUID SITZPLATZ_ID_1 = UUID.randomUUID();
    private static final UUID SITZPLATZ_ID_2 = UUID.randomUUID();

    @Test
    void erstelltNeueBuchungMitAktivStatus() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_ID_1, SITZPLATZ_ID_2));

        assertThat(buchung.getId()).isNull();
        assertThat(buchung.istAktiv()).isTrue();
        assertThat(buchung.sitzplatzIds()).containsExactlyInAnyOrder(SITZPLATZ_ID_1, SITZPLATZ_ID_2);
    }

    @Test
    void wirftExceptionBeiLeererSitzplatzListe() {
        assertThatThrownBy(() -> Buchung.neu(Set.of()))
                .isInstanceOf(DomainException.class)
                .hasMessage("Eine Buchung muss mindestens einen Sitzplatz enthalten");
    }

    @Test
    void wirftExceptionBeiNullSitzplatzListe() {
        assertThatThrownBy(() -> Buchung.neu(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("Eine Buchung muss mindestens einen Sitzplatz enthalten");
    }

    @Test
    void stornierenSetztStatusAufStorniert() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_ID_1));

        buchung.stornieren();

        assertThat(buchung.istAktiv()).isFalse();
    }

    @Test
    void stornierenAufStornierteBuchungWirftException() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_ID_1));
        buchung.stornieren();

        assertThatThrownBy(buchung::stornieren)
                .isInstanceOf(DomainException.class)
                .hasMessage("Eine stornierte Buchung kann nicht mehr geändert werden");
    }

    @Test
    void pruefeIstAenderbarWirftNichtWennAktiv() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_ID_1));

        assertThatCode(buchung::pruefeIstAenderbar).doesNotThrowAnyException();
    }

    @Test
    void pruefeIstAenderbarWirftBeiStornierterBuchung() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_ID_1));
        buchung.stornieren();

        assertThatThrownBy(buchung::pruefeIstAenderbar)
                .isInstanceOf(DomainException.class)
                .hasMessage("Eine stornierte Buchung kann nicht mehr geändert werden");
    }
}
