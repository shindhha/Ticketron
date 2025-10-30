package fr._3il.ticketron.ocr;// imports utiles en haut du fichier :
import dev.langchain4j.agent.tool.Tool;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;

/**
 * Classe pour le prétraitement d'images de tickets de frais avant OCR.
 * Contient des méthodes pour conversion en niveaux de gris, amélioration,
 * redimensionnement et seuillage adaptatif Sauvola.
 */
public class ImagePreprocessor {

    /**
     * Effectue un premier passage de traitement sur l'image du ticket :
     * conversion en niveaux de gris, agrandissement 2x, et amélioration de la netteté.
     * @param src image source en couleur
     * @return image traitée en niveaux de gris et sharpened
     */
    public BufferedImage receiptPassA(BufferedImage src) {
        BufferedImage g = toGrayscale(src);
        BufferedImage up = upscale2x(g);
        return unsharp(up);
    }



    /**
     * Effectue un second passage de traitement sur l'image :
     * applique receiptPassA, puis ajuste le contraste et applique un seuillage Sauvola.
     * @param src image source en couleur
     * @return image binarisée adaptativement avec amélioration de contraste
     */
    public BufferedImage receiptPassB(BufferedImage src) {
        BufferedImage a = receiptPassA(src);
        BufferedImage boosted = boostContrast(a, 1.2f, -10f);
        return sauvola(boosted, 31, 0.34); // fenêtre ~31, facteur k ~ 0.34
    }

    /**
     * Convertit une image couleur en niveaux de gris.
     * @param src image source couleur
     * @return image en niveaux de gris (type BYTE_GRAY)
     */
    public BufferedImage toGrayscale(BufferedImage src) {
        int w = src.getWidth(), h = src.getHeight();
        BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return gray;
    }

    /**
     * Augmente le contraste d'une image en niveaux de gris.
     * @param srcGray image en niveaux de gris source
     * @param scale facteur multiplicateur de contraste (ex. 1.2f)
     * @param offset décalage appliqué après mise à l'échelle (ex. -10f)
     * @return image avec contraste ajusté
     */
    public BufferedImage boostContrast(BufferedImage srcGray, float scale, float offset) {
        RescaleOp op = new RescaleOp(scale, offset, null);
        BufferedImage dst = new BufferedImage(srcGray.getWidth(), srcGray.getHeight(), srcGray.getType());
        op.filter(srcGray, dst);
        return dst;
    }

    /**
     * Agrandit une image en niveaux de gris d'un facteur 2 en utilisant
     * l'interpolation bicubique pour lisser.
     * @param srcGray image en niveaux de gris source
     * @return image agrandie 2x de largeur et hauteur
     */
    public BufferedImage upscale2x(BufferedImage srcGray) {
        int w = srcGray.getWidth() * 2, h = srcGray.getHeight() * 2;
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(srcGray, 0, 0, w, h, null);
        g.dispose();
        return dst;
    }

    /**
     * Applique un filtre "unsharp mask" pour améliorer la netteté de l'image.
     * Filtre convolutif avec un noyau qui accentue les contours.
     * @param srcGray image en niveaux de gris source
     * @return image avec netteté améliorée
     */
    public BufferedImage unsharp(BufferedImage srcGray) {
        float[] k = {
                0, -1,  0,
                -1,  5, -1,
                0, -1,  0
        };
        ConvolveOp op = new ConvolveOp(new Kernel(3,3,k), ConvolveOp.EDGE_NO_OP, null);
        BufferedImage dst = new BufferedImage(srcGray.getWidth(), srcGray.getHeight(), srcGray.getType());
        op.filter(srcGray, dst);
        return dst;
    }

    /**
     * Applique un seuillage adaptatif selon la méthode de Sauvola
     * pour binariser l'image en fonction du contraste local.
     * @param gray image en niveaux de gris source
     * @param window taille de la fenêtre locale (typiquement une trentaine)
     * @param k paramètre Sauvola, contrôle la sensibilité locale au contraste (environ 0.34)
     * @return image binaire (TYPE_BYTE_BINARY)
     */
    public BufferedImage sauvola(BufferedImage gray, int window, double k) {
        int w = gray.getWidth(), h = gray.getHeight();
        BufferedImage out = new BufferedImage(w,h,BufferedImage.TYPE_BYTE_BINARY);

        long[][] S = new long[h+1][w+1];
        long[][] Q = new long[h+1][w+1];
        for (int y=1;y<=h;y++){
            long rs=0, rq=0;
            for (int x=1;x<=w;x++){
                int v = gray.getRaster().getSample(x-1,y-1,0);
                rs += v; rq += (long)v*v;
                S[y][x] = S[y-1][x] + rs;
                Q[y][x] = Q[y-1][x] + rq;
            }
        }
        int r = Math.max(1, window/2);
        for (int y=0;y<h;y++){
            int y0 = Math.max(0,y-r), y1 = Math.min(h-1,y+r);
            for (int x=0;x<w;x++){
                int x0 = Math.max(0,x-r), x1 = Math.min(w-1,x+r);
                int N = (x1-x0+1)*(y1-y0+1);
                long s = area(S,x0,y0,x1,y1);
                long q = area(Q,x0,y0,x1,y1);
                double mean = s*1.0/N;
                double var = Math.max(0, q*1.0/N - mean*mean);
                double std = Math.sqrt(var);
                double R = 128.0;
                double T = mean * (1 + k*((std/R)-1));
                int v = gray.getRaster().getSample(x,y,0);
                out.setRGB(x,y, 0xFF000000 | ((v>T)?0xFFFFFF:0x000000));
            }
        }
        return out;
    }

    /**
     * Calcule la somme d'une région rectangulaire donnée dans une matrice intégrale.
     * @param I matrice intégrale
     * @param x0, y0 coordonnées du coin supérieur gauche
     * @param x1, y1 coordonnées du coin inférieur droit
     * @return somme des valeurs dans la région rectangulaire
     */
    private long area(long[][] I, int x0,int y0,int x1,int y1){
        return I[y1+1][x1+1]-I[y0][x1+1]-I[y1+1][x0]+I[y0][x0];
    }
}
