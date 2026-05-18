package com.codecamp.spring_ddd_example.platzstruktur.business.service;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SitzplatzRegelwerkService {

    private final SitzplatzRepository sitzplatzRepository;

    public void pruefeSitzplatzNummerIstEindeutig(BereichTyp bereichTyp, SitzplatzNummer nummer) {
        if (sitzplatzRepository.existsByBereichTypAndNummer(bereichTyp, nummer.reihe(), nummer.position())) {
            throw new DomainException("Die Sitznummer existiert in diesem Bereich bereits");
        }
    }
}