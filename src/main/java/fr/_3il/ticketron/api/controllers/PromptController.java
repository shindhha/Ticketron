package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.Ticketron;
import fr._3il.ticketron.api.models.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrôleur REST pour la gestion des requêtes utilisateur vers l'agent Ticketron.
 * Expose un endpoint permettant l'upload d'images de tickets avec des instructions
 * de traitement personnalisées. Gère la réception des fichiers, leur sauvegarde
 * temporaire et la délégation du traitement à l'agent IA.
 */
@RestController
public class PromptController {

    private Ticketron ticketron;

    /**
     * Constructeur avec injection de l'agent Ticketron.
     *
     * @param ticketron instance de l'agent IA pour le traitement des tickets
     */
    public PromptController(@Autowired Ticketron ticketron) {
        this.ticketron = ticketron;
    }

    /**
     * Endpoint POST pour soumettre des images de tickets avec des instructions de traitement.
     * Accepte un ou plusieurs fichiers (images de tickets) accompagnés d'instructions optionnelles.
     * Les fichiers sont sauvegardés temporairement dans le répertoire système temporaire
     * avec un préfixe timestamp pour éviter les collisions de noms.
     *
     * <p>Chaque fichier est ensuite traité par l'agent Ticketron selon les instructions fournies.
     * Le traitement inclut l'OCR, l'extraction des informations et l'enregistrement des dépenses.</p>
     *
     * @param prompt objet contenant les fichiers uploadés et les instructions de traitement
     * @return message de confirmation indiquant que les tickets sont en cours d'analyse
     * @throws IOException si l'écriture des fichiers sur le disque échoue
     * @throws IllegalArgumentException si aucun fichier n'est fourni dans la requête
     */
    @PostMapping("/chat")
    public String processPrompt(@ModelAttribute Prompt prompt) throws IOException {
        if (prompt.files == null || prompt.files.length == 0) {
            throw new IllegalArgumentException("Aucun fichier envoyé.");
        }

        Path uploadDir = Paths.get(System.getProperty("java.io.tmpdir"), "ticketron_uploads");
        Files.createDirectories(uploadDir);

        for (var file : prompt.files) {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath.toFile());

            ticketron.processReceiptWithInstruction(filePath.toString(), prompt.instructions);
        }
        return "Tickets reçus et en cours d'analyse.";
    }
}
