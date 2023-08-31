package nomad.backend.iot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IoTController", description = "게시판 컨트롤러")
@RestController
@RequestMapping("/iot")
@RequiredArgsConstructor
public class IotController {
    @Operation(operationId = "iot", summary = "Iot test", description = "iot test")
    @GetMapping()
    public String getIotTest(@RequestParam("name")String roomName,  @RequestParam("type") String clickType) {
        System.out.println("get iot Test");
        System.out.println(roomName);
        System.out.println(clickType);
        return roomName+ " " + clickType;
    }

    @Operation(operationId = "iot", summary = "Iot test", description = "iot test")
    @PostMapping()
    public String postIotTest(@RequestParam("name")String roomName,  @RequestParam("type") String clickType) {
        System.out.println("get iot Test");
        System.out.println(roomName);
        System.out.println(clickType);
        return roomName+ " " + clickType;
    }
}
