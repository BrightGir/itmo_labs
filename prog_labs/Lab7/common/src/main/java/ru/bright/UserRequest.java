package ru.bright;

public class UserRequest extends Request {

    private User user;

    public UserRequest(User user, String commandLine, Object attachedObject) {
        super(commandLine, attachedObject);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
