#!/bin/bash

echo "Configurando Ollama para Intellectus..."

# Iniciar containers
docker-compose up -d

# Aguardar Ollama inicializar
echo "Aguardando Ollama inicializar..."
sleep 10

# Baixar modelo Llama 3.1
echo "Baixando modelo Llama 3.1..."
docker exec intellectus-ollama ollama pull llama3.1

# Verificar se modelo foi instalado
echo "Verificando instalação..."
docker exec intellectus-ollama ollama list

echo "Setup concluído! Ollama está rodando em http://localhost:11434"
echo "Modelo disponível: llama3.1"