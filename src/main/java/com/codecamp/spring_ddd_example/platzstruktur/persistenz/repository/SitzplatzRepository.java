package com.codecamp.spring_ddd_example.platzstruktur.persistenz.repository;

import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.Sitzplatz;
import com.codecamp.spring_ddd_example.platzstruktur.persistenz.entity.BereichTyp;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SitzplatzRepository extends ListCrudRepository<Sitzplatz, UUID> {

	List<Sitzplatz> findByBereichTyp(BereichTyp bereichTyp);

	@Query("""
		SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
		FROM "sitzplatz"
		WHERE "bereich_typ" = :bereichTyp
		  AND "nummer_reihe" = :nummerReihe
		  AND "nummer_position" = :nummerPosition
		""")
	boolean existsByBereichTypAndNummer(
			@Param("bereichTyp") BereichTyp bereichTyp,
			@Param("nummerReihe") int nummerReihe,
			@Param("nummerPosition") String nummerPosition
	);

	List<Sitzplatz> findByGesperrt(boolean gesperrt);
}
