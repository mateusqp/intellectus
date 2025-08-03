package intellectus.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class SegmentoResponse {
    private Long id;
    private String titulo;
    private String conteudo;
    private List<String> temasPrincipais;
    private Integer posicao;
    private String fonte;
    private String interpreteIa;
    private Double grauDeConfianca;
}