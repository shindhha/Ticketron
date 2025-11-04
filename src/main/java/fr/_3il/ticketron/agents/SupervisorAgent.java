package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.V;

/**
 * Ticketron — Agent IA de gestion des notes de frais.
 *
 * Il reçoit des images de tickets, peut exécuter des outils (OCR, base de données),
 * interprète les informations extraites et prend des décisions de classement,
 * validation et enregistrement sans que l’utilisateur n’ait à formater les données.
 */

public interface SupervisorAgent {



  @Agent
  String invoke(@V("request") String request, @V("supervisorContext") String supervisorContext,@MemoryId int memoryId);



}
