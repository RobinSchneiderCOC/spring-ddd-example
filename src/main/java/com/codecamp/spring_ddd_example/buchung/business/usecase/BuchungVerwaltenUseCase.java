package com.codecamp.spring_ddd_example.buchung.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.across.annotation.UseCase;
import com.codecamp.spring_ddd_example.buchung.business.service.BuchungRegelwerkService;
import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzplatzAbfrageApi;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class BuchungVerwaltenUseCase {

    private final BuchungRepository buchungRepository;
    private final SitzplatzAbfrageApi sitzplatzAbfrageApi;
    private final BuchungRegelwerkService buchungRegelwerkService;

    public UUID bucheSitzplaetze(Set<UUID> sitzplatzIds) {
        sitzplatzAbfrageApi.pruefeSitzplaetzeNichtGesperrt(sitzplatzIds);
        buchungRegelwerkService.pruefeKeineAktiveBuchungFuerSitzplaetze(sitzplatzIds);

        Buchung buchung = Buchung.neu(sitzplatzIds);
        Buchung gespeicherteBuchung = buchungRepository.save(buchung);
        return gespeicherteBuchung.getId();
    }

    public void storniereAlleBuchungenFuerSitzplaetze(Set<UUID> sitzplatzIds) {
        buchungRepository.findAktiveBuchungsIdsForSitzplatzIds(sitzplatzIds)
                .forEach(this::storniereBuchung);
    }

    public void storniereBuchung(UUID buchungId) {
        Buchung buchung = ladeBuchung(buchungId);
        buchung.stornieren();
        buchungRepository.save(buchung);
    }

    private Buchung ladeBuchung(UUID buchungId) {
        return buchungRepository.findById(buchungId)
                .orElseThrow(() -> new DomainException("Buchung nicht gefunden"));
    }
}
