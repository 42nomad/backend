package nomad.backend.global.api.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cluster { // cluster말고 더 좋은 이름은 없을까
    String end_at;
    String begin_at;
    String host;
    User user;
}
