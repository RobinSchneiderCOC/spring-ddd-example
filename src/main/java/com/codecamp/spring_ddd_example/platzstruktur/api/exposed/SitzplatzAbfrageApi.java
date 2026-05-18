package com.codecamp.spring_ddd_example.platzstruktur.api.exposed;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SitzplatzAbfrageApi {

    private final SitzplatzRepository sitzplatzRepository;

    public void pruefeSitzplaetzeNichtGesperrt(Set<UUID> sitzplatzIds) {
        List<Sitzplatz> sitzplaetze = sitzplatzRepository.findAllById(sitzplatzIds);
        if (sitzplaetze.size() != sitzplatzIds.size()) {
            throw new DomainException("Sitzplatz nicht gefunden");
        }
        sitzplaetze.forEach(Sitzplatz::pruefeIstBuchbar);
    }
}
