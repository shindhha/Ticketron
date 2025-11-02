package fr._3il.ticketron.agents;

import fr._3il.ticketron.api.repositories.ExpenseRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TicketronTest {
  @Autowired
  TicketronAgent ticketron;

  @MockitoSpyBean
  ExpenseRepository expenseRepository;






  @Test @Disabled("Manual test") void analyseExpenseImg() {
    String path = getClass().getResource("/factures/f1.jpg").getPath();
    String instruction = "I want you to save this expense on the database, the image is located at : " + path;
    String response = ticketron.analyseReceiptImage(instruction);
    verify(expenseRepository).save(any());
  }














}