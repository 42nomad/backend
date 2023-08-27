package nomad.backend.global.exception.custom;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class BadRequestException extends RuntimeException {
    private int errorCode;

    public BadRequestException() {
        super(ResponseMsg.BAD_REQUEST);
        this.errorCode = StatusCode.BAD_REQUEST;
    }
}