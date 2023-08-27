package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class InternalServerException extends RuntimeException {
    private int errorCode;

    public InternalServerException() {
        super(ResponseMsg.FORBIDDEN);
        this.errorCode = StatusCode.FORBIDDEN;
    }
}