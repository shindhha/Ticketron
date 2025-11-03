package fr._3il.ticketron.api.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr._3il.ticketron.api.models.requests.ExpenseCandidate;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for converting JSON to/from FlexibleExpense.
 */
@Service
public class ExpenseParser {

  private final ObjectMapper mapper;

  public ExpenseParser() {
    mapper = new ObjectMapper()
            // ignore unknown fields to avoid LLM hallucination errors
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Converts a JSON string to a FlexibleExpense object.
   *
   * @param toParse description of an expense
   * @return FlexibleExpense instance
   */
  public ExpenseCandidate parseExpense(String toParse) {
    Pattern patternCode = Pattern.compile("<code[^>]*>([\\s\\S]*?)</code>", Pattern.CASE_INSENSITIVE);
    Pattern patternDesc = Pattern.compile("<sum[^>]*>([\\s\\S]*?)</sum>", Pattern.CASE_INSENSITIVE);

    Matcher mCode = patternCode.matcher(toParse);
    Matcher mDesc = patternDesc.matcher(toParse);

    String code = "UNKNOW";
    String desc = "UNKNOW";

    if (mCode.find()) {
      String g = mCode.group(1).trim();
      if (!g.isEmpty()) code = g;
    }

    if (mDesc.find()) {
      String g = mDesc.group(1).trim();
      if (!g.isEmpty()) desc = g;
    }

    ExpenseCandidate expense = new ExpenseCandidate();
    expense.summary = toParse;
    expense.categoryCode = code;
    expense.categoryDescription = desc;

    System.out.println("SOUT Description : " + toParse);
    return expense;
  }





}
