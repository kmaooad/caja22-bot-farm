package edu.kmaooad;

import com.microsoft.azure.functions.*;
import org.junit.jupiter.api.Disabled;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class FunctionTest {

    @Test
    @Disabled
    public void testHttpTriggerJava() throws Exception {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final String requestBody = "{\n" +
                "    \"update_id\": 91242774,\n" +
                "    \"message\": {\n" +
                "        \"message_id\": 25,\n" +
                "        \"from\": {\n" +
                "            \"id\": 423154484,\n" +
                "            \"is_bot\": false,\n" +
                "            \"first_name\": \"Lisa\",\n" +
                "            \"last_name\": \"Perun\",\n" +
                "            \"username\": \"lisaperun\",\n" +
                "            \"language_code\": \"uk\"\n" +
                "        },\n" +
                "        \"chat\": {\n" +
                "            \"id\": 423154484,\n" +
                "            \"first_name\": \"Lisa\",\n" +
                "            \"last_name\": \"Perun\",\n" +
                "            \"username\": \"lisaperun\",\n" +
                "            \"type\": \"private\"\n" +
                "        },\n" +
                "        \"date\": 1664647542,\n" +
                "        \"text\": \"\\u043b\\u043e\\u043b\"\n" +
                "    }\n" +
                "}";

        final Optional<String> queryBody = Optional.of(requestBody);
        doReturn(queryBody).when(req).getBody();

        doAnswer((Answer<HttpResponseMessage.Builder>) invocation -> {
            HttpStatus status = (HttpStatus) invocation.getArguments()[0];
            return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, context);

        // Verify
        assertEquals(HttpStatus.OK, ret.getStatus());
        assertEquals("25", ret.getBody().toString());
    }
}
