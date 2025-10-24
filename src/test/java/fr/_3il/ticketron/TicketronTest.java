package fr._3il.ticketron;

import fr._3il.ticketron.ocr.OcrService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TicketronTest {

  Ticketron ticketron;
  @MockitoSpyBean
  OcrService ocrService;
  public TicketronTest(@Autowired Ticketron ticketron) {
    this.ticketron = ticketron;
  }

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    System.setProperty("logging.level.dev.langchain4j", "DEBUG");
    System.setProperty("logging.level.fr._3il.ticketron", "DEBUG");

  }
  @Test
  void initContext() {
  }

  @Test
  void processReceipt() {
  }

  @Test
  void classifyExpense() {
  }

  @Test
  void validateExpense() throws Exception {
    String path = "D:\\Travail\\Projets\\ProgPro\\ticketron\\src\\main\\resources\\factures\\f1.jpg";
    assertNotEquals("",ocrService.runFile(path));
  }

  @Test
  void chat() throws Exception {
    String path = "D:\\Travail\\Projets\\ProgPro\\ticketron\\src\\main\\resources\\factures\\f1.jpg";
    String response = ticketron.processReceipt(path);
    verify(ocrService).runFile(path);
  }


}