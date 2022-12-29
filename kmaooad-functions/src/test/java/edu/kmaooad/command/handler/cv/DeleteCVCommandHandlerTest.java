package edu.kmaooad.command.handler.cv;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import edu.kmaooad.command.dispatch.Command;
import edu.kmaooad.domain.model.CV;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.*;
import edu.kmaooad.web.request.UserRequest;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeleteCVCommandHandlerTest {

  private CVService cvService;
  private TelegramService telegramService;
  private UserStateService userStateService;
  private DeleteCVCommandHandler deleteCVCommandHandler;

  @BeforeEach
  public void setup() {
    cvService = mock(CVService.class);
    telegramService = mock(TelegramService.class);
    userStateService = new UserStateServiceImpl();
    deleteCVCommandHandler =
        new DeleteCVCommandHandler(telegramService, userStateService, cvService);
  }

  @Test
  public void shouldHandleDeleteCVCommand() {
    Assertions.assertTrue(deleteCVCommandHandler.canHandle(Command.DELETE_CV));
  }

  @Test
  public void shouldInitializeStateIfCurrentStateIsEmpty() {
    Long chatId = 1L;
    userStateService.setStateForUser(chatId, UserState.newEmptyState(chatId));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("/deleteCV").isCommand(true).build();

    deleteCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.DELETE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_DECISION);

    verify(telegramService, times(1))
        .sendMessage(
            eq(chatId),
            eq(DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_DECISION.getMessage()));
  }

  @Test
  public void shouldWaitForDeleteCVDecisionAndMoveToDeletingByNameIfNameOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("Y").isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.DELETE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_NAME);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForDeleteCVDecisionAndMoveToDeletingByIdIfIdOptionSpecified() {
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_DECISION,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text("N").isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertEquals(actual.getCurrentCommand(), Command.DELETE_CV);
    Assertions.assertEquals(
        actual.getCommandState(), DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_ID);

    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVNameAndDeleteCVIfCVExists() {
    String cvId = "cv_id";
    String cvName = "some_name";
    when(cvService.getCVByName(anyString())).thenReturn(Optional.of(CV.builder().id(cvId).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, times(1)).deleteCV(eq(cvId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVNameAndSendWarningMessageIfCVDoesNotExist() {
    String cvName = "some_name";
    when(cvService.getCVByName(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_NAME,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvName).isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVByName(eq(cvName));
    verify(cvService, never()).deleteCV(anyString());
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVIdAndDeleteCVIfCVExists() {
    String cvId = "cv_id";
    when(cvService.getCVById(anyString())).thenReturn(Optional.of(CV.builder().id(cvId).build()));
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    UserState actual = userStateService.getStateForUser(chatId);
    Assertions.assertTrue(actual.isEmpty());

    verify(cvService, times(1)).getCVById(eq(cvId));
    verify(cvService, times(1)).deleteCV(eq(cvId));
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }

  @Test
  public void shouldWaitForCVIdAndSendWarningMessageIfCVDoesNotExist() {
    String cvId = "cv_id";
    when(cvService.getCVById(anyString())).thenReturn(Optional.empty());
    Long chatId = 1L;
    userStateService.setStateForUser(
        chatId,
        new UserState(
            chatId,
            Command.DELETE_CV,
            DeleteCVCommandHandler.DeleteCVState.WAITING_FOR_CV_ID,
            new HashMap<>()));
    UserRequest userRequest =
        UserRequest.builder().chatId(chatId).text(cvId).isCommand(false).build();

    deleteCVCommandHandler.handle(userRequest);

    verify(cvService, times(1)).getCVById(eq(cvId));
    verify(cvService, never()).deleteCV(anyString());
    verify(telegramService, times(1)).sendMessage(eq(chatId), anyString());
  }
}
