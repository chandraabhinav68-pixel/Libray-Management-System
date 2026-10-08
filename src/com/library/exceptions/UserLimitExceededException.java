package com.library.exceptions;

public class UserLimitExceededException extends LibraryException {
    public UserLimitExceededException(String userId, int maxLimit) {
        super("User '" + userId + "' has reached the maximum borrowing quota limit of " + maxLimit + " items.");
    }
}
