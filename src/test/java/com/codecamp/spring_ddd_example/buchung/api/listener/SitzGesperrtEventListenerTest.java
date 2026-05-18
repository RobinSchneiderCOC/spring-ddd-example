package com.codecamp.spring_ddd_example.buchung.api.listener;

import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzGesperrtEvent;
import com.codecamp.spring_ddd_example.buchung.business.usecase.BuchungVerwaltenUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SitzGesperrtEventListenerTest {

    @Mock
    private BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @InjectMocks
    private SitzGesperrtEventListener listener;

    @Test
    void delegiertAnUseCase() {
        Set<UUID> sitzplatzIds = Set.of(UUID.randomUUID());

        listener.handle(new SitzGesperrtEvent(sitzplatzIds));

        verify(buchungVerwaltenUseCase).storniereAlleBuchungenFuerSitzplaetze(sitzplatzIds);
    }
}
