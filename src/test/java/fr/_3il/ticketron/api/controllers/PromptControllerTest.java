package fr._3il.ticketron.api.controllers;

<<<<<<< HEAD
import fr._3il.ticketron.api.models.Prompt;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
=======
import fr._3il.ticketron.agents.TicketronAgent;
>>>>>>> LinkLLMBD
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
<<<<<<< HEAD
  @DisplayName("Should process prompt when a file is provided")
  @Disabled("Test désactivé temporairement")
  void shouldProcessPrompt_WhenFileIsProvided() throws Exception {
    // GIVEN
    MockMultipartFile file = new MockMultipartFile(
            "files",
            "ticket.jpg",
            "image/jpeg",
            "fakeimagecontent".getBytes()
    );
    Prompt prompt = new Prompt();
    prompt.files = new MockMultipartFile[]{ file };
    prompt.instructions = "C'était un repas client";
=======
  void testHandleChat_emptyFile_sent() {
    when(file.isEmpty()).thenReturn(true);
    when(ticketron.analyseReceiptImage(anyString())).thenReturn("test");
    ResponseEntity<?> response = controller.handleChat("", file);
>>>>>>> LinkLLMBD

    assertEquals(200, response.getStatusCodeValue());

  }

  @Test
<<<<<<< HEAD
  @DisplayName("Should throw IllegalArgumentException when no file is provided")
  @Disabled("Test désactivé temporairement")
  void shouldThrowException_WhenNoFileProvided() {
    // GIVEN
    Prompt prompt = new Prompt();
    prompt.files = new MockMultipartFile[0];
    prompt.instructions = "Peu importe";
=======
  void testHandleChat_validFile() throws Exception {
    when(file.isEmpty()).thenReturn(false);
    when(file.getOriginalFilename()).thenReturn("f1.jpg");
    when(file.getInputStream()).thenReturn(getClass().getResourceAsStream("/factures/f1.jpg"));
>>>>>>> LinkLLMBD

    when(ticketron.analyseReceiptImage(anyString())).thenReturn("OK reçu");

    ResponseEntity<?> response = controller.handleChat("", file);

    assertEquals(200, response.getStatusCodeValue());
    assertEquals("OK reçu", ((Map<?, ?>) response.getBody()).get("text"));

    verify(ticketron, times(1)).analyseReceiptImage(anyString());
  }

  @Test
<<<<<<< HEAD
  @DisplayName("Should process all files when multiple files are provided")
  @Disabled("Test désactivé temporairement")
  void shouldProcessAllFiles_WhenMultipleFilesProvided() throws Exception {
    // GIVEN
    MockMultipartFile file1 = new MockMultipartFile("files", "ticket1.jpg", "image/jpeg", "content1".getBytes());
    MockMultipartFile file2 = new MockMultipartFile("files", "ticket2.jpg", "image/jpeg", "content2".getBytes());
=======
  void testHandleChat_exceptionThrown() throws Exception {
    when(file.isEmpty()).thenReturn(false);
    when(file.getOriginalFilename()).thenReturn("f1.jpg");
>>>>>>> LinkLLMBD

    // Simule une erreur lors du stockage ou du traitement
    when(file.getInputStream()).thenThrow(new RuntimeException("IO Error"));

    ResponseEntity<?> response = controller.handleChat("",file);

    assertEquals(500, response.getStatusCodeValue());
    assertTrue(((Map<?, ?>) response.getBody()).get("text").toString().contains("Erreur serveur"));

<<<<<<< HEAD
  // ==============================================================
  // ✅ TEST 4 : Gestion d'erreur I/O (simulation d'une erreur de transfert)
  // ==============================================================
  @Test
  @DisplayName("Should handle IOException when file transfer fails")
  @Disabled("Test désactivé temporairement")
  void shouldHandleIOException_WhenFileTransferFails() throws Exception {
    // GIVEN
    MockMultipartFile mockFile = mock(MockMultipartFile.class);
    when(mockFile.getOriginalFilename()).thenReturn("ticket.jpg");
    doThrow(new IOException("Erreur simulée")).when(mockFile).transferTo(any(File.class));

    Prompt prompt = new Prompt();
    prompt.files = new MockMultipartFile[]{ mockFile };
    prompt.instructions = "Test";

    // WHEN + THEN
    assertThrows(IOException.class, () -> controller.processPrompt(prompt));
    verify(ticketron, never()).processReceiptWithInstruction(anyString(), anyString());
=======
    verifyNoInteractions(ticketron);
>>>>>>> LinkLLMBD
  }
}