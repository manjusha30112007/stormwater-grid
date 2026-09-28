package com.mentorconnect.exception;

public class MentorLimitExceededException extends RuntimeException {
    public MentorLimitExceededException(String message) {
        super(message);
    }
}
