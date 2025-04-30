package ru.bright;

import java.io.Serializable;

public enum ResponseStatus implements Serializable {
    OK, ERROR, DENIED, AUTH_FAILED, AUTH_PASSED
}
