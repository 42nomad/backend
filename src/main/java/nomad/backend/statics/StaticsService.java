package nomad.backend.statics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StaticsService {
    private final StaticRepository staticRepository;

    public void saveStatic(String cluster, String location, Date usedDate, int data) {
        Statics statics = new Statics(cluster, location, usedDate, data);
        staticRepository.save(statics);
    }

    public List<MeetingRoomStaticDto> getMeetingRoomStatics(Date startDate, Date endDate, int sort) {
        List<Statics> staticsList = staticRepository.findByUsedDate(startDate, endDate);

        Map<String, Map<String, Integer>> countMap = new HashMap<>();
        // cluster와 location이 일치하는 경우의 수가 몇번인지 세줌
        Map<String, Map<String, Integer>> dataSumMap = new HashMap<>();
        // cluster아 location이 일치하는 경우 그 data 즉, 누적 사용시간의 합을 구해줌

        for (Statics statics : staticsList) {
            String cluster = statics.getCluster();
            String location = statics.getLocation();
            int data = statics.getData();

            // 클러스터, 로케이션별로 그게 몇번 나오는지 나올때마다 카운팅 + 1
            countMap
                    .computeIfAbsent(cluster, k -> new HashMap<>())
                    .merge(location, 1, Integer::sum);

            // 클러스터, 로케이션별로 data 합산해서 누적사용 시간 구하기
            dataSumMap
                    .computeIfAbsent(cluster, k -> new HashMap<>())
                    .merge(location, data, Integer::sum);
        }

        List<MeetingRoomStaticDto> result = new ArrayList<>();

        Map<String, MeetingRoomStaticDto> uniqueDataMap = new HashMap<>();

        for (Statics statics : staticsList) {
            String cluster = statics.getCluster();
            String location = statics.getLocation();
            int count = countMap.get(cluster).get(location);
            int totalUsageTime = dataSumMap.get(cluster).get(location);

            String key = cluster + "-" + location;
            MeetingRoomStaticDto dto = uniqueDataMap.get(key);
            if (dto == null) {
                dto = new MeetingRoomStaticDto(cluster, location, count, totalUsageTime);
                uniqueDataMap.put(key, dto);
            } else {
                dto.setCount(dto.getCount() + count);
                dto.setTotalUsageTime(dto.getTotalUsageTime() + totalUsageTime);
            }
        }
        result.addAll(uniqueDataMap.values());

        if (sort == 1)
            result.sort(Comparator.comparingInt(MeetingRoomStaticDto::getCount).reversed());
        else if (sort == 2)
            result.sort(Comparator.comparingInt(MeetingRoomStaticDto::getTotalUsageTime).reversed());
        return result;
    }
}
