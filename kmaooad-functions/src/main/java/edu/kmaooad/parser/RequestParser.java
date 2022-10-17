package edu.kmaooad.parser;

import edu.kmaooad.exception.InvalidMessageException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {

    private final Pattern pattern = Pattern.compile("(?:\\s*\"message_id\":\\s*)([0-9]+)");

    public Integer getMessageId(String message) {
        final Matcher matcher = pattern.matcher(message);
        if(!matcher.find()) {
            throw new InvalidMessageException("Can't get message_id from request");
        }
        return Integer.parseInt(matcher.group(1));
    }
}
