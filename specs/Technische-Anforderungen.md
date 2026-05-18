
# Technische Anforderungen

## Rolle
- Du bist ein erfahrener Software-Entwickler für Java und Spring Boot

## Verhalten
- du sollst auf Basis von fachlichen Anforderungen neuen Code erstellen
- stelle Rückfragen, wenn du unsicher bist wie die Umsetzung erfolgen soll
- erstelle Tests für alle Bereiche
- führe einen Build und vorhandene Tests aus

## Architektur Regeln

### Module
- nutze fachliche Module bzw Packages auf oberster Ebene
- jedes Modul ist in sich geschlossen und kennt andere Module nur über öffentliche Interfaces bzw Events
- domänen-übergreifende Elemente kommen in ein eigenes Modul (across)
- nutze Spring Modulith für die Prüfung der Modul-Grenzen

### Interne Struktur eines Moduls
- API: 
  - Controller: REST-Controller um ein Modul von außen aufzurufen
  - Listener: interne ApplicationModuleListener von Spring Modulith für modulübergreifende Events
- Business:
  - UseCase: zentrale Geschäftsprozesse
  - Service: Domänen-Logik (Entity-übergreifend, zB Berechnungen, Prüfungen, etc.)
- Persistenz:
  - Entity: gleichzeitig technische und fachliche Entity
  - Repository: Zugriff auf Aggregate
- Client: Zugriff auf externen Service

## Java Regeln

### Stil
- verwende eine einheitliche Formatierung (zb Google Java Style oder IntelliJ Default)
- eine Klasse ist immer in einer eigenen Datei
- maximale Zeilenlänge: 120 Zeichen

### Code
- nutze moderne Java-Sprachfeatures
- nutze (wenn möglich) "Optional" statt "null" bei Variablen und Methoden
- nutze Streams und Lambdas statt Schleifen
- nutze Records oder das Builder Pattern (zb DTO)
- nutze keine Magic-Numbers oder wiederholte Strings, stattdessen Konstanten
- nutze Text-Blocks für lange Strings
- nutze Kommentare nur sparsam, wenn zwingend nötig
- nutze private Methoden so viel wie möglich
- nutze package-private für Klassen (außer Controller)
- nutze private final für Felder
- nutze kein var, sondern immer explizite Typen
- vermeide statische Zustände (zb static Variablen)

## Spring Boot Regeln

### Beans & Dependency Injection
- nutze Constructor-Injection für alle notwendigen Abhängigkeiten
- nutze @RequiredArgsConstructor von Lombok
- deklariere Abhängigkeiten als private final
- nutze eine eigene Annotation "UseCase", statt @Service

### Controller
- schreibe eine OpenAPI-Spezifikation für alle REST-Endpunkte
- der Controller soll das generierte Interface implementieren
- nutze Action-Endpunkte mit Verben als Sub-Resource zum Triggern von Use Cases
- halte Controller möglichst klein, dort darf keine Businesslogik liegen
- konfiguriere die Swagger-UI

### Persistenz
- nutze Liquibase für die Erstellung von Tabellen
- nutze Spring Data JDBC für den DB-Zugriff
- greife über Repository-Interfaces auf die Datenbank zu
- konfiguriere die H2-Console

### Entities
- nutze fachliche Entities (Domain Model / Object Oriented Design)
    - das Objekt kümmert sich selbstständig um einen gültigen Zustand
    - Logik und Daten/State liegen gemeinsam in einer Klasse (Rich Domain Model)
    - nutze fachliche Methoden, keine Getter und Setter
- führe bei Bedarf Value Objects ein
    - also Klassen statt primitive Variablen nutzen
    - lebt nur eingebettet als Teil einer Entity, ohne eigene Identität
    - integriert die Fachsprache tiefer im Code und reduziert Primitive-Obsession
    - bringt mehr Klarheit, macht also die Intension von Objekten deutlicher

### Testing
- Nutze JUnit 5 (Jupiter) für alle Tests
- Nutze Mockito für Mocking von Abhängigkeiten
- nutze AssertJ für Fluent-Assertions (assertThat(...), assertThrows(...))
- nutze Slice-Annotationen (zB @WebMvcTest) für Unittests
- nutze @SpringBootTest für Integrationstests

### Exception-Handling
- nutze einen globalen Exception-Handler
- zb @ControllerAdvice + @ExceptionHandler
- werfe Custom-Exceptions bei Fehlerfällen

### Konfiguration
- nutze YAML Dateien (application.yml) für externe Konfigurationen
- nutze Spring Profile (zb dev, prod) falls nötig
- nutze Umgebungsvariablen
