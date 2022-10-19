package edu.kmaooad.domain;

import lombok.Value;

@Value
public class AddMessageResult {

  boolean isSuccessful;
  String result;
  String errorMessage;
}
