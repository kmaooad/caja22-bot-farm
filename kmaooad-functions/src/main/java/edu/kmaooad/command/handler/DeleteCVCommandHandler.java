package edu.kmaooad.command.handler;

import edu.kmaooad.command.Command;
import edu.kmaooad.domain.model.UserRequest;
import edu.kmaooad.domain.model.UserState;
import edu.kmaooad.service.CVService;
import edu.kmaooad.service.TelegramService;
import edu.kmaooad.service.UserStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DeleteCVCommandHandler implements CommandHandler {

    private enum DeleteCVState implements CommandState {
        WAITING_FOR_CV_DECISION(
                "DELETE_CV_WAITING_FOR_DECISION", "You want to delete your CV? (Y or N)"),
        WAITING_FOR_CV_ID(
                "DELETE_CV_WAITING_FOR_ID", "Please, enter an id of the cv which you want to delete"),
        WAITING_FOR_CV_NAME(
                "DELETE_CV_WAITING_FOR_NAME", "Please, enter your full name");

        private final String name;
        private final String message;

        DeleteCVState(String name, String message) {
            this.name = name;
            this.message = message;
        }

        @Override
        public String getName() {
            return name;
        }

        public String getMessage() {
            return message;
        }
    }

    private final TelegramService telegramService;
    private final UserStateService userStateService;
    private final CVService cvService;

    @Override
    public void handle(UserRequest userRequest) {
        final UserState userState = userStateService.getStateForUser(userRequest.getChatId());
        if (userState.isEmpty()) {
            initializeUserState(userRequest, userState);
        } else {
            performStateAction(userRequest, userState);
        }
    }

    private void performStateAction(UserRequest userRequest, UserState userState) {
        final DeleteCVState currentState = (DeleteCVState) userState.getCommandState();
        final String userInput = userRequest.getText();
        final Long chatId = userRequest.getChatId();


        switch (currentState) {
            case WAITING_FOR_CV_DECISION:
                userState.addInput("manageCompetencies", userInput);
                DeleteCVCommandHandler.DeleteCVState nextState = Objects.equals(userInput, "Y") ? DeleteCVState.WAITING_FOR_CV_NAME : DeleteCVState.WAITING_FOR_CV_ID;
                userState.setCommandState(nextState);
                telegramService.sendMessage(chatId, nextState.getMessage());
                userStateService.setStateForUser(chatId, userState);
                break;

            case WAITING_FOR_CV_ID:
                if (cvService.getCVById(userInput).isPresent()) {
                    cvService.deleteCV(userInput);
                    userState.clear();
                    userStateService.setStateForUser(chatId, userState);
                    telegramService.sendMessage(chatId, "Successfully deleted CV!");
                } else {
                    telegramService.sendMessage(
                            chatId, "CV with provided id doesn't exists! Please, provide a valid id");
                }
                break;

            case WAITING_FOR_CV_NAME:
                if (cvService.getCVByName(userInput).isPresent()) {
                    cvService.deleteCV(userInput);
                    userState.clear();
                    userStateService.setStateForUser(chatId, userState);
                    telegramService.sendMessage(chatId, "Successfully deleted CV!");
                } else {
                    telegramService.sendMessage(
                            chatId, "CV with provided full name doesn't exists! Please, provide a valid id");
                }
                break;
        }
    }

    private void initializeUserState(UserRequest userRequest, UserState userState) {
        final Long chatId = userRequest.getChatId();
        userState.setCurrentCommand(Command.DELETE_CV);
        userState.setCommandState(DeleteCVState.WAITING_FOR_CV_ID);
        userStateService.setStateForUser(chatId, userState);
        telegramService.sendMessage(chatId, DeleteCVState.WAITING_FOR_CV_ID.getMessage());
    }

    @Override
    public boolean canHandle(Command command) {
        return command.equals(Command.DELETE_CV);
    }
}

