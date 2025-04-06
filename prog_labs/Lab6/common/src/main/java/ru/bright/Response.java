package ru.bright;

public class Response implements java.io.Serializable {

    private ResponseStatus status;
    private String message;

    public Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }


    public ResponseStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
