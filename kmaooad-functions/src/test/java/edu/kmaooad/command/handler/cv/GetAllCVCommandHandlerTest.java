package edu.kmaooad.command.handler.cv;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.web.request.UserRequest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GetAllCVCommandHandlerTest {

  private CVService cvService;
  private TelegramService telegramService;
  private GetAllCVCommandHandler getAllCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    getAllCVCommandHandler = new GetAllCVCommandHandler(cvService, telegramService);
  }

  @Test
  public void shouldHandleGetAllCVCommand() {
    Assertions.assertTrue(getAllCVCommandHandler.canHandle(Command.GET_ALL_CV));
  }

  @Test
  public void shouldGetAllCvs() {
    when(cvService.getAllCVs()).thenReturn(List.of(CV.builder().build()));

    Long chatId = 1L;
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/getAllCV").isCommand(true).build();

    getAllCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getAllCVs();
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
