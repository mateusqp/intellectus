package intellectus.backend.dto;

import lombok.Data;

@Data
public class SegmentacaoRequest {
    private String texto;
    private String fonte;
    private String modelo;
}