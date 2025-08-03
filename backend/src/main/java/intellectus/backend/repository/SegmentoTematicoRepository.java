package intellectus.backend.repository;

import intellectus.backend.model.SegmentoTematico;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SegmentoTematicoRepository extends Neo4jRepository<SegmentoTematico, Long> {
    
    List<SegmentoTematico> findByFonteOrderByPosicao(String fonte);
    
    @Query("MATCH (s:SegmentoTematico)-[r:SEQUENCIA]->(next:SegmentoTematico) WHERE s.fonte = $fonte RETURN s, r, next ORDER BY s.posicao")
    List<SegmentoTematico> findSegmentosComSequencia(String fonte);
}