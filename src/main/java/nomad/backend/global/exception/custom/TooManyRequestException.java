package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class TooManyRequestException extends RuntimeException {
    private int errorCode;

    public TooManyRequestException() {
        super(ResponseMsg.TOO_MANY_REQUEST);
        this.errorCode = StatusCode.TOO_MANY_REQUEST;
    }
}