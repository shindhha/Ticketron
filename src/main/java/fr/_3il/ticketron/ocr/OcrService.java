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

@Service
public class OcrService {

    private final ITesseract tesseract = new Tesseract();

    private static final String DEFAULT_TESSDATA_PATH = "tessdata";
    public OcrService() throws URISyntaxException {
        this(DEFAULT_TESSDATA_PATH);
    }

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
    @Tool(value = "Execute l’OCR sur une image locale donnee par son chemin absolu et retourne le texte extrait.")
    public String runFile(@P("Chemin de l'image") String imagePath) throws IOException, TesseractException {
        BufferedImage img = ImageIO.read(new File(imagePath));
        return tesseract.doOCR(img);
    }

}
