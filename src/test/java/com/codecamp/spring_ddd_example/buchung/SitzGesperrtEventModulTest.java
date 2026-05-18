package com.codecamp.spring_ddd_example.buchung;

import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import com.codecamp.spring_ddd_example.platzstruktur.business.usecase.SitzplatzVerwaltenUseCase;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.ApplicationModuleTest.BootstrapMode;
import org.springframework.modulith.test.Scenario;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest(mode = BootstrapMode.DIRECT_DEPENDENCIES)
class SitzGesperrtEventModulTest {

    @Autowired
    private BuchungRepository buchungRepository;

    @Autowired
    private SitzplatzRepository sitzplatzRepository;

    @Autowired
    private SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @Test
    // Testet den Fluss über die Modulgrenze:
    // platzstruktur sperrt einen Sitzplatz → publiziert ein SitzGesperrtEvent.
    // Das Buchungsmodul empfängt dieses Event und storniert automatisch
    // alle aktiven Buchungen für diesen Sitzplatz.
    // Am Ende prüfen wir, dass die Buchung tatsächlich storniert wurde.
    void stornieretBuchungenWennPlatzstrukturSitzGesperrtEventPubliziert(Scenario scenario) {
        Sitzplatz sitzplatz = sitzplatzRepository.save(
                Sitzplatz.neu(BereichTyp.MITTE, SitzplatzNummer.ausWert("10B")));
        UUID sitzplatzId = sitzplatz.getId();

        Buchung buchung = buchungRepository.save(Buchung.neu(Set.of(sitzplatzId)));
        UUID buchungId = buchung.getId();

        scenario.stimulate(() -> sitzplatzVerwaltenUseCase.sperreSitzplatz(sitzplatzId))
                .andWaitForStateChange(() -> buchungRepository.findById(buchungId).map(Buchung::istAktiv))
                .andVerify(isAktiv -> assertThat(isAktiv).contains(false));
    }
}
