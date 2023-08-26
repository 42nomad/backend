package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class NotFoundException extends RuntimeException {
    private int errorCode;

    public NotFoundException() {
        super(ResponseMsg.INTERNAL_SERVER_ERROR);
        this.errorCode = StatusCode.INTERNAL_SERVER_ERROR;
    }
}