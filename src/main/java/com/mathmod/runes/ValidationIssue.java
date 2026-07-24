package com.mathmod.runes;

import java.util.Arrays;
import java.util.List;

public record ValidationIssue(
        Severity severity,
        String nodeId,
        String message,
        String messageKey,
        List<String> messageArguments
) {
    public ValidationIssue {
        if (severity == null) {
            throw new IllegalArgumentException("severity must not be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
        nodeId = nodeId == null ? "" : nodeId.trim();
        message = message.trim();
        messageKey = messageKey == null ? "" : messageKey.trim();
        messageArguments = messageArguments == null ? List.of() : List.copyOf(messageArguments);
    }

    public ValidationIssue(Severity severity, String nodeId, String message) {
        this(severity, nodeId, message, "", List.of());
    }

    public static ValidationIssue error(String nodeId, String message) {
        return new ValidationIssue(Severity.ERROR, nodeId, message);
    }

    public static ValidationIssue localizedError(
            String nodeId,
            String fallbackMessage,
            String messageKey,
            Object... messageArguments
    ) {
        return new ValidationIssue(
                Severity.ERROR,
                nodeId,
                fallbackMessage,
                messageKey,
                Arrays.stream(messageArguments).map(String::valueOf).toList()
        );
    }

    public static ValidationIssue warning(String nodeId, String message) {
        return new ValidationIssue(Severity.WARNING, nodeId, message);
    }

    public static ValidationIssue localizedWarning(
            String nodeId,
            String fallbackMessage,
            String messageKey,
            Object... messageArguments
    ) {
        return new ValidationIssue(
                Severity.WARNING,
                nodeId,
                fallbackMessage,
                messageKey,
                Arrays.stream(messageArguments).map(String::valueOf).toList()
        );
    }

    public boolean localized() {
        return !messageKey.isBlank();
    }

    public enum Severity {
        ERROR,
        WARNING
    }
}
