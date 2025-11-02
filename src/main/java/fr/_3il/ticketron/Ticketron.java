package fr._3il.ticketron;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface définissant l'agent IA Ticketron pour la gestion automatisée des notes de frais.
 *
 * <p>Ticketron est un agent conversationnel spécialisé qui utilise LangChain4j et Ollama
 * pour traiter des images de tickets de caisse via OCR, extraire les informations pertinentes,
 * classifier les dépenses, valider leur conformité et les enregistrer en base de données.</p>
 *
 * <p>L'agent dispose d'outils pour :</p>
 * <ul>
 *   <li>Exécuter l'OCR sur des images (fichier local ou Base64)</li>
 *   <li>Gérer les catégories de dépenses</li>
 *   <li>Construire et enregistrer des objets Expense</li>
 *   <li>Valider la conformité des dépenses selon les règles métier</li>
 * </ul>
 *
 * <p>Le message système configure le comportement de base de l'agent, tandis que
 * chaque méthode définit un cas d'usage spécifique avec son propre prompt utilisateur.</p>
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

    /**
     * Traite une image de ticket avec des directives utilisateur personnalisées.
     * Utilise l'outil OCR pour lire le contenu du ticket depuis le chemin fourni,
     * puis analyse la dépense selon les instructions spécifiques de l'utilisateur.
     *
     * @param imagePath chemin absolu vers le fichier image du ticket à traiter
     * @param userDirectives instructions personnalisées de l'utilisateur pour le traitement
     *                       (ex: "enregistre cette dépense", "vérifie la TVA", etc.)
     * @return réponse de l'agent après traitement et analyse du ticket
     */
    @UserMessage("""
    Voici le chemin d'une image de ticket à traiter :
    {{imagePath}}

    Directives de l'utilisateur : "{{userDirectives}}"

    Tu disposes d'un outil nommé `ocrRunFile(imagePath)` 
    qui sait lire l'image sur le disque et en extraire le texte.
    Utilise cet outil pour lire le contenu du ticket avant d'analyser la dépense.
""")
    String processReceiptWithInstruction(
            @V("imagePath") String imagePath,
            @V("userDirectives") String userDirectives
    );

    /**
     * Traite une image de ticket avec le comportement par défaut.
     * Utilise l'outil OCR pour extraire le texte du ticket, puis analyse
     * automatiquement les informations de la dépense sans directive spécifique.
     *
     * @param imagePath chemin absolu vers le fichier image du ticket à traiter
     * @return réponse de l'agent après traitement et analyse du ticket
     */
    @UserMessage("""
    Voici le chemin d'une image de ticket à traiter :
    {{imagePath}}

    Tu disposes d'un outil nommé `ocrRunFile(imagePath)` 
    qui sait lire l'image sur le disque et en extraire le texte.
    Tu doit utiliser cet outil pour lire le contenu du ticket avant d'analyser la dépense.
""")
    String processReceipt(@V("imagePath") String imagePath);


    // ============================
    // 2️⃣ CLASSIFICATION / APPRENTISSAGE
    // ============================

    /**
     * Classifie une dépense en lui associant une ou plusieurs catégories appropriées.
     * L'agent consulte les catégories existantes via les outils disponibles et peut
     * créer de nouvelles catégories si aucune ne correspond parfaitement.
     *
     * @param expenseSummary résumé textuel de la dépense à classifier
     *                       (ex: "Restaurant Le Bistrot - 45.50€ - déjeuner client")
     * @return réponse de l'agent indiquant les catégories associées à la dépense
     */
    @UserMessage("""
        L'utilisateur souhaite que tu analyses cette dépense : {{expenseSummary}}
        et que y associe une ou plusieur catégorie.
        Tu disposes d'une liste de catégories accessible via outil.
        Si aucune catégorie existante ne correspond, tu peux créer une nouvelle catégorie
        via l'outil prévu à cet effet.
        Indique à l'utilisateur la ou les catégories associées.
    """)
    String classifyExpense(String expenseSummary);

    /**
     * Analyse et enregistre une dépense complète en base de données.
     * L'agent extrait tous les champs pertinents (montant, date, marchand, catégorie, etc.),
     * crée la catégorie si nécessaire, puis enregistre la dépense via les outils disponibles.
     *
     * @param expenseSummary résumé ou description complète de la dépense à enregistrer
     * @return confirmation d'enregistrement ou message d'erreur de l'agent
     */
    @UserMessage("""
        L'utilisateur souhaite que tu analyses cette dépense : {{expenseSummary}}
        Il souhaite que tu l'enregistre dans la base de données avec tous les champs pertinents.
        Tu dispose d'outil pour renseigner une à une les infos puis pour les enregistrées.
        N'oublie pas de commencer par enregistré la catégorie si elle n'existe pas encore.
          """)
    String saveExpense(String expenseSummary);


    // ============================
    // 3️⃣ VÉRIFICATION ET VALIDATION
    // ============================

    /**
     * Valide la conformité d'une dépense selon les règles métier de l'entreprise.
     * Vérifie les critères suivants :
     * <ul>
     *   <li>Type de dépense autorisé</li>
     *   <li>Montant raisonnable et dans les limites</li>
     *   <li>Justificatif complet et valide</li>
     * </ul>
     *
     * <p>Si la dépense est conforme, l'agent confirme son enregistrement.
     * Sinon, il ajoute un commentaire détaillé expliquant les non-conformités détectées.</p>
     *
     * @param expenseSummary résumé de la dépense à valider
     * @return résultat de la validation avec confirmation ou explication des problèmes
     */
    @UserMessage("""
        Verifie la conformite de cette depense selon les regles de l'entreprise :
        - type de dépense autorisé,
        - montant raisonnable,
        - justificatif complet.
        Si elle est conforme, confirme l'enregistrement.
        Sinon, ajoute un commentaire expliquant la non-conformité.
        
        Dépense : {expenseSummary}
    """)
    String validateExpense(String expenseSummary);


    // ============================
    // 4️⃣ INTERACTION LIBRE
    // ============================

    /**
     * Permet une interaction conversationnelle libre avec l'agent.
     * L'utilisateur peut poser des questions ou donner des instructions
     * dans le contexte de la gestion des notes de frais, et l'agent
     * répond selon son rôle d'assistant spécialisé.
     *
     * @param userQuery question ou instruction de l'utilisateur en langage naturel
     * @return réponse de l'agent adaptée à la requête
     */
    @UserMessage("""
        L'utilisateur s'adresse directement à toi :
        "{userQuery}"
        Réponds ou agis selon ton rôle d'assistant de gestion des notes de frais.
    """)
    String chat(String userQuery);
}
