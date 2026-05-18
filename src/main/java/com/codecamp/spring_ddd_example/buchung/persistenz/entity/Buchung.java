package com.codecamp.spring_ddd_example.buchung.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Table("buchung")
public class Buchung {

    @Id
    @Column("id")
    private final UUID id;

    @Column("status")
    private BuchungStatus status;

    @MappedCollection(idColumn = "buchung_id")
    private final Set<BuchungSitzplatz> sitzplaetze;

    Buchung(UUID id, BuchungStatus status, Set<BuchungSitzplatz> sitzplaetze) {
        this.id = id;
        this.status = Objects.requireNonNull(status);
        this.sitzplaetze = Objects.requireNonNull(sitzplaetze);
    }

    public static Buchung neu(Set<UUID> sitzplatzIds) {
        if (sitzplatzIds == null || sitzplatzIds.isEmpty()) {
            throw new DomainException("Eine Buchung muss mindestens einen Sitzplatz enthalten");
        }
        Set<BuchungSitzplatz> sitzplaetze = sitzplatzIds.stream()
                .map(BuchungSitzplatz::new)
                .collect(Collectors.toSet());
        return new Buchung(null, BuchungStatus.AKTIV, sitzplaetze);
    }

    public void stornieren() {
        pruefeIstAenderbar();
        this.status = BuchungStatus.STORNIERT;
    }

    void pruefeIstAenderbar() {
        if (status == BuchungStatus.STORNIERT) {
            throw new DomainException("Eine stornierte Buchung kann nicht mehr geändert werden");
        }
    }

    public boolean istAktiv() {
        return status == BuchungStatus.AKTIV;
    }

    public Set<UUID> sitzplatzIds() {
        return sitzplaetze.stream()
                .map(BuchungSitzplatz::getSitzplatzId)
                .collect(Collectors.toUnmodifiableSet());
    }

    public UUID getId() {
        return id;
    }
}
