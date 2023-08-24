package nomad.backend.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.board.PostDto;
import nomad.backend.imac.IMac;
import nomad.backend.imac.IMacDto;
import nomad.backend.imac.IMacService;
import nomad.backend.starred.Starred;
import nomad.backend.starred.StarredDto;
import nomad.backend.starred.StarredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Tag(name = "MemberController", description = "회원 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final StarredService starredService;
    private final IMacService iMacService;


    //  GET 요청이 오면 member 의 intra 아이디를 반환한다.
    @Operation(summary = "멤버 정보", description = "회원의 IntraId와 홈화면 정보를 가져온다. ",  operationId = "getIntraID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))
            ),
    })
    @GetMapping("")
    public ResponseEntity<Member> getMemberName(Authentication authentication) {
        System.out.println("MemberController : getMemberName");
        Member member = memberService.getMemberByAuth(authentication);
        return new ResponseEntity<Member>(member, HttpStatus.OK);
    }

    /*
   member 가 즐겨찾기 한 자리를 리스트 형태로 반환하는 API
   String location, String cadet(null 비어있는 자리고, logout_time을 같이 봐야 함)
   int logoutTime(-1이면 42분 지난거) - 좌석 순 정렬해서 List 반환:200
   */
    @Operation(summary = "즐겨찾기 리스트", description = "회원이 즐겨찾기한 리스트를 가져온다.",  operationId = "starredList")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = StarredDto.class)))
            ),
    })
    @GetMapping("/favorite")
    public List<StarredDto> getStarredList(Authentication authentication) {
        System.out.println("MemberController : getStarList");
        Member member = memberService.getMemberByAuth(authentication);
        List<StarredDto> starred = starredService.getMemberStarredList(member);
        return starred;
    }

    /*
     member 가 새로운 자리를 즐겨찾기 하는 API
     200 : OK
     409 : 유효하지 않은 좌석, 이미 등록된 좌석
     */
    @Operation(summary = "즐겨찾기 추가", description = "새로운 자리를 즐겨찾기 리스트에 추가한다.",  operationId = "registerStarred")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = StarredDto.class))
            ),
            @ApiResponse(responseCode = "409", description = "이미 등록된 좌석"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 좌석"),
    })
    @PostMapping("/favorite/{location}")
    public ResponseEntity<StarredDto> registerStar(@PathVariable String location, Authentication authentication) {
        System.out.println("MemberController : registerStar " + location);
        IMac iMac = iMacService.findByLocation(location);
        Member owner = memberService.getMemberByAuth(authentication);
        starredService.registerStar(owner, iMac);
        // 중복 제거 추가 필요
        return new ResponseEntity<StarredDto>(HttpStatus.OK);
    }

    /*
    member 가 새로운 자리를 즐겨찾기 한 자리를 삭제하는 API
    200 : OK
    */
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 리스트에서 하나를 삭제한다.",  operationId = "deleteStarred")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = StarredDto.class))
            ),
    })
    @DeleteMapping("/favorite/{id}")
    public ResponseEntity<StarredDto> deleteStar(@PathVariable Integer id, Authentication authentication) {
        System.out.println("MemberController : deleteStar " + id);
        starredService.deleteStar(id);
        return new ResponseEntity<StarredDto>(HttpStatus.OK);
    }

    /*
    좌석 검색 API
    String location, String cadet, int logoutTime, boolean isStar: 200 OK
    유효하지 않은 좌석: 404
    api intra 오류: 429
     */
    @Operation(summary = "자리 검색", description = "아이맥 자리를 검색한다.", operationId = "search IMac")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMac.class))
            ),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 좌석 "
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMac.class))
            ),
    })
    @GetMapping("/search/{location}")
    public ResponseEntity<IMac> getLocation(@PathVariable String location)
    {
        System.out.println("MemberController : getLocation " + location);
        IMac iMac = iMacService.findByLocation(location);
        return new ResponseEntity<IMac>(iMac, HttpStatus.OK);
    }

    /*
    member 가 최근에 앉은 자리 리스트를 반환해주는 API
   String location, String cadet, int logoutTime, String date(앉은 날짜): 200
   날짜순 정렬해서 list 형태로 반환
     */
    @Operation(summary = "자리 기록", description = "최근 앉은 5개의 자리의 리스트를 보여준다.", operationId = "IMacHistory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacDto.class))
            )
    })
    @GetMapping("/history")
    public ResponseEntity<List<IMacDto>> getHistory()
    {
        System.out.println("MemberController : getHistory" );
        List<IMacDto> IMacs = new ArrayList<IMacDto>();
        IMacs.add(new IMacDto("cluster", FALSE, 1));
        return new ResponseEntity<List<IMacDto>>(IMacs, HttpStatus.OK);
    }

    /*
    member 의 홈 화면 설정을 반환하는 API
     */
    @Operation(summary = "홈 화면", description = "사용자가 설정한 홈 화면 설정을 가져온다.", operationId = "homeSetting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))
            )
    })
    @GetMapping("/home")
    public ResponseEntity<MemberDto> getMemberHome()
    {
        System.out.println("MemberController : getMemberHome" );
        MemberDto memberDTO = new MemberDto(1L, "intra", "refreshToken", 1);
        return new ResponseEntity<MemberDto>(memberDTO, HttpStatus.OK);
    }

    /*
    * member의 홈 화면 설정을 업데이트 하는 API
    * */
    @Operation(summary = "홈 화면 변경", description = "사용자의 홈 화면 설정을 업데이트한다.", operationId = "homeSettingUpdate")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))
            )
    })
    @PostMapping("/home/{home}")
    public ResponseEntity<MemberDto> updateMemberHome(@PathVariable Integer home)
    {
        System.out.println("MemberController : updateMemberHome " + home);
        MemberDto memberDTO = new MemberDto(1L, "intra", "refreshToken", home);
        return new ResponseEntity<MemberDto>(memberDTO, HttpStatus.OK);
    }

    /*
    member 자신의 쓴 글 리스트를 반환하는  API
    int postId, String location, String imgUrl, String date
     */
    @Operation(summary = "내가 쓴 글", description = "사용자가 분실물 게시판에 글 들을 가져온다.", operationId = "MemberPostList")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))
            )
    })
    @GetMapping("/post")
    public ResponseEntity<List<PostDto>> getMemberPosts()
    {
        System.out.println("MemberController : getMemberPosts" );
        PostDto postDto = new PostDto(1L, "jonkim", "location", "contents", "imgUrl", "data", TRUE);
        List<PostDto> postDtoList = new ArrayList<PostDto>();
        postDtoList.add(postDto);
        return new ResponseEntity<List<PostDto>>(postDtoList, HttpStatus.OK);
    }

}
