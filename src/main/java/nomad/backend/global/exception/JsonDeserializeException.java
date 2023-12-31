package nomad.backend.global.exception;

import lombok.Getter;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;

@Getter
public class JsonDeserializeException extends RuntimeException {
    private int errorCode;

    public JsonDeserializeException() {
        super(ResponseMsg.JSON_DESERIALIZE_FAILED);
        this.errorCode = StatusCode.INTERNAL_SERVER_ERROR;
    }
}