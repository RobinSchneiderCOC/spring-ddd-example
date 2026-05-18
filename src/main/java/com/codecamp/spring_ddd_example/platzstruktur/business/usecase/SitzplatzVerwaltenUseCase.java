package com.codecamp.spring_ddd_example.platzstruktur.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzGesperrtEvent;
import com.codecamp.spring_ddd_example.across.annotation.UseCase;
import com.codecamp.spring_ddd_example.platzstruktur.business.service.SitzplatzRegelwerkService;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Set;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class SitzplatzVerwaltenUseCase {

	private final SitzplatzRepository sitzplatzRepository;
	private final SitzplatzRegelwerkService sitzplatzRegelwerkService;
	private final ApplicationEventPublisher applicationEventPublisher;

	public UUID legeSitzplatzAn(BereichTyp bereichTyp, String nummer) {
		SitzplatzNummer normalisierteNummer = SitzplatzNummer.ausWert(nummer);
		sitzplatzRegelwerkService.pruefeSitzplatzNummerIstEindeutig(bereichTyp, normalisierteNummer);

		Sitzplatz neuerSitzplatz = Sitzplatz.neu(bereichTyp, normalisierteNummer);
		Sitzplatz gespeicherterSitzplatz = sitzplatzRepository.save(neuerSitzplatz);
		return gespeicherterSitzplatz.getId();
	}

	public void entferneSitzplatz(UUID sitzplatzId) {
		ladeSitzplatz(sitzplatzId);
		sitzplatzRepository.deleteById(sitzplatzId);
	}

	public void sperreSitzplatz(UUID sitzplatzId) {
		Sitzplatz sitzplatz = ladeSitzplatz(sitzplatzId);
		sitzplatz.sperren();
		sitzplatzRepository.save(sitzplatz);
		applicationEventPublisher.publishEvent(new SitzGesperrtEvent(Set.of(sitzplatzId)));
	}

	public void gibSitzplatzFrei(UUID sitzplatzId) {
		Sitzplatz sitzplatz = ladeSitzplatz(sitzplatzId);
		sitzplatz.freigeben();
		sitzplatzRepository.save(sitzplatz);
	}

	private Sitzplatz ladeSitzplatz(UUID sitzplatzId) {
		return sitzplatzRepository.findById(sitzplatzId)
				.orElseThrow(() -> new DomainException("Sitzplatz nicht gefunden"));
	}
}
