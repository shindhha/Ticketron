package fr._3il.ticketron;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Ticketron — Agent IA de gestion des notes de frais.
 *
 * Il reçoit des images de tickets, peut exécuter des outils (OCR, base de données),
 * interprète les informations extraites et prend des décisions de classement,
 * validation et enregistrement sans que l’utilisateur n’ait à formater les données.
 */
public interface Ticketron {

  // ============================
  // CONTEXTE GÉNÉRAL DU MODÈLE
  // ============================
  @SystemMessage("""
        Tu es Ticketron, un agent IA expert en gestion de notes de frais.
        Ton rôle est de :
        - Analyser des tickets de caisse fournis sous forme d’image,
        - Extraire et structurer leurs informations clés,
        - Classer automatiquement la dépense dans la catégorie appropriée,
        - Enregistrer les résultats via les outils disponibles.

        Tu disposes d’outils pour :
        - extraire le texte d’un ticket à partir d’une image (OCR),
        - enregistrer des dépenses en base,
        - créer ou retrouver des catégories de dépenses.
        
        L’utilisateur peut te donner des directives précises (ex : “classe cela comme hébergement”).
        Tu dois raisonner et utiliser les outils nécessaires pour accomplir sa demande.
    """)
  void initContext();


  // ============================
  // 1️⃣ TRAITEMENT GLOBAL D'UN TICKET
  // ============================
  @UserMessage("""
        Voici une image de ticket à traiter : {imagePath}
        Directives de l’utilisateur : "{userDirectives}"
        
        Ton objectif :
        1. Utiliser l’outil OCR pour lire le contenu du ticket,
        2. Extraire les données essentielles (date, montant, commerçant, TVA, devise...),
        3. Classer la dépense selon les catégories connues,
        4. Enregistrer la dépense en base via l’outil approprié.
        
        Tu dois raisonner étape par étape et appeler les outils nécessaires.
        Ne renvoie pas de texte explicatif, exécute les actions.
    """)
  String processReceipt(String imagePath, String userDirectives);


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
