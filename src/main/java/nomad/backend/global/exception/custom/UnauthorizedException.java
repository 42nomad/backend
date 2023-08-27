package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class UnauthorizedException extends RuntimeException {
    private int errorCode;

    public UnauthorizedException() {
        super(ResponseMsg.UNAUTHORIZED);
        this.errorCode = StatusCode.UNAUTHORIZED;
    }
}