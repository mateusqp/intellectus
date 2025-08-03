package intellectus.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.HashMap;

@Service
public class AIService {
    
    @Value("${ollama.url:http://localhost:11434}")
    private String ollamaUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String generateResponse(String prompt, String context) {
        try {
            String fullPrompt = buildKnowledgePrompt(prompt, context);
            
            Map<String, Object> request = new HashMap<>();
            request.put("model", "llama3.1");
            request.put("prompt", fullPrompt);
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
            return "Erro ao processar solicitação: " + e.getMessage();
        }
    }
    
    public String mapKnowledge(String subject, String knowledgeBase) {
        String prompt = String.format(
            "Analise o seguinte banco de conhecimento sobre '%s' e crie um mapa estruturado " +
            "mostrando os principais conceitos, suas relações e hierarquia. " +
            "Formate como uma estrutura clara e navegável:\n\n%s",
            subject, knowledgeBase
        );
        
        return generateResponse(prompt, "");
    }
    
    public String answerQuestion(String question, String knowledgeContext) {
        String prompt = String.format(
            "Com base no contexto de conhecimento fornecido, responda a pergunta de forma " +
            "didática e estruturada. Se necessário, explique conceitos relacionados.\n\n" +
            "Pergunta: %s", question
        );
        
        return generateResponse(prompt, knowledgeContext);
    }
    
    private String buildKnowledgePrompt(String prompt, String context) {
        StringBuilder fullPrompt = new StringBuilder();
        
        fullPrompt.append("Você é um assistente especializado em educação e mapeamento de conhecimento. ");
        fullPrompt.append("Sua função é ajudar estudantes a compreender e navegar por informações complexas. ");
        
        if (!context.isEmpty()) {
            fullPrompt.append("\n\nContexto do conhecimento:\n").append(context).append("\n\n");
        }
        
        fullPrompt.append("Solicitação: ").append(prompt);
        
        return fullPrompt.toString();
    }
}