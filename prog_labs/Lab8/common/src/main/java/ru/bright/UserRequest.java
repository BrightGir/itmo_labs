package ru.bright;

import java.util.UUID;

public class UserRequest extends Request {

    private User user;
    private UUID uuid;

    public UserRequest(User user, String commandLine, Object attachedObject) {
        super(commandLine, attachedObject);
        this.user = user;
        this.uuid = UUID.randomUUID();
    }

    public User getUser() {
        return user;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
