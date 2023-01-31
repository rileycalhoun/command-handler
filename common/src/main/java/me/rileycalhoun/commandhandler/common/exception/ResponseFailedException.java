package me.rileycalhoun.commandhandler.common.exception;

import me.rileycalhoun.commandhandler.common.ResponseHandler;

public class ResponseFailedException extends CommandException {
    private final ResponseHandler<?> responseHandler;
    private final Object response;

    public ResponseFailedException(ResponseHandler<?> responseHandler, Object response) {
        this.responseHandler = responseHandler;
        this.response = response;
    }

    public ResponseFailedException(String message, ResponseHandler<?> responseHandler, Object response) {
        super(message);
        this.responseHandler = responseHandler;
        this.response = response;
    }

    public ResponseFailedException(String message, Throwable cause, ResponseHandler<?> responseHandler, Object response) {
        super(message, cause);
        this.responseHandler = responseHandler;
        this.response = response;
    }

    public ResponseFailedException(Throwable cause, ResponseHandler<?> responseHandler, Object response) {
        super(cause);
        this.responseHandler = responseHandler;
        this.response = response;
    }

    public Object getResponse() {
        return response;
    }

    public ResponseHandler<?> getResponseHandler() {
        return responseHandler;
    }
}
