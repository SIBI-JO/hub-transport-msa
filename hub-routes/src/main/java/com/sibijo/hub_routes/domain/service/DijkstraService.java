package com.sibijo.hub_routes.domain.service;

import com.sibijo.common.exception.CustomException;
import com.sibijo.hub_routes.application.dto.HubRoutesCommand;
import com.sibijo.hub_routes.application.dto.RouteCoordRequestDto;
import com.sibijo.hub_routes.application.dto.RouteTimeResponseDto;
import com.sibijo.hub_routes.domain.exception.HubRoutesDomainExceptionCode;
import com.sibijo.hub_routes.infrastructure.dto.BestRouteResponseDto;
import com.sibijo.hub_routes.infrastructure.dto.HubDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DijkstraService {
    private final HubRoutesKakaoMapService hubRoutesKakaoMapService;

    //출발, 도착, 허브 전체 데이터 받고 카카오맵 api를 통해 총거리, 총시간 비용 구함
    public BestRouteResponseDto findShortestPath(HubRoutesCommand hubRoutesCommand) {
        log.info("findShortestPath req={}", hubRoutesCommand);

        //허브 노드 그래프 만들기
        Map<String, List<HubDto>> hubListGraph = createHubGraph(hubRoutesCommand.hubDtoList());

        //허브 출발, 도착 선정 -> 그래프의 모든 경우의 경로 생성 -> 카카오맵 서비스에 넘기기
        String departureHubName = getHubNameById(hubRoutesCommand.departure(), hubRoutesCommand.hubDtoList());
        String destinationHubName = getHubNameById(hubRoutesCommand.destination(), hubRoutesCommand.hubDtoList());

        if (!hubListGraph.containsKey(departureHubName) || !hubListGraph.containsKey(destinationHubName)) {
            throw new CustomException(HubRoutesDomainExceptionCode.INVALID_HUB_NAME_FOR_HUB_ROUTES);
        }
        //그래프의 모든 경로 생성(DFS)
        List<List<String>> allPath = findAllPath(hubListGraph, departureHubName, destinationHubName, new ArrayList<>(), new ArrayList<>());
        Map<Integer, List<String>> matchPathMap = new HashMap<>();
        for (int i = 0; i < allPath.size(); i++) {
            matchPathMap.put(i, allPath.get(i));
        }

        //카카오맵 api 요청 dto로 변환
        Map<Integer, RouteCoordRequestDto> routeCoordRequestDtoMap = convertHubNamesToLocations(matchPathMap, hubRoutesCommand.hubDtoList());

        //카카오 응답 가져옴
        Map<Integer, RouteTimeResponseDto> routeTimeResponseDtoMap = getBestRoutes(routeCoordRequestDtoMap);

        //가장 작은 비용 선택
        BigDecimal bestRouteDistance = BigDecimal.valueOf(Long.MAX_VALUE);
        int bestRouteTime = Integer.MAX_VALUE;
        RouteTimeResponseDto bestRouteTimeResponseDto = null;
        Integer bestRouteKey = null;

        for (Map.Entry<Integer, RouteTimeResponseDto> entry : routeTimeResponseDtoMap.entrySet()) {
            Integer routeIndex = entry.getKey();
            RouteTimeResponseDto routeTimeResponseDto = entry.getValue();

            // 거리와 시간 추출
            BigDecimal currentDistance = routeTimeResponseDto.getRoutes().get(0).getSummary().getDistanceToKm();
            int currentTime = routeTimeResponseDto.getRoutes().get(0).getSummary().getDurationToMinutes();

            // 최단 거리 및 시간 비교
            if (currentDistance.compareTo(bestRouteDistance) < 0) {
                bestRouteDistance = currentDistance;
                bestRouteTime = currentTime;
                bestRouteTimeResponseDto = routeTimeResponseDto;
                bestRouteKey = routeIndex; // 최단 경로의 키 저장
            } else if (currentDistance.compareTo(bestRouteDistance) == 0) {
                // 거리 동일하면 시간으로 비교
                if (currentTime < bestRouteTime) {
                    bestRouteTime = currentTime;
                    bestRouteTimeResponseDto = routeTimeResponseDto;
                    bestRouteKey = routeIndex; // 최단 경로의 키 저장
                }
            }
        }
        log.info("bestRouteTimeResponseDto={}", bestRouteTimeResponseDto);
        //경로 최종 선택
        List<String> bestPath = matchPathMap.get(bestRouteKey);
        Map<Integer, List<String>> beatRouteMatchMap = new HashMap<>();
        log.info("bestPath={}", bestPath);
        for (int i = 0; i < bestPath.size(); i++) {
            beatRouteMatchMap.put(i, bestPath);
        }
        BestRouteResponseDto bestRouteResponseDto = BestRouteResponseDto.builder()
                .bestPathMap(beatRouteMatchMap)
                .bestRouteDistance(bestRouteDistance)
                .bestRouteTime(bestRouteTime)
                .departureHubId(hubRoutesCommand.departure())
                .destinationHubId(hubRoutesCommand.destination())
                .build();
        return bestRouteResponseDto;
    }

    private Map<Integer, RouteTimeResponseDto> getBestRoutes(Map<Integer, RouteCoordRequestDto> routeCoordRequestDtoMap) {
        Map<Integer, RouteTimeResponseDto> result = new HashMap<>();

        for (Map.Entry<Integer, RouteCoordRequestDto> entry : routeCoordRequestDtoMap.entrySet()) {
            Integer routeIndex = entry.getKey();
            RouteCoordRequestDto routeCoordRequestDto = entry.getValue();

            //카카오맵 호출
            RouteTimeResponseDto routeTimeResponseDto = hubRoutesKakaoMapService.getDirections(routeCoordRequestDto);

            if (routeTimeResponseDto != null) {
                result.put(routeIndex, routeTimeResponseDto);
            }
        }
        return result;
    }

    private Map<Integer, RouteCoordRequestDto> convertHubNamesToLocations(Map<Integer, List<String>> matchPathMap, List<HubDto> hubDtoList) {
        Map<Integer, RouteCoordRequestDto> result = new HashMap<>();
        for (Map.Entry<Integer, List<String>> entry : matchPathMap.entrySet()) {
            Integer pathIndex = entry.getKey();
            List<String> path = entry.getValue();

            //출발, 도착 설정
            String departureHubName = path.get(0);
            String destinationHubName = path.get(path.size() - 1);

            //출발, 도착 hub
            HubDto departureHubDto = getHubByName(departureHubName, hubDtoList);
            HubDto destinationHubDto = getHubByName(destinationHubName, hubDtoList);

            if (departureHubDto == null && destinationHubDto == null) continue;

            //경유지 허브 구하기
            List<RouteCoordRequestDto.WayPoint> wayPoints = new ArrayList<>();
            for (int i = 1; i < path.size() - 1; ++i) {
                HubDto hubDto = getHubByName(path.get(i), hubDtoList);
                if (hubDto != null) {
                    wayPoints.add(RouteCoordRequestDto.WayPoint.builder()
                            .x(String.valueOf(hubDto.getLongitude()))
                            .y(String.valueOf(hubDto.getLatitude()))
                            .build());
                }
            }
            //출발, 경유지, 도착 dto로 변환
            RouteCoordRequestDto routeCoordRequestDto = RouteCoordRequestDto.builder()
                    .departure(RouteCoordRequestDto.Location.builder()
                            .x(String.valueOf(departureHubDto.getLongitude()))
                            .y(String.valueOf(departureHubDto.getLatitude()))
                            .angle(0)
                            .build())
                    .destination(RouteCoordRequestDto.Location.builder()
                            .x(String.valueOf(destinationHubDto.getLongitude()))
                            .y(String.valueOf(destinationHubDto.getLatitude()))
                            .build())
                    .wayPoints(wayPoints.isEmpty() ? List.of() : wayPoints)
                    .build();
            result.put(pathIndex, routeCoordRequestDto);
        }
        return result;
    }

    private HubDto getHubByName(String hubName, List<HubDto> hubDtoList) {
        for (HubDto hubDto : hubDtoList) {
            if (hubDto.getHubName().equals(hubName)) {
                return hubDto;
            }
        }
        return null;
    }

    private Map<String, List<HubDto>> createHubGraph(List<HubDto> hubDtoList) {

        //허브 관계는 요구사항 내용 하드 코딩 적용
        Map<String, List<String>> hubConnections = new HashMap<>();
        hubConnections.put("경기 남부 센터", Arrays.asList("경기 북부 센터", "서울특별시 센터", "인천광역시 센터", "강원특별자치도 센터", "경상북도 센터", "대전광역시 센터", "대구광역시 센터"));
        hubConnections.put("대전광역시 센터", Arrays.asList("충청남도 센터", "충청북도 센터", "세종특별자치시 센터", "전라북도 센터", "광주광역시 센터", "전라남도 센터", "경기 남부 센터", "대구광역시 센터"));
        hubConnections.put("대구광역시 센터", Arrays.asList("경상북도 센터", "경상남도 센터", "부산광역시 센터", "울산광역시 센터", "경상북도 센터", "경기 남부 센터", "대전광역시 센터"));
        hubConnections.put("경상북도 센터", Arrays.asList("경기 남부 센터", "대구광역시 센터"));
        //반대 방향도 만들어 주기
        Map<String, List<String>> biHubConnections = createBiHubGraph(hubConnections);

        //허브 테이블의 데이터만으로 그래프 만듬
        Map<String, List<HubDto>> graph = new HashMap<>();

        //허브 테이블의 데이터와 요구사항 관계와 비교하기 위해 허브이름 Map으로 변환
        Map<String, HubDto> hubDtoMap = new HashMap<>();
        for (HubDto hubDto : hubDtoList) {
            hubDtoMap.put(hubDto.getHubName(), hubDto);
        }

        //허브 테이블 데이터를 기준으로 그래프 생성
        for (Map.Entry<String, List<String>> entry : biHubConnections.entrySet()) {
            String hubName = entry.getKey();
            if (hubDtoMap.containsKey(hubName)) {
                List<String> connectedHubs = entry.getValue();
                List<HubDto> connectedHubDtoList = new ArrayList<>();

                for (String connectedHub : connectedHubs) {
                    if (hubDtoMap.containsKey(connectedHub)) {
                        connectedHubDtoList.add(hubDtoMap.get(connectedHub));
                    }
                }

                if (!connectedHubDtoList.isEmpty()) {
                    graph.put(hubName, connectedHubDtoList);
                }
            }
        }
        log.info("graph={}", graph);

        return graph;
    }

    private Map<String, List<String>> createBiHubGraph(Map<String, List<String>> hubConnections) {
        Map<String, List<String>> biHubConnections = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : hubConnections.entrySet()) {
            String hubName = entry.getKey();
            List<String> connectedHubs = entry.getValue();

            biHubConnections.putIfAbsent(hubName, new ArrayList<>());
            for (String connectedHub : connectedHubs) {
                if (!biHubConnections.get(hubName).contains(connectedHub)) {
                    biHubConnections.get(hubName).add(connectedHub);
                }

                //반대 방향 추가
                biHubConnections.putIfAbsent(connectedHub, new ArrayList<>());
                if (!biHubConnections.get(connectedHub).contains(hubName)) {
                    biHubConnections.get(connectedHub).add(hubName);
                }
            }
        }
        return biHubConnections;
    }

    private List<String> findDijkstraPath(Map<String, List<HubDto>> hubListGraph, String departureHubName, String destinationHubName) {
        return null;
    }


    private List<List<String>> findAllPath(
            Map<String, List<HubDto>> hubListGraph,
            String departureHub,
            String destinationHub,
            List<String> path,
            List<List<String>> allPath) {

        //출발점
        path.add(departureHub);

        //도착할때 경로 allPath에 추가
        if (departureHub.equals(destinationHub)) {
            allPath.add(new ArrayList<>(path));
        } else {
            if (hubListGraph.containsKey(departureHub)) {
                for (HubDto neighborHub : hubListGraph.get(departureHub)) {
                    String neighborHubName = neighborHub.getHubName();
                    if (!path.contains(neighborHubName)) {
                        //재귀로 계속 탐색
                        findAllPath(hubListGraph, neighborHubName, destinationHub, path, allPath);
                    }
                }
            }
        }
        path.remove(path.size() - 1);
        return allPath;
    }

    private String getHubNameById(UUID hubId, List<HubDto> hubDtoList) {
        for (HubDto hubDto : hubDtoList) {
            if (hubDto.getHubId().equals(hubId)) {
                return hubDto.getHubName();
            }
        }
        throw new CustomException(HubRoutesDomainExceptionCode.HUB_NOT_FOUND);
    }
}
