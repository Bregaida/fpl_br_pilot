package br.com.aisweb.exception;

/**
 * Exceção customizada para erros da API AISWEB.
 */
public class ApiException extends RuntimeException {
    
    private final int status;
    private final String error;
    private final String detail;

    public ApiException(int status, String error, String detail) {
        super(error + ": " + detail);
        this.status = status;
        this.error = error;
        this.detail = detail;
    }

    public ApiException(int status, String error, String detail, Throwable cause) {
        super(error + ": " + detail, cause);
        this.status = status;
        this.error = error;
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getDetail() {
        return detail;
    }
}
