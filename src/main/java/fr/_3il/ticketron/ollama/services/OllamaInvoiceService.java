package fr._3il.ticketron.ollama.services;

import dev.langchain4j.model.chat.ChatModel;
import fr._3il.ticketron.ollama.models.AgentEntity;
import fr._3il.ticketron.ollama.repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service qui interagit avec Ollama pour analyser le texte d'une facture ou d'un ticket.
 * Le modèle Ollama est utilisé localement pour transformer le texte OCR
 * en une phrase claire et contextualisée pour un rapport de note de frais.
 */
@Service
public class OllamaInvoiceService {
    private static final int TIMEOUT_SECONDS = 120;
    private final ChatModel model;
    private final AgentRepository agentRepository;

    public OllamaInvoiceService(@Autowired AgentRepository agentRepository, @Autowired ChatModel model) {
      this.agentRepository = agentRepository;
      this.model = model;
    }

}
