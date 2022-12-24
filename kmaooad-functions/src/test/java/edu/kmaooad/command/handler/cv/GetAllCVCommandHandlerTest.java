package edu.kmaooad.command.handler.cv;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class GetAllCVCommandHandlerTest {

    private GetAllCVCommandHandler getAllCVCommandHandler;

    @BeforeEach
    public void setup() {
        CVService cvService = mock(CVService.class);
        TelegramService telegramService = mock(TelegramService.class);
        getAllCVCommandHandler = new GetAllCVCommandHandler(cvService, telegramService);
    }

    @Test
    public void shouldGetAllCV(){
        UserRequest userRequest = UserRequest.builder().chatId(1L).text("/getAllCV").isCommand(true).build();
        Assertions.assertTrue(getAllCVCommandHandler.canHandle(Command.GET_ALL_CV));
        Assertions.assertDoesNotThrow(() -> getAllCVCommandHandler.handle(userRequest));
    }

}
