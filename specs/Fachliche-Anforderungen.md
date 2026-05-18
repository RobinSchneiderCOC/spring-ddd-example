
# Fachliche Anforderungen

## Domain
- allgemeine Sitzplatzbuchung
- ohne spezielle Veranstaltung, ohne Bezahlung, etc.
- Sitze sind in Bereiche unterteilt
- Buchungen haben bestimmte Kriterien

## Kontexte & Regeln
- Platzstruktur
  - Sitze mit Nummern gehören zu einem Bereich (vorne, mitte, hinten)
  - Sitze können angelegt und entfernt werden für einen Bereich
  - Sitze können gesperrt und wieder freigegeben werden
- Buchung
  - mehrere Sitze können gebucht und wieder storniert werden
  - gesperrte Sitze können nicht gebucht werden
  - wenn Sitze gesperrt werden, dann wird die Buchung automatisch storniert
  - ein Sitz darf nur einmal gleichzeitig gebucht sein
  - eine Buchung enthält einen Status und mindestens einen Sitz
  - nach einer Stornierung ist keine Änderung an der Buchung möglich
