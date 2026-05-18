package com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SitzplatzNummerTest {

    @Test
    void erzeugtAusWertMitNormalisierung() {
        SitzplatzNummer nummer = SitzplatzNummer.ausWert(" 9c ");

        assertThat(nummer.reihe()).isEqualTo(9);
        assertThat(nummer.position()).isEqualTo("C");
        assertThat(nummer.alsText()).isEqualTo("9C");
    }

    @Test
    void lehntNullAlsWertAb() {
        assertThatThrownBy(() -> SitzplatzNummer.ausWert(null))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Sitzplatznummer muss dem Format Reihe+Position entsprechen (z. B. 1B)");
    }

    @Test
    void lehntUngueltigesFormatAb() {
        assertThatThrownBy(() -> SitzplatzNummer.ausWert("A12"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Sitzplatznummer muss dem Format Reihe+Position entsprechen (z. B. 1B)");
    }

    @Test
    void lehntUngueltigeReiheAb() {
        assertThatThrownBy(() -> new SitzplatzNummer(0, "A"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Reihe muss zwischen 1 und 999 liegen");
    }

    @Test
    void lehntUngueltigePositionAb() {
        assertThatThrownBy(() -> new SitzplatzNummer(1, "AB"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Position muss ein einzelner Buchstabe A-Z sein");
    }
}
