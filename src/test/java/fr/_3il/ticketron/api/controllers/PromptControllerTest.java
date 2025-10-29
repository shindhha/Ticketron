package fr._3il.ticketron.api.controllers;

import fr._3il.ticketron.agents.Ticketron;
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


}