package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.Ticketron;
import fr._3il.ticketron.api.models.Prompt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PromptControllerTest {

  @Mock
  private Ticketron ticketron;

  @InjectMocks
  private PromptController controller;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // ==============================================================
  // ✅ TEST 1 : Cas nominal - un fichier est envoyé
  // ==============================================================
  @Test
  @DisplayName("Should process prompt when a file is provided")
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

    // WHEN
    String response = controller.processPrompt(prompt);

    // THEN
    assertNotNull(response);
    assertTrue(response.contains("Tickets reçus"));
    verify(ticketron, times(1)).processReceiptWithInstruction(anyString(), eq("C'était un repas client"));

    // Vérifie qu'un fichier temporaire a bien été créé
    Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "ticketron_uploads");
    assertTrue(Files.exists(tmpDir));
  }

  // ==============================================================
  // ❌ TEST 2 : Aucun fichier envoyé
  // ==============================================================
  @Test
  @DisplayName("Should throw IllegalArgumentException when no file is provided")
  void shouldThrowException_WhenNoFileProvided() {
    // GIVEN
    Prompt prompt = new Prompt();
    prompt.files = new MockMultipartFile[0];
    prompt.instructions = "Peu importe";

    // WHEN + THEN
    assertThrows(IllegalArgumentException.class, () -> controller.processPrompt(prompt));

    verify(ticketron, never()).processReceiptWithInstruction(anyString(), anyString());
  }

  // ==============================================================
  // ✅ TEST 3 : Plusieurs fichiers
  // ==============================================================
  @Test
  @DisplayName("Should process all files when multiple files are provided")
  void shouldProcessAllFiles_WhenMultipleFilesProvided() throws Exception {
    // GIVEN
    MockMultipartFile file1 = new MockMultipartFile("files", "ticket1.jpg", "image/jpeg", "content1".getBytes());
    MockMultipartFile file2 = new MockMultipartFile("files", "ticket2.jpg", "image/jpeg", "content2".getBytes());

    Prompt prompt = new Prompt();
    prompt.files = new MockMultipartFile[]{ file1, file2 };
    prompt.instructions = "Frais de déplacement";

    // WHEN
    String response = controller.processPrompt(prompt);

    // THEN
    assertTrue(response.contains("Tickets reçus"));
    verify(ticketron, times(2)).processReceiptWithInstruction(anyString(), eq("Frais de déplacement"));
  }

  // ==============================================================
  // ✅ TEST 4 : Gestion d'erreur I/O (simulation d'une erreur de transfert)
  // ==============================================================
  @Test
  @DisplayName("Should handle IOException when file transfer fails")
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
  }
}