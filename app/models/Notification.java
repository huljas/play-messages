package models;

/**
 * A notification that will be display in the UI.
 */
public class Notification {
    /**
     * The notification message.
     */
    public String message;

    /**
     * The notification type. Valid values are:
     * <ul>
     * <li>success</li>
     * <li>alert</li>
     * <li>error</li>
     * <li>basic</li>
     * </ul>
     */
    public String notificationType;
}