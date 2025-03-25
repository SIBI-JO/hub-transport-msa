# 프로젝트 목적 / 상세

## 1) 프로젝트 목적
- MSA 기반 국내 B2B 물류 및 배송 관리 시스템 구축

## 2) 프로젝트 상세
### 주요 기능 및 시나리오

- 허브 관리: 전국 17개 허브의 위치 및 상태 관리
- 허브 이동 경로 관리: 주문의 수령 업체부터 배송 업체까지 허브 간 이동 경로 관리
- 업체 관리: 생산업체 및 수령업체 등록, 관리
- 상품 관리: 업체가 보유한 상품 등록 및 재고 반영
- 주문 관리: 수령업체의 주문 요청 처리 및 재고 차감/복원
- 배송 관리: 허브 간 이동 수령 업체까지 배송 이력 관리
- 배송 담당자 관리: 허브 담당자와 업체 담당자로 나누어 관리
- 사용자 및 권한 관리: 마스터/허브관리자/배송담당자/업체담당자 권한 분리
- 슬랙 메시지 발송: 시스템 내 주요 이벤트 발생 시 알림 전송

### 설계 패턴 및 구조

- 도메인 주도 설계(DDD) 기반의 Layered Architecture 적용
- API Gateway를 통해 모든 요청 제어 및 필터 처리
- 모노레포 방식의 프로젝트 구성

# 인프라 설계도
## 1) 인프라 설계도
![image](https://github.com/user-attachments/assets/ce9dab7a-260c-414f-95f3-28be81d6bfca)
## 2) API Gateway Filter 상세 구조
![image](https://github.com/user-attachments/assets/d1494d7f-f07d-4bb5-887c-cdf70b07d369)


# 서비스 구성 및 실행방법
- [서비스 구성 및 실행방법](https://github.com/SIBI-JO/hub-transport-msa/wiki/%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%EC%84%B1-%EB%B0%8F-%EC%8B%A4%ED%96%89%EB%B0%A9%EB%B2%95)


# ERD
- 링크 : https://www.erdcloud.com/d/dmvPP47iLhn4hQknd

![image](https://github.com/user-attachments/assets/6926c395-9b9a-422d-ab47-712d4eba8304)


# 기술스택

| 분류 | 상세 |
| ------------ | ------------- |
| IDE | IntelliJ |
| Language | Java 17 |
| Framework | Spring Boot 3.4.2 |
| Repository | PostgreSQL 16.2, Redis |
| Build Tool | Gradle 8.8 |
| Service Discovery | Eureka |
| DevOps - dev |  Docker, Docker-compose |
| ECT | Zipkin, Feign Client, Swagger |



# 트러블슈팅
- [트러블 슈팅 #1 : 삭제/수정된 유저의 JWT를 통한 접근 문제 해결](https://github.com/SIBI-JO/hub-transport-msa/wiki/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%231:-%EC%82%AD%EC%A0%9C-%EC%88%98%EC%A0%95%EB%90%9C-%EC%9C%A0%EC%A0%80%EC%9D%98-JWT%EB%A5%BC-%ED%86%B5%ED%95%9C-%EC%A0%91%EA%B7%BC-%EB%AC%B8%EC%A0%9C)
- [트러블슈팅 #2 : MSA 환경에서 DTO 필드 네이밍 불일치 문제](https://github.com/SIBI-JO/hub-transport-msa/wiki/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%232-:-MSA-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-DTO-%ED%95%84%EB%93%9C-%EB%84%A4%EC%9D%B4%EB%B0%8D-%EB%B6%88%EC%9D%BC%EC%B9%98-%EB%AC%B8%EC%A0%9C)
- [트러블슈팅 #3 : 주문과 배송의 동시 생성에서 발생한 트랜잭션 문제](https://github.com/SIBI-JO/hub-transport-msa/wiki/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%233-%EC%A3%BC%EB%AC%B8%EA%B3%BC-%EB%B0%B0%EC%86%A1%EC%9D%98-%EB%8F%99%EC%8B%9C-%EC%83%9D%EC%84%B1%EC%97%90%EC%84%9C-%EB%B0%9C%EC%83%9D%ED%95%9C-%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98-%EB%AC%B8%EC%A0%9C)
- [트러블슈팅 #4 : 허브 투 허브 릴레이 방식의 다익스트라 비용 계산시 카카오맵 API 이용 및 많은 API 호출](https://github.com/SIBI-JO/hub-transport-msa/wiki/%ED%8A%B8%EB%9F%AC%EB%B8%94%EC%8A%88%ED%8C%85-%234-:-%ED%97%88%EB%B8%8C-%ED%88%AC-%ED%97%88%EB%B8%8C-%EB%A6%B4%EB%A0%88%EC%9D%B4-%EB%B0%A9%EC%8B%9D%EC%9D%98-%EB%8B%A4%EC%9D%B5%EC%8A%A4%ED%8A%B8%EB%9D%BC-%EB%B9%84%EC%9A%A9-%EA%B3%84%EC%82%B0%EC%8B%9C-%EC%B9%B4%EC%B9%B4%EC%98%A4%EB%A7%B5-API-%EC%9D%B4%EC%9A%A9-%EB%B0%8F-%EB%A7%8E%EC%9D%80-API-%ED%98%B8%EC%B6%9C)


# 팀원 역할분담
|Leader|Member|Member|Member|
|:-:|:-:|:-:|:-:|
|<img src="https://avatars.githubusercontent.com/u/58907538?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/96772776?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/66461013?v=4" width="150" height="150"/>|<img src="https://avatars.githubusercontent.com/u/139435177?v=4" width="150" height="150"/>|
|[박재은](https://github.com/park-jaeeun)|[박성빈](https://github.com/dohdong)|[주재일](https://github.com/juji1007)|[윤정영](https://github.com/jyYoon96)|
| BE / 인증,인가 / 유저, 배송담당자 / API Gateway | BE / 업체 / 상품 / AI 및 slack | BE / 허브 / 허브 이동경로 / 공통 모듈 | BE / 주문 / 배송 | 
