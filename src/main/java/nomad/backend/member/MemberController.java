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
import nomad.backend.imac.IMacDto;
import nomad.backend.starred.StarredDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    MemberService memberService;
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //  GET 요청이 오면 member 의 intra 아이디를 반환한다.
    @Operation(summary = "Intra Id 요청", description = "회원의 IntraId를 가져온다. ",  operationId = "getIntraID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberDto.class))
            ),
    })
    @GetMapping("")
    public String getMemberName() {
        return "intra";
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
    public ResponseEntity<List<StarredDto>> getStarredList() {
        System.out.println("MemberController : getStarList");
        List<StarredDto> starreds = new ArrayList<StarredDto>();
        return new ResponseEntity<List<StarredDto>>(starreds, HttpStatus.OK);
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
    public ResponseEntity<StarredDto> registerStar(@PathVariable String location) {
        System.out.println("MemberController : registerStar " + location);
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
    @DeleteMapping("/favorite/{location}")
    public ResponseEntity<StarredDto> deleteStar(@PathVariable String location) {
        System.out.println("MemberController : deleteStar " + location);
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
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 좌석 "
                    ,content = @Content(mediaType = "application/json", schema = @Schema(implementation = IMacDto.class))
            ),
    })
    @GetMapping("/search/{location}")
    public ResponseEntity<IMacDto> getLocation(@PathVariable String location)
    {
        System.out.println("MemberController : getLocation " + location);
        IMacDto iMacDto = new IMacDto(location, FALSE, 1);
        return new ResponseEntity<IMacDto>(iMacDto, HttpStatus.OK);
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
