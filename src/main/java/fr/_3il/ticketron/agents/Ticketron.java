package fr._3il.ticketron.agents;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import fr._3il.ticketron.api.models.requests.FlexibleExpense;

/**
 * Ticketron — Agent IA de gestion des notes de frais.
 *
 * Il reçoit des images de tickets, peut exécuter des outils (OCR, base de données),
 * interprète les informations extraites et prend des décisions de classement,
 * validation et enregistrement sans que l’utilisateur n’ait à formater les données.
 */
@SystemMessage("""
        Tu es Ticketron, un agent spécialisé dans la lecture et l'analyse de tickets de caisse.
        Tu DOIS utiliser les outils à ta disposition pour analyser les images encodées en Base64.
        Si une image Base64 est fournie, utilise l'outil `ocrRunBase64(base64Image)` immédiatement.
        Ne tente pas de la décoder manuellement ni d'appeler d'autres outils.
    """)
public interface Ticketron {



  @UserMessage("""
        L’utilisateur souhaite que tu analyses une image de dépense située à l’emplacement suivant : {{imgPath}}.
        Tu disposes d'un outil nommé ocrRunFile(imagePath) qui sait lire l’image sur le disque et en extraire le texte.
        Pour le moment contente-toi de lire les infos importante dans l'image.
    """)
  @Agent
  String analyseExpenseImg(String imgPath);

  @UserMessage("""
          À partir de la description suivante d'une dépense extraite d'un ticket, crée un objet JSON FlexibleExpense.
        Remplis tous les champs possibles (merchant, date, totalAmount, currency, description).
        Voici la description : {{description}}
        
        Réponds uniquement avec un JSON valide. N'écris pas d'introduction ni de résumé.
    """)
  FlexibleExpense createFlexibleExpenseObject(String description);

  @UserMessage("""
          À partir de la description suivante d'une dépense extraite d'un ticket, crée un objet JSON FlexibleExpense.
        Remplis tous les champs possibles (merchant, date, totalAmount, currency, description).
        Voici la description : {{description}}
        
        Réponds uniquement avec un JSON valide. N'écris pas d'introduction ni de résumé.
    """)
  FlexibleExpense createExpenseObject(FlexibleExpense description);


}
