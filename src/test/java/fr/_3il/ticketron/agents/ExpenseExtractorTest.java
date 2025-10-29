package fr._3il.ticketron.agents;

import fr._3il.ticketron.api.models.requests.FlexibleExpense;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.OcrService;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ExpenseExtractorTest {

  ExpenseInterpreter extractor;
  @Autowired
  ExpenseService expenseService;
  public ExpenseExtractorTest(@Autowired ExpenseInterpreter extractor) {
    this.extractor = extractor;
  }


  @Test
  void extractFlexibleExpense() throws URISyntaxException, TesseractException, IOException {
    OcrService ocrService = new OcrService();
    // GIVEN an image translated by OCR
    String imgText = ocrService.runFile(getClass().getResource("/factures/f1.jpg").getPath());
    FlexibleExpense expense = extractor.extractExpense(imgText);
    assertNotNull(expense);
    assertNotNull(expense.merchant);
    assertNotNull(expense.date);
    assertNotNull(expense.totalAmount);
    assertNotNull(expense.currency);
    assertNotNull(expense.description);
    assertNotNull(expense.hour);
  }





}