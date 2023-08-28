package nomad.backend.slack;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import net.minidev.json.JSONObject;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackDto {
    private String ok;
    private JSONObject user;

    @Override
    public String toString() {
        return "ok = " + ok + " " + user;
    }
}
