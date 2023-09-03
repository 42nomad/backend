package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class UnauthorizedException extends RuntimeException {
    private int errorCode;

    public UnauthorizedException(String msg) {
        super(msg);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}