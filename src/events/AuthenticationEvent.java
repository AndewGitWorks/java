package events;

/**
 * Событие аутентификации пользователя
 */
public class AuthenticationEvent extends ATMEvent {
    public enum AuthType {
        LOGIN,      // Вход в систему
        LOGOUT,     // Выход из системы
        PIN_CHANGE, // Смена ПИН-кода
        SESSION_EXPIRED // Сеанс истек
    }

    private String userId;
    private AuthType authType;
    private boolean success;
    private String reason;

    public AuthenticationEvent(String userId, AuthType authType, boolean success, String reason) {
        super("AUTHENTICATION", "User authentication - " + authType);
        this.userId = userId;
        this.authType = authType;
        this.success = success;
        this.reason = reason;
    }

    public String getUserId() { return userId; }
    public AuthType getAuthType() { return authType; }
    public boolean isSuccess() { return success; }
    public String getReason() { return reason; }

    /**
     * Вложенный класс для обработки деталей аутентификации
     */
    public static class AuthDetails {
        private final String cardNumber;
        private final String sessionId;
        private final long sessionDuration;

        public AuthDetails(String cardNumber, String sessionId, long sessionDuration) {
            this.cardNumber = cardNumber;
            this.sessionId = sessionId;
            this.sessionDuration = sessionDuration;
        }

        public String getCardNumber() { return cardNumber; }
        public String getSessionId() { return sessionId; }
        public long getSessionDuration() { return sessionDuration; }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Type: %s | Success: %s | Reason: %s",
                authType, success, reason);
    }
}
