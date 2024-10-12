package utility;

public enum ExitCode {
    CONFIG_ERROR(2),
    UNDEFINED_TOKEN(14),
    ;

    private final int code;

    ExitCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
