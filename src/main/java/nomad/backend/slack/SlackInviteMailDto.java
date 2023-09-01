package nomad.backend.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SlackInviteMailDto {
    private String address;
    private String title;
    private String content;
}
