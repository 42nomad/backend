package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class TooManyRequestException extends RuntimeException {
    private int errorCode;

    public TooManyRequestException(String msg) {
        super(msg);
        this.errorCode = StatusCode.TOO_MANY_REQUEST;
    }
}