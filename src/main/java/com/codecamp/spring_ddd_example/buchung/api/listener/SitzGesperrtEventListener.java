package com.codecamp.spring_ddd_example.buchung.api.listener;

import com.codecamp.spring_ddd_example.platzstruktur.api.exposed.SitzGesperrtEvent;
import com.codecamp.spring_ddd_example.buchung.business.usecase.BuchungVerwaltenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SitzGesperrtEventListener {

    private final BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @ApplicationModuleListener
    void handle(SitzGesperrtEvent event) {
        buchungVerwaltenUseCase.storniereAlleBuchungenFuerSitzplaetze(event.sitzplatzIds());
    }
}
