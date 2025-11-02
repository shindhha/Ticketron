package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.agents.TicketronAgent;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@SpringBootTest
class PromptControllerTest {

  @MockitoBean
  private TicketronAgent ticketron;

  @Mock
  private MultipartFile file;

  @Autowired
  private PromptController controller;




  @Test
  void testHandleChat_noFile_sent() {
    when(ticketron.analyseReceiptImage(anyString())).thenReturn("test");
    ResponseEntity<?> response = controller.handleChat("",null);
    assertEquals(200, response.getStatusCodeValue());

  }

  @Test
  void testHandleChat_emptyFile_sent() {
    when(file.isEmpty()).thenReturn(true);
    when(ticketron.analyseReceiptImage(anyString())).thenReturn("test");
    ResponseEntity<?> response = controller.handleChat("", file);

    assertEquals(200, response.getStatusCodeValue());

  }

  @Test
  void testHandleChat_validFile() throws Exception {
    when(file.isEmpty()).thenReturn(false);
    when(file.getOriginalFilename()).thenReturn("f1.jpg");
    when(file.getInputStream()).thenReturn(getClass().getResourceAsStream("/factures/f1.jpg"));

    when(ticketron.analyseReceiptImage(anyString())).thenReturn("OK reçu");

    ResponseEntity<?> response = controller.handleChat("", file);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("OK reçu", ((Map<?, ?>) response.getBody()).get("text"));

    verify(ticketron, times(1)).analyseReceiptImage(anyString());
  }

  @Test
  void testHandleChat_exceptionThrown() throws Exception {
    when(file.isEmpty()).thenReturn(false);
    when(file.getOriginalFilename()).thenReturn("f1.jpg");

    // Simule une erreur lors du stockage ou du traitement
    when(file.getInputStream()).thenThrow(new RuntimeException("IO Error"));

    ResponseEntity<?> response = controller.handleChat("",file);

    assertEquals(500, response.getStatusCodeValue());
    assertTrue(((Map<?, ?>) response.getBody()).get("text").toString().contains("Erreur serveur"));

    verifyNoInteractions(ticketron);
  }
}