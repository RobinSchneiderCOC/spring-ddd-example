package com.codecamp.spring_ddd_example.buchung.persistenz.repository;

import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Transactional
class BuchungRepositoryTest {

    @Autowired
    private BuchungRepository buchungRepository;

    private static final UUID SITZPLATZ_A = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID SITZPLATZ_B = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    private static final UUID SITZPLATZ_C = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @Test
    void speichertUndFindetBuchungMitSitzplaetzen() {
        Buchung buchung = Buchung.neu(Set.of(SITZPLATZ_A, SITZPLATZ_B));

        Buchung gespeichert = buchungRepository.save(buchung);

        Optional<Buchung> gefunden = buchungRepository.findById(gespeichert.getId());
        assertThat(gefunden).isPresent();
        assertThat(gefunden.get().istAktiv()).isTrue();
        assertThat(gefunden.get().sitzplatzIds()).containsExactlyInAnyOrder(SITZPLATZ_A, SITZPLATZ_B);
    }

    @Test
    void existsAktiveBuchungFuerSitzplatzGibtTrueZurueckFuerAktiveBuchung() {
        buchungRepository.save(Buchung.neu(Set.of(SITZPLATZ_A)));

        assertThat(buchungRepository.existsAktiveBuchungFuerSitzplatz(SITZPLATZ_A)).isTrue();
    }

    @Test
    void existsAktiveBuchungFuerSitzplatzGibtFalseZurueckWennKeineBuchung() {
        assertThat(buchungRepository.existsAktiveBuchungFuerSitzplatz(SITZPLATZ_A)).isFalse();
    }

    @Test
    void existsAktiveBuchungFuerSitzplatzGibtFalseZurueckNachStornierung() {
        Buchung buchung = buchungRepository.save(Buchung.neu(Set.of(SITZPLATZ_A)));
        buchung.stornieren();
        buchungRepository.save(buchung);

        assertThat(buchungRepository.existsAktiveBuchungFuerSitzplatz(SITZPLATZ_A)).isFalse();
    }

    @Test
    void findAktiveBuchungsIdsGibtNurAktiveBuchungenZurueck() {
        Buchung aktiv = buchungRepository.save(Buchung.neu(Set.of(SITZPLATZ_A)));
        Buchung storniert = buchungRepository.save(Buchung.neu(Set.of(SITZPLATZ_B)));
        storniert.stornieren();
        buchungRepository.save(storniert);

        List<UUID> ergebnis = buchungRepository.findAktiveBuchungsIdsForSitzplatzIds(Set.of(SITZPLATZ_A, SITZPLATZ_B));

        assertThat(ergebnis).contains(aktiv.getId());
        assertThat(ergebnis).doesNotContain(storniert.getId());
    }

    @Test
    void findAktiveBuchungsIdsGibtLeereListeZurueckWennNurStorniert() {
        Buchung buchung = buchungRepository.save(Buchung.neu(Set.of(SITZPLATZ_C)));
        buchung.stornieren();
        buchungRepository.save(buchung);

        List<UUID> ergebnis = buchungRepository.findAktiveBuchungsIdsForSitzplatzIds(Set.of(SITZPLATZ_C));

        assertThat(ergebnis).isEmpty();
    }
}
