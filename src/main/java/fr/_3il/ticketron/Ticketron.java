package fr._3il.ticketron;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.web.multipart.MultipartFile;

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



  // ============================
  // 1️⃣ TRAITEMENT GLOBAL D'UN TICKET
  // ============================
  @UserMessage("""
    Voici le chemin d'une image de ticket à traiter :
    {{imagePath}}

    Directives de l’utilisateur : "{{userDirectives}}"

    Tu disposes d’un outil nommé `ocrRunFile(imagePath)` 
    qui sait lire l’image sur le disque et en extraire le texte.
    Utilise cet outil pour lire le contenu du ticket avant d'analyser la dépense.
""")
  String processReceiptWithInstruction(
          @V("imagePath") String imagePath,
          @V("userDirectives") String userDirectives
  );

  @UserMessage("""
    Voici le chemin d'une image de ticket à traiter :
    {{imagePath}}

    Tu disposes d’un outil nommé `ocrRunFile(imagePath)` 
    qui sait lire l’image sur le disque et en extraire le texte.
    Tu doit utiliser cet outil pour lire le contenu du ticket avant d'analyser la dépense.
""")
  String processReceipt(@V("imagePath") String imagePath);


  // ============================
  // 2️⃣ CLASSIFICATION / APPRENTISSAGE
  // ============================
  @UserMessage("""
        L’utilisateur souhaite que tu analyses cette dépense : {expenseSummary}
        et que tu l’associes à une catégorie.
        Si aucune catégorie existante ne correspond, tu peux créer une nouvelle catégorie
        via l’outil prévu à cet effet.
    """)
  String classifyExpense(String expenseSummary);


  // ============================
  // 3️⃣ VÉRIFICATION ET VALIDATION
  // ============================
  @UserMessage("""
        Vérifie la conformité de cette dépense selon les règles de l’entreprise :
        - type de dépense autorisé,
        - montant raisonnable,
        - justificatif complet.
        Si elle est conforme, confirme l’enregistrement.
        Sinon, ajoute un commentaire expliquant la non-conformité.
        
        Dépense : {expenseSummary}
    """)
  String validateExpense(String expenseSummary);


  // ============================
  // 4️⃣ INTERACTION LIBRE
  // ============================
  @UserMessage("""
        L’utilisateur s’adresse directement à toi :
        "{userQuery}"
        Réponds ou agis selon ton rôle d’assistant de gestion des notes de frais.
    """)
  String chat(String userQuery);
}
