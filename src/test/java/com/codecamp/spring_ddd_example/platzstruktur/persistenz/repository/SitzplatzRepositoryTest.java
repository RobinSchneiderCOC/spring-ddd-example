package com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository;

import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Transactional
class SitzplatzRepositoryTest {

    @Autowired
    private SitzplatzRepository sitzplatzRepository;

    @Test
    void speichertUndFindetSitzplatz() {
        Sitzplatz sitzplatz = Sitzplatz.neu(BereichTyp.MITTE, new SitzplatzNummer(5, "C"));

        Sitzplatz gespeichert = sitzplatzRepository.save(sitzplatz);

        Optional<Sitzplatz> gefunden = sitzplatzRepository.findById(gespeichert.getId());
        assertThat(gefunden).isPresent();
        assertThat(gefunden.get().getNummer()).isEqualTo(new SitzplatzNummer(5, "C"));
        assertThat(gefunden.get().istGesperrt()).isFalse();
    }

    @Test
    void findByBereichTypGibtNurRichtigeBereicheZurueck() {
        Sitzplatz mitte = sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.MITTE, new SitzplatzNummer(20, "A")));
        Sitzplatz vorne = sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.VORNE, new SitzplatzNummer(20, "B")));

        List<Sitzplatz> mitteErgebnis = sitzplatzRepository.findByBereichTyp(BereichTyp.MITTE);

        assertThat(mitteErgebnis).extracting(Sitzplatz::getId).contains(mitte.getId());
        assertThat(mitteErgebnis).extracting(Sitzplatz::getId).doesNotContain(vorne.getId());
    }

    @Test
    void existsByBereichTypAndNummerGibtTrueZurueckWennVorhanden() {
        sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.HINTEN, new SitzplatzNummer(30, "Z")));

        assertThat(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.HINTEN, 30, "Z")).isTrue();
    }

    @Test
    void existsByBereichTypAndNummerGibtFalseZurueckWennNichtVorhanden() {
        assertThat(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.HINTEN, 99, "Q")).isFalse();
    }

    @Test
    void existsByBereichTypAndNummerGibtFalseZurueckBeiFalscherKombination() {
        sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.VORNE, new SitzplatzNummer(31, "A")));

        // gleiche Nummer, anderer Bereich
        assertThat(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.MITTE, 31, "A")).isFalse();
    }

    @Test
    void findByGesperrtGibtNurGesperrteSitzplaetzeZurueck() {
        Sitzplatz frei = sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.MITTE, new SitzplatzNummer(40, "A")));
        Sitzplatz gesperrt = Sitzplatz.neu(BereichTyp.MITTE, new SitzplatzNummer(40, "B"));
        gesperrt.sperren();
        gesperrt = sitzplatzRepository.save(gesperrt);

        List<Sitzplatz> gesperrteErgebnis = sitzplatzRepository.findByGesperrt(true);
        List<Sitzplatz> freieErgebnis = sitzplatzRepository.findByGesperrt(false);

        assertThat(gesperrteErgebnis).extracting(Sitzplatz::getId).contains(gesperrt.getId());
        assertThat(gesperrteErgebnis).extracting(Sitzplatz::getId).doesNotContain(frei.getId());
        assertThat(freieErgebnis).extracting(Sitzplatz::getId).contains(frei.getId());
        assertThat(freieErgebnis).extracting(Sitzplatz::getId).doesNotContain(gesperrt.getId());
    }

    @Test
    void loeschtSitzplatz() {
        Sitzplatz sitzplatz = sitzplatzRepository.save(Sitzplatz.neu(BereichTyp.VORNE, new SitzplatzNummer(50, "A")));

        sitzplatzRepository.deleteById(sitzplatz.getId());

        assertThat(sitzplatzRepository.findById(sitzplatz.getId())).isEmpty();
    }
}
