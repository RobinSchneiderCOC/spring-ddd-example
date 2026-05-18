package com.codecamp.spring_ddd_example.platzstruktur.business.usecase;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzGesperrtEvent;
import com.codecamp.spring_ddd_example.platzstruktur.business.service.SitzplatzRegelwerkService;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SitzplatzVerwaltenUseCaseTest {

    @Mock
    private SitzplatzRepository sitzplatzRepository;

    @InjectMocks
    private SitzplatzRegelwerkService sitzplatzRegelwerkService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @Test
    void legtSitzplatzAnWennNummerFreiIst() {
        sitzplatzVerwaltenUseCase = new SitzplatzVerwaltenUseCase(
            sitzplatzRepository,
            sitzplatzRegelwerkService,
            applicationEventPublisher
        );
        when(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.VORNE, 1, "B")).thenReturn(false);
        when(sitzplatzRepository.save(any(Sitzplatz.class))).thenAnswer(invocation -> {
            Sitzplatz neuerSitzplatz = invocation.getArgument(0);
            return new Sitzplatz(UUID.randomUUID(), BereichTyp.VORNE, neuerSitzplatz.getNummer(), neuerSitzplatz.istGesperrt());
        });

        UUID sitzplatzId = sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.VORNE, "1B");

        assertThat(sitzplatzId).isNotNull();
        ArgumentCaptor<Sitzplatz> sitzplatzCaptor = ArgumentCaptor.forClass(Sitzplatz.class);
        verify(sitzplatzRepository).save(sitzplatzCaptor.capture());
        assertThat(sitzplatzCaptor.getValue().istGesperrt()).isFalse();
    }

    @Test
    void wirftFehlerWennSitznummerImBereichSchonExistiert() {
        sitzplatzVerwaltenUseCase = new SitzplatzVerwaltenUseCase(
            sitzplatzRepository,
            sitzplatzRegelwerkService,
            applicationEventPublisher
        );
        when(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.HINTEN, 1, "B")).thenReturn(true);

        assertThatThrownBy(() -> sitzplatzVerwaltenUseCase.legeSitzplatzAn(BereichTyp.HINTEN, "1B"))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Sitznummer existiert in diesem Bereich bereits");
    }

    @Test
    void sperrtSitzplatzUndSpeichertIhn() {
        sitzplatzVerwaltenUseCase = new SitzplatzVerwaltenUseCase(
            sitzplatzRepository,
            sitzplatzRegelwerkService,
            applicationEventPublisher
        );
        UUID sitzplatzId = UUID.randomUUID();
        Sitzplatz sitzplatz = new Sitzplatz(sitzplatzId, BereichTyp.MITTE, SitzplatzNummer.ausWert("1B"), false);
        when(sitzplatzRepository.findById(sitzplatzId)).thenReturn(Optional.of(sitzplatz));
        when(sitzplatzRepository.save(any(Sitzplatz.class))).thenAnswer(invocation -> invocation.getArgument(0));

        sitzplatzVerwaltenUseCase.sperreSitzplatz(sitzplatzId);

        assertThat(sitzplatz.istGesperrt()).isTrue();
        verify(sitzplatzRepository).save(sitzplatz);
        ArgumentCaptor<SitzGesperrtEvent> eventCaptor = ArgumentCaptor.forClass(SitzGesperrtEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().sitzplatzIds()).containsExactly(sitzplatzId);
    }

    @Test
    void gibtSitzplatzFreiUndSpeichertIhn() {
        sitzplatzVerwaltenUseCase = new SitzplatzVerwaltenUseCase(
            sitzplatzRepository,
            sitzplatzRegelwerkService,
            applicationEventPublisher
        );
        UUID sitzplatzId = UUID.randomUUID();
        Sitzplatz sitzplatz = new Sitzplatz(UUID.randomUUID(), BereichTyp.MITTE, SitzplatzNummer.ausWert("1B"), true);
        when(sitzplatzRepository.findById(sitzplatzId)).thenReturn(Optional.of(sitzplatz));
        when(sitzplatzRepository.save(any(Sitzplatz.class))).thenAnswer(invocation -> invocation.getArgument(0));

        sitzplatzVerwaltenUseCase.gibSitzplatzFrei(sitzplatzId);

        assertThat(sitzplatz.istGesperrt()).isFalse();
        verify(sitzplatzRepository).save(sitzplatz);
    }

    @Test
    void entferntSitzplatzWennErExistiert() {
        sitzplatzVerwaltenUseCase = new SitzplatzVerwaltenUseCase(
            sitzplatzRepository,
            sitzplatzRegelwerkService,
            applicationEventPublisher
        );
        UUID sitzplatzId = UUID.randomUUID();
        Sitzplatz sitzplatz = Sitzplatz.neu(BereichTyp.VORNE, SitzplatzNummer.ausWert("1B"));
        when(sitzplatzRepository.findById(sitzplatzId)).thenReturn(Optional.of(sitzplatz));

        sitzplatzVerwaltenUseCase.entferneSitzplatz(sitzplatzId);

        verify(sitzplatzRepository).deleteById(sitzplatzId);
    }
}
