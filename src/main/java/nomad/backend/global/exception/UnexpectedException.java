package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class UnexpectedException extends RuntimeException {
    private int errorCode;

    public UnexpectedException(String msg) {
        super(msg);
        this.errorCode = StatusCode.INTERNAL_SERVER_ERROR;
    }
}