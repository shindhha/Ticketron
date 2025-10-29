package fr._3il.ticketron.ocr;

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