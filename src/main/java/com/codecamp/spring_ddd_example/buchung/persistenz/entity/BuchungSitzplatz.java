package com.codecamp.spring_ddd_example.buchung.persistenz.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("buchung_sitzplatz")
class BuchungSitzplatz {

    @Column("sitzplatz_id")
    private final UUID sitzplatzId;

    BuchungSitzplatz(UUID sitzplatzId) {
        this.sitzplatzId = sitzplatzId;
    }

    UUID getSitzplatzId() {
        return sitzplatzId;
    }
}
