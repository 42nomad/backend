package nomad.backend.member;

import nomad.backend.imac.IMac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired MemberService memberService;
    //  GET 요청이 오면 member 의 intra 아이디를 반환한다.
    @GetMapping("")
    public String getMemberName() {
        return "intra";
    }

    /*
   member 가 즐겨찾기 한 자리를 리스트 형태로 반환하는 API
   String location, String cadet(null 비어있는 자리고, logout_time을 같이 봐야 함)
   int logoutTime(-1이면 42분 지난거) - 좌석 순 정렬해서 List 반환:200
   */
    @GetMapping("/star")
    public void getStarList() {

        return;
    }

    /*
     member 가 새로운 자리를 즐겨찾기 하는 API
     200 : OK
     409 : 유효하지 않은 좌석, 이미 등록된 좌석
     */
    @PostMapping("/star")
    public void registerStar(@RequestParam(name = "location") String location) {


        return;
    }

    /*
    member 가 새로운 자리를 즐겨찾기 한 자리를 삭제하는 API
    200 : OK
    */
    @DeleteMapping("/star")
    public void deleteStar(@RequestParam(name = "location") String location) {

        return;
    }

    /*
    좌석 검색 API
    String location, String cadet, int logoutTime, boolean isStar: 200 OK
    유효하지 않은 좌석: 409
    api intra 오류: 429
     */
    @GetMapping("/search")
    public void getLocation(@RequestParam(name = "location") String location)
    {

    }

    /*
    member 가 최근에 앉은 자리 리스트를 반환해주는 API
   String location, String cadet, int logoutTime, String date(앉은 날짜): 200
   날짜순 정렬해서 list 형태로 반환
     */
    @GetMapping("/history")
    public void getHistory(@RequestParam(name = "location") String location)
    {

    }

    /*
    member 의 홈 화면 설정을 반환하는 API
     */
    @GetMapping("/home")
    public void getHome()
    {

    }

    /*
    * member의 홈 화면 설정을 업데이트 하는 API
    * */
    @PostMapping("/home")
    public void updateHome(@RequestParam(name = "home")Integer home)
    {

    }

    /*
    member 자신의 쓴 글 리스트를 반환하는  API
    int postId, String location, String imgUrl, String date
     */
    @GetMapping("/post")
    public void getPosts()
    {

    }

}
