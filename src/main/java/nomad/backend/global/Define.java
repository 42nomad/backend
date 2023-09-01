package nomad.backend.global;

public class Define {
    public static final String INTRA_VERSION_PATH = "/v2";
    public static final String SEOUL = "29";
    public static final String SECRET_ID = "secret";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String SLACK_PATH = "path";

    public static final String SLACK_INVITE_URL = "아래 링크를 통해 42Seoul 계정으로([본인 Intra]@student.42seoul.kr) 워크스페이스에 참가해 주세요.\n42Seoul 계정이 아니라면 알림을 받을 수 없습니다.\n초대링크 : https://join.slack.com/t/42nomad/shared_invite/";

    public static final Integer ADMIN = 2;
    public static final Integer STAFF = 1;
    public static final Integer USER = 0;

    public static final String EMPTY_SEAT = " 좌석이 현재 사용 가능합니다.";
    public static final String TAKEN_SEAT = " 좌석이 현재 사용 불가 좌석으로 변경되었습니다.";
    public static final String EMPTY_ROOM = "(이)가 현재 사용 가능합니다.";
    public static final String TAKEN_ROOM = "(을)를 현재 다른 카뎃이 사용하기 시작했습니다.";
    public static final String LOST_AND_FOUND = ", 혹시 당신의 짐일 수도..? 분실물 게시판을 확인해주세요.\n\"https://42nomad.kr/lost/\"";

}
