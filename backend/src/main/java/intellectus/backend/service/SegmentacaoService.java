package intellectus.backend.service;

import intellectus.backend.model.SegmentoTematico;
import intellectus.backend.repository.SegmentoTematicoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SegmentacaoService {
    
    @Autowired
    private SegmentoTematicoRepository repository;
    
    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public List<SegmentoTematico> segmentarTexto(String texto, String fonte, String modelo) {
        try {
            String prompt = criarPromptSegmentacao(texto);
            String resposta = chamarIA(prompt, modelo);
            
            List<SegmentoTematico> segmentos = processarRespostaIA(resposta, fonte, modelo);
            criarRelacoes(segmentos);
            
            return repository.saveAll(segmentos);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro na segmentação: " + e.getMessage(), e);
        }
    }
    
    private String criarPromptSegmentacao(String texto) {
        return String.format("""
            Leia o texto abaixo com atenção e divida-o em unidades temáticas coerentes.
            
            Para cada segmento, forneça:
            - titulo: um título representativo (máximo 50 caracteres)
            - conteudo: o trecho textual exato do segmento
            - temas_principais: lista de 3-5 palavras-chave principais
            - descricao: breve descrição em uma frase
            
            Mantenha a sequência lógica do texto original.
            
            Formato de saída: JSON array com objetos contendo os campos acima.
            
            Texto:
            %s
            """, texto);
    }
    
    private String chamarIA(String prompt, String modelo) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", modelo != null ? modelo : "llama3.1");
            request.put("prompt", prompt);
            request.put("stream", false);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            String response = restTemplate.postForObject(
                ollamaUrl + "/api/generate", 
                entity, 
                String.class
            );
            
            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse.get("response").asText();
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar IA: " + e.getMessage(), e);
        }
    }
    
    private List<SegmentoTematico> processarRespostaIA(String resposta, String fonte, String modelo) {
        try {
            // Extrair JSON da resposta (pode vir com texto adicional)
            String jsonStr = extrairJSON(resposta);
            JsonNode segmentosJson = objectMapper.readTree(jsonStr);
            
            List<SegmentoTematico> segmentos = new ArrayList<>();
            
            for (int i = 0; i < segmentosJson.size(); i++) {
                JsonNode segmentoJson = segmentosJson.get(i);
                
                SegmentoTematico segmento = new SegmentoTematico();
                segmento.setTitulo(segmentoJson.get("titulo").asText());
                segmento.setConteudo(segmentoJson.get("conteudo").asText());
                segmento.setPosicao(i + 1);
                segmento.setFonte(fonte);
                segmento.setInterpreteIa(modelo);
                segmento.setGrauDeConfianca(0.8); // Valor padrão
                
                // Processar temas principais
                JsonNode temasNode = segmentoJson.get("temas_principais");
                List<String> temas = new ArrayList<>();
                if (temasNode.isArray()) {
                    temasNode.forEach(tema -> temas.add(tema.asText()));
                }
                segmento.setTemasPrincipais(temas);
                
                segmentos.add(segmento);
            }
            
            return segmentos;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta da IA: " + e.getMessage(), e);
        }
    }
    
    private String extrairJSON(String resposta) {
        // Procurar por array JSON na resposta
        int inicioArray = resposta.indexOf('[');
        int fimArray = resposta.lastIndexOf(']');
        
        if (inicioArray != -1 && fimArray != -1 && fimArray > inicioArray) {
            return resposta.substring(inicioArray, fimArray + 1);
        }
        
        throw new RuntimeException("JSON não encontrado na resposta da IA");
    }
    
    private void criarRelacoes(List<SegmentoTematico> segmentos) {
        for (int i = 0; i < segmentos.size() - 1; i++) {
            SegmentoTematico atual = segmentos.get(i);
            SegmentoTematico proximo = segmentos.get(i + 1);
            
            if (atual.getProximosSegmentos() == null) {
                atual.setProximosSegmentos(new HashSet<>());
            }
            atual.getProximosSegmentos().add(proximo);
        }
    }
    
    public List<SegmentoTematico> buscarSegmentosPorFonte(String fonte) {
        return repository.findByFonteOrderByPosicao(fonte);
    }
}