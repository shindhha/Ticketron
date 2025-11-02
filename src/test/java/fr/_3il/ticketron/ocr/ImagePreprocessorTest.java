package fr._3il.ticketron.ocr;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class ImagePreprocessorTest {

    /**
     * Crée une image en niveaux de gris unie d’une couleur donnée.
     */
    private BufferedImage newGrayImage(int w, int h, int gray) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(gray, gray, gray));
        g.fillRect(0, 0, w, h);
        g.dispose();
        return img;
    }

    /**
     * Test : conversion en niveaux de gris.
     * Vérifie que l’image produite a les mêmes dimensions
     * et que son type est bien BYTE_GRAY.
     */
    @Test
    void toGrayscale_shouldReturnGrayImageSameSize() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage rgb = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0,0,20,10);
        g.dispose();

        BufferedImage gray = p.toGrayscale(rgb);

        assertThat(gray.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);
        assertThat(gray.getWidth()).isEqualTo(20);
        assertThat(gray.getHeight()).isEqualTo(10);
    }

    /**
     * Test : agrandissement 2x.
     * Vérifie que l’image est deux fois plus grande
     * et qu’elle reste en niveaux de gris.
     */
    @Test
    void upscale2x_shouldDoubleDimensions_andKeepGrayType() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage gray = newGrayImage(15, 8, 128);

        BufferedImage up = p.upscale2x(gray);

        assertThat(up.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);
        assertThat(up.getWidth()).isEqualTo(30);
        assertThat(up.getHeight()).isEqualTo(16);
    }

    /**
     * Test : filtre de netteté (unsharp mask).
     * Vérifie que la taille et le type de l’image sont conservés.
     */
    @Test
    void unsharp_shouldPreserveSizeAndType() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage gray = newGrayImage(32, 24, 128);

        BufferedImage sharp = p.unsharp(gray);

        assertThat(sharp.getWidth()).isEqualTo(gray.getWidth());
        assertThat(sharp.getHeight()).isEqualTo(gray.getHeight());
        assertThat(sharp.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);
    }

    /**
     * Test : ajustement du contraste.
     * Vérifie que la transformation modifie bien les niveaux de gris des pixels.
     */
    @Test
    void boostContrast_shouldChangeHistogram_whenScaleOrOffsetApplied() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage gray = newGrayImage(10, 10, 100);

        BufferedImage boosted = p.boostContrast(gray, 1.3f, -10f);

        // Un pixel au centre pour vérifier que la valeur change
        int vBefore = gray.getRaster().getSample(5,5,0);
        int vAfter  = boosted.getRaster().getSample(5,5,0);
        assertThat(vAfter).isNotEqualTo(vBefore);
    }

    /**
     * Test : seuillage adaptatif Sauvola.
     * Vérifie que les zones sombres sont binarisées en noir
     * et les zones claires en blanc (cas réaliste : texte sombre sur fond clair).
     */
    @Test
    void sauvola_shouldBinarize_darkInkOnLightBackground() {
        ImagePreprocessor p = new ImagePreprocessor();

        // Fond clair
        BufferedImage gray = new BufferedImage(80, 40, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.setColor(new Color(200,200,200)); // fond
        g.fillRect(0,0,80,40);

        // Bloc sombre (simule de l’encre)
        g.setColor(new Color(60,60,60));
        g.fillRect(10,10,20,20);
        g.dispose();

        BufferedImage bin = p.sauvola(gray, 31, 0.34);

        // Pixel dans le bloc sombre => noir
        int ink = (bin.getRGB(15,20) & 0xFFFFFF);
        // Pixel dans le fond clair => blanc
        int bg  = (bin.getRGB(60,20) & 0xFFFFFF);

        assertThat(ink).isEqualTo(0x000000);
        assertThat(bg).isEqualTo(0xFFFFFF);
        assertThat(bin.getType()).isEqualTo(BufferedImage.TYPE_BYTE_BINARY);
    }

    /**
     * Test : première passe de traitement (receiptPassA).
     * Vérifie que l’image est bien convertie en niveaux de gris,
     * agrandie et que les dimensions sont doublées.
     */
    @Test
    void receiptPassA_shouldReturnSharpenedGray2x() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage color = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        BufferedImage out = p.receiptPassA(color);

        assertThat(out.getType()).isEqualTo(BufferedImage.TYPE_BYTE_GRAY);
        assertThat(out.getWidth()).isEqualTo(100);
        assertThat(out.getHeight()).isEqualTo(60);
    }

    /**
     * Test : seconde passe de traitement (receiptPassB).
     * Vérifie que la sortie finale est bien une image binaire (noir/blanc),
     * ce qui est le format attendu pour l’OCR.
     */
    @Test
    void receiptPassB_shouldReturnBinary() {
        ImagePreprocessor p = new ImagePreprocessor();
        BufferedImage color = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        BufferedImage out = p.receiptPassB(color);

        assertThat(out.getType()).isEqualTo(BufferedImage.TYPE_BYTE_BINARY);
    }
}
