package fr._3il.ticketron.ocr;

<<<<<<< HEAD
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitaires et d’intégration pour la classe OcrService.
 * Ces tests vérifient la configuration du service et son comportement
 * lors de l’exécution d’un OCR sur une image.
 */
class OcrServiceTest {

    /**
     * Test : constructeur avec un dossier inexistant.
     * Vérifie que le service lève une IllegalStateException
     * lorsque le dossier "tessdata" n’existe pas dans le classpath.
     */
    @Test
    void constructor_shouldThrow_ifTessdataMissing() {
        assertThatThrownBy(() -> new OcrService("tessdata-introuvable"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ressource 'tessdata' introuvable");
    }

    /**
     * Test : constructeur par défaut sans tessdata disponible.
     * Vérifie que l’erreur renvoyée est claire si la ressource n’est pas trouvée.
     */
    @Test
    void defaultConstructor_shouldFailIfNoClasspathTessdata() {
        try {
            new OcrService();
        } catch (Exception e) {
            assertThat(e)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("tessdata");
        }
    }

    /**
     * Test d’intégration : exécution complète de l’OCR.
     * Nécessite que le dossier "tessdata/" soit présent dans src/test/resources.
     * Génère une image de test avec du texte, lance l’OCR et vérifie
     * que le texte reconnu contient bien “Bonjour”.
     */
    @Test
    void runFile_shouldOCRText_whenTessdataAvailable(@TempDir Path tmp) throws Exception {
        // On saute le test si tessdata n’est pas dans le classpath
        URL tess = Thread.currentThread().getContextClassLoader().getResource("tessdata");
        Assumptions.assumeTrue(tess != null, "tessdata absent du classpath – test ignoré");

        // Création d’une image avec texte “Bonjour 123”
        int w = 800, h = 200;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 80));
        g.drawString("Bonjour 123", 50, 120);
        g.dispose();

        File file = tmp.resolve("ocr_test.png").toFile();
        ImageIO.write(img, "png", file);

        // Exécution de l’OCR
        OcrService service = new OcrService("tessdata");
        String text = service.runFile(file.getAbsolutePath());

        // Vérifications souples (OCR ≠ exact)
        assertThat(text).isNotBlank();
        assertThat(text.toLowerCase()).contains("bonjour");
    }

    /**
     * Test : tentative d’OCR sur une image inexistante.
     * Vérifie qu’une IOException est levée.
     */
    @Test
    void runFile_shouldThrow_onMissingImage() throws URISyntaxException {
        URL tess = Thread.currentThread().getContextClassLoader().getResource("tessdata");
        Assumptions.assumeTrue(tess != null, "tessdata absent du classpath – test ignoré");

        OcrService service = new OcrService("tessdata");

        assertThatThrownBy(() -> service.runFile("/chemin/inexistant.png"))
                .isInstanceOf(java.io.IOException.class);
    }

    /**
     * Test : dossier tessdata présent mais n’est pas un répertoire.
     * Crée un fichier temporaire pour simuler une ressource invalide.
     * Vérifie que le service lève une IllegalStateException.
     */
    @Test
    void getTessDataFile_shouldThrow_ifNotADirectory(@TempDir Path tmp) throws Exception {
        Path fakeFile = tmp.resolve("fakefile");
        Files.writeString(fakeFile, "not a folder");

        // Simule une ressource 'tessdata' pointant vers un fichier
        URL fakeUrl = fakeFile.toUri().toURL();
        ClassLoader loader = new ClassLoader(Thread.currentThread().getContextClassLoader()) {
            @Override
            public URL getResource(String name) {
                if (name.equals("tessdata")) {
                    return fakeUrl;
                }
                return super.getResource(name);
            }
        };
        Thread.currentThread().setContextClassLoader(loader);

        assertThatThrownBy(() -> new OcrService("tessdata"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("n'est pas un dossier");
    }
    /**
     * Test : Charge une image depuis les ressources, et vérife que l'OCR retourne un résultat
     *
     */
    @Test
    void runFile_withExistingImagePath_returnsText() throws Exception {
        URL tess = Thread.currentThread().getContextClassLoader().getResource("tessdata");
        Assumptions.assumeTrue(tess != null, "tessdata absent du classpath — test ignoré");
        URL imgUrl = getClass().getResource("/factures/f1.jpg");
        Assumptions.assumeTrue(imgUrl != null, "/factures/f1.jpg manquant — test ignoré");

        String imgPath = java.nio.file.Paths.get(imgUrl.toURI()).toString();

        try (InputStream in = java.nio.file.Files.newInputStream(java.nio.file.Path.of(imgPath))) {
            BufferedImage bi = ImageIO.read(in);
            Assumptions.assumeTrue(bi != null, "Image illisible — test ignoré");
        }

        OcrService service = new OcrService("tessdata");
        String text = service.runFile(imgPath);

        assertThat(text).isNotBlank();
    }

}
=======
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class OcrServiceTest {

  @Test
  void runFile() throws URISyntaxException, TesseractException, IOException {
    OcrService ocrService = new OcrService();
    String text = ocrService.runFile(getClass().getResource("/factures/f1.jpg").getPath());
    assertEquals("= —\n" +
            "\n" +
            "=\n" +
            "\n" +
            "iin Y\n" +
            "\n" +
            "<& market\n" +
            "\n" +
            ">\n" +
            "\n" +
            "HAUTEVIÈLE LOMPNES\n" +
            "\n" +
            "Tel : 04 74 35 31 14\n" +
            "\n" +
            "du Lundi au Samedi de 8h30 à 19h30\n" +
            "\n" +
            "vescarr  A\n" +
            "\n" +
            "QTE\n" +
            "\n" +
            "fro.\n" +
            "\n" +
            "i\n" +
            "\n" +
            "PALMITO LU\n" +
            "\n" +
            "1.00¢\n" +
            "\n" +
            "PET 1L COCA COLA L\n" +
            "\n" +
            "TOTAL ALIMENTAIRE\n" +
            "\n" +
            "1.42¢\n" +
            "\n" +
            "2.42¢\n" +
            "\n" +
            "ny\n" +
            "\n" +
            "2 IIE)\n" +
            "\n" +
            "==\n" +
            "\n" +
            "TOTAL À PAYER\n" +
            "\n" +
            "ESPEC!\n" +
            "\n" +
            "2.50\n" +
            "\n" +
            "Votre Monnaie\n" +
            "\n" +
            "-0.08e\n" +
            "\n" +
            "ong\n" +
            "\n" +
            "001\n" +
            "\n" +
            "000242\n" +
            "\n" +
            "01/02/2016\n" +
            "\n" +
            "18:25:08\n" +
            "\n" +
            "MARKET Hauteville\n" +
            "\n" +
            "vous at vi visite\n", text);
  }
}
>>>>>>> LinkLLMBD
