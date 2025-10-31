package fr._3il.ticketron.ocr;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import net.sourceforge.tess4j.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;

/**
 * Service de reconnaissance optique de caractères (OCR) utilisant Tesseract.
 * Permet d'extraire du texte à partir d'images de tickets de caisse et autres documents.
 * Configuré pour reconnaître l'anglais et le français.
 */
@Service
public class OcrService {

    private final ITesseract tesseract = new Tesseract();

    private static final String DEFAULT_TESSDATA_PATH = "tessdata";

    /**
     * Constructeur par défaut utilisant le chemin tessdata standard.
     * @throws URISyntaxException si le chemin vers les données Tesseract est invalide
     */
    public OcrService() throws URISyntaxException {
        this(DEFAULT_TESSDATA_PATH);
    }

    /**
     * Constructeur avec chemin personnalisé vers les données Tesseract.
     * Configure le moteur OCR avec les paramètres optimaux pour la reconnaissance
     * de texte en anglais et français sur des tickets de caisse.
     *
     * @param path chemin vers le dossier tessdata contenant les données de langue
     * @throws URISyntaxException si le chemin est invalide ou inaccessible
     */
    public OcrService(String path) throws URISyntaxException {
        Path tessDataDir = getTessDataFile(path);
        tesseract.setDatapath(tessDataDir.toAbsolutePath().toString());
        tesseract.setLanguage("eng+fra");
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY);
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SPARSE_TEXT);
        tesseract.setVariable("user_defined_dpi", "300");
        tesseract.setVariable("preserve_interword_spaces", "1");
        tesseract.setVariable("tessedit_char_blacklist", "|~`^'\"");
    }

    /**
     * Récupère et valide le chemin vers le dossier tessdata depuis les ressources.
     * Vérifie que la ressource existe, utilise le protocole 'file', et est un dossier valide.
     *
     * @param path chemin relatif vers tessdata dans le classpath
     * @return Path absolu et validé vers le dossier tessdata
     * @throws URISyntaxException si l'URL ne peut être convertie en URI
     * @throws IllegalStateException si la ressource est introuvable,
     *         n'utilise pas le protocole 'file', ou n'est pas un dossier
     */
    private Path getTessDataFile(String path) throws URISyntaxException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Ressource 'tessdata' introuvable dans le classpath : " + path);
        }
        String protocol = url.getProtocol();
        if (!protocol.equalsIgnoreCase("file")) {
            throw new IllegalStateException("Le service OCR ne supporte que le protocole 'file' pour 'tessdata', trouvé: " + protocol);
        }
        Path tessDataDir = Paths.get(url.toURI());
        if (!Files.isDirectory(tessDataDir)) {
            throw new IllegalStateException("'tessdata' n'est pas un dossier: " + tessDataDir);
        }
        return tessDataDir;
    }
    /**
     * Exécute l'OCR sur une image locale et extrait le texte reconnu.
     * Cette méthode est exposée comme outil pour l'agent LLM.
     *
     * @param imagePath chemin absolu vers le fichier image à analyser
     * @return texte extrait de l'image par reconnaissance optique de caractères
     * @throws IOException si le fichier image ne peut être lu
     * @throws TesseractException si l'OCR échoue pendant le traitement
     */
    @Tool(value = "Execute l'OCR sur une image locale donnee par son chemin absolu et retourne le texte extrait.")
    public String runFile(@P("Chemin de l'image") String imagePath) throws IOException, TesseractException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        return tesseract.doOCR(img);
    }

}
