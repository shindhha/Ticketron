package fr._3il.ticketron.api.models;

import org.springframework.web.multipart.MultipartFile;

/**
 * Modèle de données pour les requêtes utilisateur vers l'API Ticketron.
 * Encapsule les fichiers d'images de tickets uploadés et les instructions
 * de traitement associées envoyées par l'utilisateur via l'endpoint REST.
 *
 * <p>Utilisé avec l'annotation @ModelAttribute dans le contrôleur pour
 * recevoir automatiquement les données de formulaire multipart.</p>
 */
public class Prompt {
    /**
     * Tableau de fichiers uploadés (images de tickets de caisse).
     * Peut contenir une ou plusieurs images à traiter en une seule requête.
     */
    public MultipartFile[] files;

    /**
     * Instructions textuelles de l'utilisateur pour le traitement des tickets.
     * Exemple : "enregistre ces dépenses", "analyse et classifie",
     * "vérifie la conformité et enregistre si valide".
     * Peut être null si l'utilisateur ne donne pas de directive spécifique.
     */
    public String instructions;
}
