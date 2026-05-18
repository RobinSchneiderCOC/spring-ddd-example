package com.codecamp.spring_ddd_example.platzstruktur.business.service;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.SitzplatzNummer;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository.SitzplatzRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SitzplatzRegelwerkServiceTest {

    @Mock
    private SitzplatzRepository sitzplatzRepository;

    @InjectMocks
    private SitzplatzRegelwerkService sitzplatzRegelwerkService;

    @Test
    void akzeptiertEindeutigeSitzplatzNummer() {
        when(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.MITTE, 7, "D")).thenReturn(false);

        assertThatCode(() -> sitzplatzRegelwerkService.pruefeSitzplatzNummerIstEindeutig(
                BereichTyp.MITTE,
                new SitzplatzNummer(7, "D")
        )).doesNotThrowAnyException();
    }

    @Test
    void lehntDoppelteSitzplatzNummerAb() {
        when(sitzplatzRepository.existsByBereichTypAndNummer(BereichTyp.MITTE, 7, "D")).thenReturn(true);

        assertThatThrownBy(() -> sitzplatzRegelwerkService.pruefeSitzplatzNummerIstEindeutig(
                BereichTyp.MITTE,
                new SitzplatzNummer(7, "D")
        ))
                .isInstanceOf(DomainException.class)
                .hasMessage("Die Sitznummer existiert in diesem Bereich bereits");
    }
}
