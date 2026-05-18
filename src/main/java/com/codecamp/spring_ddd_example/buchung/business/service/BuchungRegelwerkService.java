package com.codecamp.spring_ddd_example.buchung.business.service;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.buchung.persistenz.repository.BuchungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuchungRegelwerkService {

    private final BuchungRepository buchungRepository;

    public void pruefeKeineAktiveBuchungFuerSitzplaetze(Set<UUID> sitzplatzIds) {
        sitzplatzIds.forEach(id -> {
            if (buchungRepository.existsAktiveBuchungFuerSitzplatz(id)) {
                throw new DomainException("Sitzplatz ist bereits gebucht");
            }
        });
    }
}
