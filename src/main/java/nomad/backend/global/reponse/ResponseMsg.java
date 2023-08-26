package nomad.backend.global.reponse;

public class ResponseMsg {
    // success
    public static final String POST_WRITE_SUCCESS = "게시글 작성 성공";
    public static final String POST_MODIFY_SUCCESS = "게시글 수정 성공";
    public static final String POST_DELETE_SUCCESS = "게시글 삭제 성공";
    public static final String SECRET_INSERT_SUCCESS = "시크릿 주입 성공";
    public static final String IMAC_SET_SUCCESS = "아이맥 자리 정보 정리 성공";

    // error
    public static final String BAD_REQUEST = "잘못된 요청";
    public static final String UNAUTHORIZED = "인증 실패";
    public static final String FORBIDDEN = "권한 없음";
    public static final String NOT_FOUND = "데이터를 찾을 수 없음";
    public static final String TOO_MANY_REQUEST = "Api 요청 횟수 초과";
    public static final String JSON_DESERIALIZE_FAILED = "Json 매핑 실패";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
}
