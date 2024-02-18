package ru.yandex.practicum.filmorate.controller.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    int status;
    String error;
    String message;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
