# API de Segmentação Temática

## Endpoint Principal

### POST /api/segmentacao/segmentar

Segmenta um texto longo em unidades temáticas coerentes.

**Request:**
```json
{
  "texto": "Capítulo completo do livro aqui...",
  "fonte": "A República - Platão - Capítulo 1",
  "modelo": "llama3.1"
}
```

**Response:**
```json
[
  {
    "id": 1,
    "titulo": "Introdução ao Conceito de Justiça",
    "conteudo": "Trecho do texto original...",
    "temasPrincipais": ["justiça", "virtude", "estado", "indivíduo"],
    "posicao": 1,
    "fonte": "A República - Platão - Capítulo 1",
    "interpreteIa": "llama3.1",
    "grauDeConfianca": 0.8
  },
  {
    "id": 2,
    "titulo": "Definições Tradicionais de Justiça",
    "conteudo": "Segundo trecho...",
    "temasPrincipais": ["definição", "tradição", "sócrates"],
    "posicao": 2,
    "fonte": "A República - Platão - Capítulo 1",
    "interpreteIa": "llama3.1",
    "grauDeConfianca": 0.8
  }
]
```

### GET /api/segmentacao/fonte/{fonte}

Recupera segmentos já processados por fonte.

## Estrutura do Grafo Neo4j

- **Nós**: SegmentoTematico
- **Relações**: 
  - SEQUENCIA (conecta segmentos consecutivos)
  - CONTRASTA_COM
  - DETALHA  
  - RETOMA

## Exemplo de Uso

```bash
curl -X POST http://localhost:8080/api/segmentacao/segmentar \
  -H "Content-Type: application/json" \
  -d '{
    "texto": "Seu capítulo aqui...",
    "fonte": "Livro - Capítulo X",
    "modelo": "llama3.1"
  }'
```