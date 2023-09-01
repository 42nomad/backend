package nomad.backend.statistics;

import lombok.RequiredArgsConstructor;
import nomad.backend.global.Define;
import nomad.backend.imac.IMac;
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
        Stat stat = new Stat(Define.STAT_MEETINGROOM, cluster, location, usedDate, data);
        statRepository.save(stat);
    }

    public List<MeetingRoomStatDto> getMeetingRoomStat(StatDao statInfo) {
        List<Stat> statList = statRepository.findByUsedDate(Define.STAT_MEETINGROOM, statInfo.getStartDate(), statInfo.getEndDate());

        Map<String, Map<String, Integer>> countMap = new LinkedHashMap<>();
        // cluster와 location이 일치하는 경우의 수가 몇번인지 세줌
        Map<String, Map<String, Integer>> dataSumMap = new LinkedHashMap<>();
        // cluster아 location이 일치하는 경우 그 data 즉, 누적 사용시간의 합을 구해줌

        for (Stat stat : statList) {
            String cluster = stat.getCluster();
            String location = stat.getLocation();
            int data = stat.getData();

            // 클러스터, 로케이션별로 그게 몇번 나오는지 나올때마다 카운팅 + 1
            // 처음 나온거 카운트 0되나..? 한 개 적게 되는지 확인 필요
            countMap
                    .computeIfAbsent(cluster, k -> new LinkedHashMap<>())
                    .merge(location, 1, Integer::sum);

            // 클러스터, 로케이션별로 data 합산해서 누적사용 시간 구하기
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
            // 두개 맵으로 나눠논 정보를 하나의 디티오로 다시 담는 과정
            // 유니크 키를 만들어서 한 클러스터-로케이션에는 하나의 정보만 생겨서 계속 누적할 수 있도록

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


    @Scheduled(cron = "0 0 * * * MON") // 매주 월요일마다 실행되는 코드 매주 월요일 기준으로 즐겨찾기 stat 저장함
    @Transactional
    public void saveStarredIMacStat() { // 이 코드 리팩토링할 사람 괌 ㅎㅎㅎㅎㅎㅎㅎㅎㅎ for문을 한번만 돌면서 처리할 수 있는 방법.. 걍 맵써서 처리하는게 낫나?
        List<Starred> starredList = starredRepository.findAllByOrderByLocationAsc();
        int type = Define.STAT_IMAC;
        Date now = new Date();
        String before = starredList.get(0).getLocation().getLocation(); // 오름차순의 첫번째 Location 저장
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

        // map을 통해 각각 Unique한 location에 정보가 저장되면 이를 순회하며 dto로 변환
        for (Map.Entry<String, Integer> entry : countMap.entrySet())
            result.add(new IMacStatDto(entry.getKey(), entry.getValue()));

        if (sort == 1)
            result.sort(Comparator.comparingInt(IMacStatDto::getCount).reversed());
        return result;
    }
}
