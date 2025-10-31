package fr._3il.ticketron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * Service de récupération des variables d'environnement et de configuration.
 * Charge les propriétés depuis le fichier 'config' présent dans le classpath
 * et expose les configurations du modèle LLM (URL et nom).
 */
@Service
@PropertySource("classpath:/config")
public class EnvGetter {

    /**
     * URL du serveur hébergeant le modèle LLM (ex: Ollama).
     * Chargée depuis la propriété MODEL_URL du fichier de configuration.
     */
    @Value("${MODEL_URL}")
    private String modelUrl;

    /**
     * Nom du modèle LLM à utiliser pour le traitement.
     * Chargé depuis la propriété MODEL_NAME du fichier de configuration.
     */
    @Value("${MODEL_NAME}")
    private String modelName;

    /**
     * Récupère le nom du modèle LLM configuré.
     *
     * @return le nom du modèle (ex: "llama3.2", "mistral")
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * Récupère l'URL du serveur hébergeant le modèle LLM.
     *
     * @return l'URL complète du serveur (ex: "http://localhost:11434")
     */
    public String getModelUrl() {
        return modelUrl;
    }
}
