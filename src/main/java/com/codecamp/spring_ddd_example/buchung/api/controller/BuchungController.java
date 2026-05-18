package com.codecamp.spring_ddd_example.buchung.api.controller;

import com.codecamp.spring_ddd_example.buchung.api.generated.BuchungApi;
import com.codecamp.spring_ddd_example.buchung.api.generated.model.BuchungAnlegenRequest;
import com.codecamp.spring_ddd_example.buchung.api.generated.model.BuchungAnlegenResponse;
import com.codecamp.spring_ddd_example.buchung.business.usecase.BuchungVerwaltenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BuchungController implements BuchungApi {

    private final BuchungVerwaltenUseCase buchungVerwaltenUseCase;

    @Override
    public BuchungAnlegenResponse bucheSitzplaetze(BuchungAnlegenRequest request) {
        UUID buchungId = buchungVerwaltenUseCase.bucheSitzplaetze(Set.copyOf(request.getSitzplatzIds()));

        BuchungAnlegenResponse response = new BuchungAnlegenResponse();
        response.setBuchungId(buchungId);
        return response;
    }

    @Override
    public void storniereBuchung(UUID buchungId) {
        buchungVerwaltenUseCase.storniereBuchung(buchungId);
    }
}
