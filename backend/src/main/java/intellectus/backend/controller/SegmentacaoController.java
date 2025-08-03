package intellectus.backend.controller;

import intellectus.backend.dto.SegmentacaoRequest;
import intellectus.backend.dto.SegmentoResponse;
import intellectus.backend.model.SegmentoTematico;
import intellectus.backend.service.SegmentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/segmentacao")
@CrossOrigin(origins = "*")
public class SegmentacaoController {
    
    @Autowired
    private SegmentacaoService segmentacaoService;
    
    @PostMapping("/segmentar")
    public ResponseEntity<List<SegmentoResponse>> segmentarTexto(@RequestBody SegmentacaoRequest request) {
        try {
            List<SegmentoTematico> segmentos = segmentacaoService.segmentarTexto(
                request.getTexto(), 
                request.getFonte(), 
                request.getModelo()
            );
            
            List<SegmentoResponse> response = segmentos.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/fonte/{fonte}")
    public ResponseEntity<List<SegmentoResponse>> buscarPorFonte(@PathVariable String fonte) {
        try {
            List<SegmentoTematico> segmentos = segmentacaoService.buscarSegmentosPorFonte(fonte);
            
            List<SegmentoResponse> response = segmentos.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private SegmentoResponse converterParaResponse(SegmentoTematico segmento) {
        SegmentoResponse response = new SegmentoResponse();
        response.setId(segmento.getId());
        response.setTitulo(segmento.getTitulo());
        response.setConteudo(segmento.getConteudo());
        response.setTemasPrincipais(segmento.getTemasPrincipais());
        response.setPosicao(segmento.getPosicao());
        response.setFonte(segmento.getFonte());
        response.setInterpreteIa(segmento.getInterpreteIa());
        response.setGrauDeConfianca(segmento.getGrauDeConfianca());
        return response;
    }
}