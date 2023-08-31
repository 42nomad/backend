package nomad.backend.iot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.board.WriteDto;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IoTController", description = "게시판 컨트롤러")
@RestController
@RequestMapping("/iot")
@RequiredArgsConstructor
public class IotController {
    @Operation(operationId = "iot", summary = "Iot test", description = "iot test")
    @GetMapping()
    public String getIotTest(@RequestBody IoTDto ioTDto) {
        System.out.println("get iot Test");
        System.out.println(ioTDto.getName());
        System.out.println(ioTDto.getType());
        return ioTDto.getName()+ " " + ioTDto.getType();
    }

    @Operation(operationId = "iot", summary = "Iot test", description = "iot test")
    @PostMapping()
    public String postIotTest(@RequestBody IoTDto ioTDto) {
        System.out.println("get iot Test");
        System.out.println(ioTDto.getName());
        System.out.println(ioTDto.getType());
        return ioTDto.getName()+ " " + ioTDto.getType();
    }
}
