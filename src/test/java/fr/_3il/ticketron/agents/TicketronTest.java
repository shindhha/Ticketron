package fr._3il.ticketron.agents;

import fr._3il.ticketron.api.models.Category;
import fr._3il.ticketron.api.models.Expense;
import fr._3il.ticketron.api.models.requests.FlexibleCategory;
import fr._3il.ticketron.api.models.requests.FlexibleExpense;
import fr._3il.ticketron.api.services.ExpenseService;
import fr._3il.ticketron.ocr.OcrService;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TicketronTest {

  Ticketron ticketron;
  @MockitoSpyBean
  OcrService ocrService;

  @MockitoSpyBean
  ExpenseService expenseService;

  Expense expense;



  public TicketronTest(@Autowired Ticketron ticketron) {
    this.ticketron = ticketron;
  }

  @BeforeAll
  static void initAll() {
    System.setProperty("logging.level.dev.langchain4j", "DEBUG");
    System.setProperty("logging.level.fr._3il.ticketron", "DEBUG");
  }

  @BeforeEach
  void setUp() throws URISyntaxException, TesseractException, IOException {
    MockitoAnnotations.openMocks(this);

  }




  @Test
  void anaylyseExpenseImg() throws TesseractException, IOException {
    // GIVEN an image path
    String path = getClass().getResource("/factures/f1.jpg").getPath();
    String response = ticketron.analyseExpenseImg(path);
    // EXPECTED OCR service is called
    verify(ocrService).runFile(path);
    assertEquals("", response);
  }


  @Test
  void createFlexibleExpenseObject() {
    String prompt = "J'ai lu l'image que vous m'avez envoyée. Voici les informations importantes que j'ai extraites :\n" +
            "\n" +
            "* Le nom du magasin : MARKET Hauteville\n" +
            "* La date de la facture : 01/02/2016\n" +
            "* L'heure de la facture : 18:25:08\n" +
            "* Les produits achetés :\n" +
            " + 1 PET 1L COCA COLA L (1.00 €)\n" +
            " + PALMITO LU (1.42 €)\n" +
            "* Le total à payer : 2,50 €\n" +
            "* La monnaie reçue : -0,08 €\n" +
            "\n" +
            "Ces informations devraient vous aider à comprendre la situation. Si vous avez d'autres questions ou besoin de plus de détails, n'hésitez pas à me les demander !";

    FlexibleExpense expense = ticketron.createFlexibleExpenseObject(prompt);

    assertNotNull(expense);
    assertNotNull(expense.merchant);
    assertNotNull(expense.totalAmount);
    assertNotNull(expense.hour);
    assertNotNull(expense.date);
    assertNotNull(expense.description);
  }

  @Test
  void createFlexibleCategoryObject() {
    String prompt = "J'ai lu l'image que vous m'avez envoyée. Voici les informations importantes que j'ai extraites :\n" +
            "\n" +
            "* Le nom du magasin : MARKET Hauteville\n" +
            "* La date de la facture : 01/02/2016\n" +
            "* L'heure de la facture : 18:25:08\n" +
            "* Les produits achetés :\n" +
            " + 1 PET 1L COCA COLA L (1.00 €)\n" +
            " + PALMITO LU (1.42 €)\n" +
            "* Le total à payer : 2,50 €\n" +
            "* La monnaie reçue : -0,08 €\n" +
            "\n" +
            "Ces informations devraient vous aider à comprendre la situation. Si vous avez d'autres questions ou besoin de plus de détails, n'hésitez pas à me les demander !";

    FlexibleCategory category = ticketron.createFlexibleCategory(prompt);

    assertNotNull(category.description);
    assertNotNull(category.name);
    assertNotNull(category.code);
  }

  @Test
  void createExpenseObject() {
    FlexibleExpense fe = new FlexibleExpense();
    fe.merchant = "MARKET Hauteville";
    fe.totalAmount = "2,50 €";
    fe.date = "2024-10-15";
    fe.currency = "EUR";
    fe.description = "Achat de fruits";
    fe.hour = "14:30";


  }

  @Test
  void saveExpense() throws TesseractException, IOException {
    // GIVEN an image path
    String path = getClass().getResource("/factures/f1.jpg").getPath();
    String response = ticketron.analyseExpenseImg(path);
    FlexibleExpense fe = ticketron.createFlexibleExpenseObject(response);
    FlexibleCategory fc = ticketron.createFlexibleCategory(response);

    Expense expense = expenseService.fromFlexible(fe);
    Category category = expenseService.fromFlexible(fc);
    expense.category = category;
    expenseService.saveExpense(expense);
    // EXPECTED OCR service is called
  }

  @Test void getCategory() {
    FlexibleCategory fc = new FlexibleCategory();
    fc.code = "MARKET Hauteville";
    fc.name = "2,50 €";
    fc.description = "Achat de fruits";
    Category category = ticketron.getCategory(fc);
    verify(expenseService).getCategories();
    verify(expenseService, atMost(0)).fromFlexible(any(FlexibleCategory.class));
  }




}