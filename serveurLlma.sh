#!/usr/bin/env bash
set -euo pipefail

MODEL_NAME="${MODEL_NAME:-llama3.1}"
OLLAMA_CONTAINER="${OLLAMA_CONTAINER:-ollama}"
OLLAMA_VOLUME="${OLLAMA_VOLUME:-ollama}"
OLLAMA_PORT="${OLLAMA_PORT:-11434}"
OLLAMA_IMAGE="${OLLAMA_IMAGE:-ollama/ollama:latest}"
HOST="${HOST:-localhost}"

echo "Démarrage du serveur Ollama (container=${OLLAMA_CONTAINER}, port=${OLLAMA_PORT}, modèle=${MODEL_NAME})"

container_exists() { docker ps -a --format '{{.Names}}' | grep -Fxq "${OLLAMA_CONTAINER}"; }
container_running() { docker ps --format '{{.Names}}' | grep -Fxq "${OLLAMA_CONTAINER}"; }

wait_for_api() {
  echo "Attente de l'API Ollama sur http://${HOST}:${OLLAMA_PORT} ..."
  for i in {1..30}; do
    if curl -s "http://${HOST}:${OLLAMA_PORT}/api/version" >/dev/null; then
      echo "API Ollama prête."
      return 0
    fi
    sleep 1
  done
  echo "Timeout: l'API Ollama n'a pas répondu." >&2
  exit 1
}

ensure_container() {
  if container_exists; then
    if container_running; then
      echo "Conteneur '${OLLAMA_CONTAINER}' déjà en cours d'exécution."
    else
      echo "Conteneur '${OLLAMA_CONTAINER}' existant, démarrage..."
      docker start "${OLLAMA_CONTAINER}" >/dev/null
    fi
  else
    echo "Création du conteneur Ollama..."
    docker run -d --name "${OLLAMA_CONTAINER}" \
      -p "${OLLAMA_PORT}:11434" \
      -v "${OLLAMA_VOLUME}:/root/.ollama" \
      "${OLLAMA_IMAGE}" >/dev/null
  fi
}

model_installed() {
  docker exec -i "${OLLAMA_CONTAINER}" ollama list 2>/dev/null | awk '{print $1}' | grep -Fxq "${MODEL_NAME}" || return 1
}

ensure_model() {
  echo "Vérification du modèle '${MODEL_NAME}' ..."
  if model_installed; then
    echo "Modèle déjà présent."
  else
    echo "⬇Téléchargement du modèle '${MODEL_NAME}' (une seule fois, peut être long la 1ère fois)..."
    docker exec -i "${OLLAMA_CONTAINER}" ollama pull "${MODEL_N./AME}"
  fi
}

ensure_container
wait_for_api
ensure_model

echo "Test léger de santé (version) ..."

echo "Prêt ! Ollama écoute sur http://${HOST}:${OLLAMA_PORT} avec le modèle '${MODEL_NAME}'."