package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class InternalServerException extends RuntimeException {
    private int errorCode;

    public InternalServerException(String msg) {
        super(msg);
        this.errorCode = StatusCode.INTERNAL_SERVER_ERROR;
    }
}