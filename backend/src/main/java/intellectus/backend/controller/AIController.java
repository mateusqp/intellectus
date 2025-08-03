package intellectus.backend.controller;

import intellectus.backend.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    
    @Autowired
    private AIService aiService;
    
    @PostMapping("/map-knowledge")
    public Map<String, String> mapKnowledge(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String knowledgeBase = request.get("knowledgeBase");
        
        String response = aiService.mapKnowledge(subject, knowledgeBase);
        
        return Map.of("response", response);
    }
    
    @PostMapping("/answer")
    public Map<String, String> answerQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String context = request.getOrDefault("context", "");
        
        String response = aiService.answerQuestion(question, context);
        
        return Map.of("response", response);
    }
    
    @PostMapping("/generate")
    public Map<String, String> generateResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String context = request.getOrDefault("context", "");
        
        String response = aiService.generateResponse(prompt, context);
        
        return Map.of("response", response);
    }
}