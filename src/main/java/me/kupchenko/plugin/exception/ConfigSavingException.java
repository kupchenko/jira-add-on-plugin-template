package me.kupchenko.plugin.exception;

/**
 * This exception is checked because as far as I know there is no way
 * to create some kind of class to handle all
 * application exceptions. Like ControllerAdvice in spring.
 */
public class ConfigSavingException extends Exception {
    public ConfigSavingException(String message) {
        super(message);
    }
}
