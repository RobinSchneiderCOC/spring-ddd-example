package com.codecamp.spring_ddd_example.buchung.persistenz.repository;

import com.codecamp.spring_ddd_example.buchung.persistenz.entity.Buchung;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface BuchungRepository extends ListCrudRepository<Buchung, UUID> {

    @Query("""
            SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
            FROM "buchung" b
            JOIN "buchung_sitzplatz" bs ON bs."buchung_id" = b."id"
            WHERE bs."sitzplatz_id" = :sitzplatzId
              AND b."status" = 'AKTIV'
            """)
    boolean existsAktiveBuchungFuerSitzplatz(@Param("sitzplatzId") UUID sitzplatzId);

    @Query("""
            SELECT DISTINCT b."id"
            FROM "buchung" b
            JOIN "buchung_sitzplatz" bs ON bs."buchung_id" = b."id"
            WHERE bs."sitzplatz_id" IN (:sitzplatzIds)
              AND b."status" = 'AKTIV'
            """)
    List<UUID> findAktiveBuchungsIdsForSitzplatzIds(@Param("sitzplatzIds") Set<UUID> sitzplatzIds);
}
