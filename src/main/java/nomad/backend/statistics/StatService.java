package nomad.backend.statistics;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.starred.Starred;
import nomad.backend.starred.StarredRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatService {
    private final StatRepository statRepository;
    private final StarredRepository starredRepository;


    public void saveStatic(String cluster, String location, Date usedDate, int data) {
        Stat stat = new Stat(Define.STAT_MEETING_ROOM, cluster, location, usedDate, data);
        statRepository.save(stat);
    }

    public List<MeetingRoomStatDto> getMeetingRoomStat(StatDao statInfo) {
        List<Stat> statList = statRepository.findByUsedDate(Define.STAT_MEETING_ROOM, statInfo.getStartDate(), statInfo.getEndDate());
        Map<String, Map<String, Integer>> countMap = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> dataSumMap = new LinkedHashMap<>();

        for (Stat stat : statList) {
            String cluster = stat.getCluster();
            String location = stat.getLocation();
            int data = stat.getData();

            // 클러스터, 로케이션별로 그게 몇번 나오는지 나올때마다 카운팅 + 1
            // 처음 나온거 카운트 0되나..? 한 개 적게 되는지 확인 필요
            countMap
                    .computeIfAbsent(cluster, k -> new LinkedHashMap<>())
                    .merge(location, 1, Integer::sum);

            dataSumMap
                    .computeIfAbsent(cluster, k -> new LinkedHashMap<>())
                    .merge(location, data, Integer::sum);
        }

        List<MeetingRoomStatDto> result = new ArrayList<>();
        Map<String, MeetingRoomStatDto> uniqueDataMap = new LinkedHashMap<>();

        for (Stat stat : statList) {
            String cluster = stat.getCluster();
            String location = stat.getLocation();
            int count = countMap.get(cluster).get(location);
            int totalUsageTime = dataSumMap.get(cluster).get(location);

            String key = cluster + "-" + location;
            MeetingRoomStatDto dto = uniqueDataMap.get(key);
            if (dto == null) {
                dto = new MeetingRoomStatDto(cluster, location, count, totalUsageTime);
                uniqueDataMap.put(key, dto);
            } else {
                dto.setCount(dto.getCount() + count);
                dto.setTotalUsageTime(dto.getTotalUsageTime() + totalUsageTime);
            }
        }
        result.addAll(uniqueDataMap.values());

        if (statInfo.getSort() == 1)
            result.sort(Comparator.comparingInt(MeetingRoomStatDto::getCount).reversed());
        else if (statInfo.getSort() == 2)
            result.sort(Comparator.comparingInt(MeetingRoomStatDto::getTotalUsageTime).reversed());
        return result;
    }


    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void saveStarredIMacStat() { // 이 코드 리팩토링할 사람 괌 ㅎㅎㅎㅎㅎㅎㅎㅎㅎ for문을 한번만 돌면서 처리할 수 있는 방법.. 걍 맵써서 처리하는게 낫나?
        List<Starred> starredList = starredRepository.findAllByOrderByLocationAsc();
        int type = Define.STAT_IMAC;
        Date now = new Date();
        String before = starredList.get(0).getLocation().getLocation();
        String cluster = starredList.get(0).getLocation().getCluster();
        String location = null;
        int flag = 0;
        int count = 0;
        for (Starred starred : starredList) {
            location = starred.getLocation().getLocation();
            if (location.equalsIgnoreCase(before)) // 전거랑 같으면 카운트만 증가
                count++;
            else {
                flag += count;
                statRepository.save(new Stat(type, cluster, before, now, count)); // 달라지면 현재까지의 카운트와 로케이션을 저장
                count = 1;
                before = location;
                cluster = starred.getLocation().getCluster();
            }
        }

        if (flag != starredList.size()) // 마지막이 바로 직전거와 같아서 저장하지 못하고 나온 경우에만 실행 <- 이게 젤 꼴보기 싫음
            statRepository.save(new Stat(type, cluster, location, now, count));
    }

    public List<IMacStatDto> getAllStarredIMacStat(StatDao statInfo) {
        List<Stat> statList = statRepository.findByUsedDate(Define.STAT_IMAC, statInfo.getStartDate(), statInfo.getEndDate());
        return toIMacStatDto(statList, statInfo.getSort());
    }

    public List<IMacStatDto> getStarredIMacStatByCluster(String cluster, StatDao statInfo) {
        List<Stat> statList = statRepository.findByClusterAndUsedDate(Define.STAT_IMAC, cluster, statInfo.getStartDate(), statInfo.getEndDate());
        return toIMacStatDto(statList, statInfo.getSort());
    }

    private List<IMacStatDto> toIMacStatDto(List<Stat> statList, int sort) {
        Map<String, Integer> countMap = new LinkedHashMap<>();

        for (Stat stat : statList) {
            String location = stat.getLocation();
            countMap.put(location, countMap.getOrDefault(location, 0) + stat.getData());
        }

        List<IMacStatDto> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countMap.entrySet())
            result.add(new IMacStatDto(entry.getKey(), entry.getValue()));
        if (sort == 1)
            result.sort(Comparator.comparingInt(IMacStatDto::getCount).reversed());
        return result;
    }
}
