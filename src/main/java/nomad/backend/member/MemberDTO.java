package nomad.backend.member;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDTO {

    private Long memberId;
    private String intra;
    private String refreshToken;
    private Integer home;
}


