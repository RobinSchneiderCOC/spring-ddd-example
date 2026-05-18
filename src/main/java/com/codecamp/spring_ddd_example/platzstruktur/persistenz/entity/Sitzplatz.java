package com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("sitzplatz")
public class Sitzplatz {

    @Id
    @Column("id")
    private final UUID id;

    @Column("bereich_typ")
    private final BereichTyp bereichTyp;

    @Embedded.Nullable(prefix = "nummer_")
    private final SitzplatzNummer nummer;

    @Column("gesperrt")
    private boolean gesperrt;

    public Sitzplatz(UUID id, BereichTyp bereichTyp, SitzplatzNummer nummer, boolean gesperrt) {
        this.id = id;
        this.bereichTyp = Objects.requireNonNull(bereichTyp);
        this.nummer = Objects.requireNonNull(nummer);
        this.gesperrt = gesperrt;
    }

    public static Sitzplatz neu(BereichTyp bereichTyp, SitzplatzNummer nummer) {
        return new Sitzplatz(null, bereichTyp, nummer, false);
    }

    public void sperren() {
        this.gesperrt = true;
    }

    public void freigeben() {
        this.gesperrt = false;
    }

    public void pruefeIstBuchbar() {
        if (gesperrt) {
            throw new DomainException("Gesperrte Sitze können nicht gebucht werden");
        }
    }

    public boolean istGesperrt() {
        return gesperrt;
    }

    public UUID getId() {
        return id;
    }

    public SitzplatzNummer getNummer() {
        return nummer;
    }
}
