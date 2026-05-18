package com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity;

import com.codecamp.spring_ddd_example.across.exception.DomainException;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SitzplatzNummer(@Column("reihe") int reihe, @Column("position") String position) {

    private static final Pattern FORMAT = Pattern.compile("^([1-9]\\d{0,2})([A-Z])$");

    public SitzplatzNummer(int reihe, String position) {
        if (reihe < 1 || reihe > 999) {
            throw new DomainException("Die Reihe muss zwischen 1 und 999 liegen");
        }

        String normalisiertePosition = Objects.requireNonNull(position).trim().toUpperCase(Locale.ROOT);
        if (!normalisiertePosition.matches("[A-Z]")) {
            throw new DomainException("Die Position muss ein einzelner Buchstabe A-Z sein");
        }

        this.reihe = reihe;
        this.position = normalisiertePosition;
    }

    public static SitzplatzNummer ausWert(String wert) {
        String normalisierterWert = wert == null ? null : wert.trim().toUpperCase(Locale.ROOT);
        if (normalisierterWert == null) {
            throw new DomainException("Die Sitzplatznummer muss dem Format Reihe+Position entsprechen (z. B. 1B)");
        }

        Matcher matcher = FORMAT.matcher(normalisierterWert);
        if (!matcher.matches()) {
            throw new DomainException("Die Sitzplatznummer muss dem Format Reihe+Position entsprechen (z. B. 1B)");
        }

        int reihe = Integer.parseInt(matcher.group(1));
        String position = matcher.group(2);
        return new SitzplatzNummer(reihe, position);
    }

    public String alsText() {
        return "%d%s".formatted(reihe, position);
    }
}
