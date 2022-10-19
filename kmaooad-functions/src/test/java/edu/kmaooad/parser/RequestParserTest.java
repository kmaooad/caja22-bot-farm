package edu.kmaooad.parser;

import edu.kmaooad.exception.InvalidMessageException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RequestParserTest {

  private final RequestParser requestParser = new RequestParser();

  @Test
  public void shouldGetMessageIdFromValidMessage() {
    final String message =
        "{\n"
            + "    \"update_id\": 91242774,\n"
            + "    \"message\": {\n"
            + "        \"message_id\": 25,\n"
            + "        \"from\": {\n"
            + "            \"id\": 423154484,\n"
            + "            \"is_bot\": false,\n"
            + "            \"first_name\": \"Lisa\",\n"
            + "            \"last_name\": \"Perun\",\n"
            + "            \"username\": \"lisaperun\",\n"
            + "            \"language_code\": \"uk\"\n"
            + "        },\n"
            + "        \"chat\": {\n"
            + "            \"id\": 423154484,\n"
            + "            \"first_name\": \"Lisa\",\n"
            + "            \"last_name\": \"Perun\",\n"
            + "            \"username\": \"lisaperun\",\n"
            + "            \"type\": \"private\"\n"
            + "        },\n"
            + "        \"date\": 1664647542,\n"
            + "        \"text\": \"\\u043b\\u043e\\u043b\"\n"
            + "    }\n"
            + "}";

    Integer expected = 25;
    Integer actual = requestParser.getMessageId(message);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  public void shouldThrowExceptionIfMessageIsInvalid() {
    final String message = "{ \"update_id\": 91242774 }";

    Assertions.assertThrows(
        InvalidMessageException.class, () -> requestParser.getMessageId(message));
  }
}
