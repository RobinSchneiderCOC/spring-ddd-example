package com.codecamp.spring_ddd_example.platzstruktur.api.controller;

import com.codecamp.spring_ddd_example.platzstruktur.api.generated.PlatzstrukturApi;
import com.codecamp.spring_ddd_example.platzstruktur.api.generated.model.SitzplatzAnlegenRequest;
import com.codecamp.spring_ddd_example.platzstruktur.api.generated.model.SitzplatzAnlegenResponse;
import com.codecamp.spring_ddd_example.platzstruktur.business.usecase.SitzplatzVerwaltenUseCase;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlatzstrukturController implements PlatzstrukturApi {

    private final SitzplatzVerwaltenUseCase sitzplatzVerwaltenUseCase;

    @Override
    public SitzplatzAnlegenResponse legeSitzplatzAn(SitzplatzAnlegenRequest request) {
        UUID sitzplatzId = sitzplatzVerwaltenUseCase.legeSitzplatzAn(
                BereichTyp.valueOf(request.getBereichTyp().name()),
                request.getNummer()
        );

        SitzplatzAnlegenResponse response = new SitzplatzAnlegenResponse();
        response.setSitzplatzId(sitzplatzId);
        return response;
    }

    @Override
    public void sperreSitzplatz(UUID sitzplatzId) {
        sitzplatzVerwaltenUseCase.sperreSitzplatz(sitzplatzId);
    }

    @Override
    public void gibSitzplatzFrei(UUID sitzplatzId) {
        sitzplatzVerwaltenUseCase.gibSitzplatzFrei(sitzplatzId);
    }

    @Override
    public void entferneSitzplatz(UUID sitzplatzId) {
        sitzplatzVerwaltenUseCase.entferneSitzplatz(sitzplatzId);
    }
}
