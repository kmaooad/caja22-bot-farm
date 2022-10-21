package edu.kmaooad.utils;

import com.microsoft.azure.functions.ExecutionContext;
import java.util.logging.Logger;

public class TestExecutionContext implements ExecutionContext {

  @Override
  public Logger getLogger() {
    return Logger.getLogger(TestExecutionContext.class.getName());
  }

  @Override
  public String getInvocationId() {
    return "id1";
  }

  @Override
  public String getFunctionName() {
    return "TelegramWebhook";
  }
}
