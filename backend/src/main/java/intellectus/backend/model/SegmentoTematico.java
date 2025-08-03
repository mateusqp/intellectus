package intellectus.backend.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

@Node
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SegmentoTematico {
    
    @Id @GeneratedValue
    private Long id;
    
    private String titulo;
    private String conteudo;
    private List<String> temasPrincipais;
    private Integer posicao;
    private String fonte;
    private String interpreteIa;
    private Double grauDeConfianca;
    
    @Relationship(type = "SEQUENCIA", direction = Relationship.Direction.OUTGOING)
    private Set<SegmentoTematico> proximosSegmentos;
    
    @Relationship(type = "CONTRASTA_COM", direction = Relationship.Direction.OUTGOING)
    private Set<SegmentoTematico> contrastes;
    
    @Relationship(type = "DETALHA", direction = Relationship.Direction.OUTGOING)
    private Set<SegmentoTematico> detalhamentos;
    
    @Relationship(type = "RETOMA", direction = Relationship.Direction.OUTGOING)
    private Set<SegmentoTematico> retomadas;
}