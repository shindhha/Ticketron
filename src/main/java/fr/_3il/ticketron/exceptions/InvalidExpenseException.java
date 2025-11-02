package fr._3il.ticketron.exceptions;

public class InvalidExpenseException extends RuntimeException {

  public InvalidExpenseException(String errorMessage) {
    super(errorMessage);
  }
}
