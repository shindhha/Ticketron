package fr._3il.ticketron;

import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.OcrService;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TicketronTest {

  Ticketron ticketron;
  @MockitoSpyBean
  OcrService ocrService;

  @MockitoBean
  ExpenseService expenseService;

  @MockitoSpyBean
  Expense.ExpenseBuilder expenseBuilder;

  private static final String TEST_IMAGE_PATH = "D:\\Travail\\Projets\\ProgPro\\ticketron\\src\\main\\resources\\factures\\f1.jpg";
  public TicketronTest(@Autowired Ticketron ticketron) {
    this.ticketron = ticketron;
  }

  @BeforeEach
  @Disabled("Test désactivé temporairement")
  void setUp() {
    MockitoAnnotations.openMocks(this);
    System.setProperty("logging.level.dev.langchain4j", "DEBUG");
    System.setProperty("logging.level.fr._3il.ticketron", "DEBUG");

  }
  @Test
  @Disabled("Test désactivé temporairement")
  void initContext() {
  }

  @Test
  @Disabled("Test désactivé temporairement")
  void chat() {
  }

  @Test
  @Disabled("Test désactivé temporairement")
  void saveExpense() throws URISyntaxException, TesseractException, IOException {
    OcrService ocrService = new OcrService();
    // GIVEN an image translated by OCR
    String imgText = ocrService.runFile(TEST_IMAGE_PATH);
    String result = ticketron.saveExpense(imgText);
    // EXPECTED LLM Should return a category
    verify(expenseBuilder).merchant(anyString());
    verify(expenseBuilder).date(any());
    verify(expenseBuilder).totalAmount(any());
    verify(expenseBuilder).vatAmount(any());
    verify(expenseBuilder).currency(anyString());
    verify(expenseBuilder).description(anyString());
    verify(expenseBuilder).imagePath(anyString());
    verify(expenseBuilder).confidence(anyFloat());
    verify(expenseService).getCategories();
    verify(expenseService).addCategory(anyString(), anyString(), anyString());
    verify(expenseService).saveExpense();


  }

  @Test
  @Disabled("Test désactivé temporairement")
  void classifyExpense() throws URISyntaxException, TesseractException, IOException {
    OcrService ocrService = new OcrService();
    // GIVEN an image translated by OCR
    String imgText = ocrService.runFile(TEST_IMAGE_PATH);
    String result = ticketron.classifyExpense(imgText);
    // EXPECTED LLM Should return a category
    verify(expenseService).getCategories();
    verify(expenseService, atLeastOnce()).addCategory(anyString(), anyString(), anyString());

  }

  @Test
  @Disabled("Test désactivé temporairement")
  void validateExpense() throws Exception {
    assertNotEquals("",ocrService.runFile(TEST_IMAGE_PATH));
  }

  @Test
  @Disabled("Test désactivé temporairement")
  void processReceipt() throws Exception {
    // GIVEN an image path
    String path = TEST_IMAGE_PATH;
    // WHEN processing the receipt
    String response = ticketron.processReceipt(path);
    // THEN LLM should call OCR tool
    verify(ocrService).runFile(path);
  }


}