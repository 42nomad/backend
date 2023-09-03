package nomad.backend.iot;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nomad.backend.global.exception.NotFoundException;
import nomad.backend.global.reponse.Response;
import nomad.backend.global.reponse.ResponseMsg;
import nomad.backend.global.reponse.StatusCode;
import nomad.backend.meetingroom.MeetingRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "IoTController", description = "IoT 컨트롤러")
@RestController
@RequestMapping("/iot")
@RequiredArgsConstructor
public class IotController {
    private final MeetingRoomService meetingRoomService;
    @Operation(operationId = "iot", summary = "Iot test", description = "iot test")
    @GetMapping()
    public String getIotTest(@RequestBody IoTDto ioTDto) {
        System.out.println("get iot Test");
        System.out.println(ioTDto.getName());
        System.out.println(ioTDto.getType());
        return ioTDto.getName()+ " " + ioTDto.getType();
    }

    @Operation(operationId = "iot", summary = "iotSignalHandler", description = "IoT 기기의 신호를 받아서 해당 회의실의 상태를 바꾼다.")
    @PostMapping()
    public ResponseEntity iotSignalHandler(@RequestBody IoTDto ioTDto) {
        System.out.println("post iot Test");
        System.out.println(ioTDto.getName());
        System.out.println(ioTDto.getType());
        String[] parts = ioTDto.getName().split("-");
        if (parts.length == 2) {
            String cluster = parts[0];
            String location = parts[1].replace("_", " ");
            System.out.println("Cluster: " + cluster);
            System.out.println("Location: " + location);
            meetingRoomService.updateMeetingRoomStatus(cluster, location);
        } else {
            throw new NotFoundException();
        }
        return new ResponseEntity(Response.res(StatusCode.OK, ResponseMsg.IOT_UPDATE_SUCCESS), HttpStatus.OK);
    }
}
