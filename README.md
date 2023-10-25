# 42Nomad Backend

## 프로젝트 소개
매일 자리를 찾아 방랑하는 카뎃들의 슬기로운 42생활을 위한 42서울 회의실 및 자리 상태 원격 확인 서비스
- 42서울 소프트웨어 경진대회 Life is 42! Challenge 우수상 수상작
- 이노베이션아카데미 학장상 수상
- 2023년 이노콘 발표 [<img src="https://img.shields.io/badge/YouTube-FF0000?style=flat&logo=youtube&logoColor=white">](https://youtu.be/TmVEQyRo_Bc?si=26pYwWhGC3vyo--V&t=9339)
## 팀원 및 기술 스택
<div align="center">
  
[🐿️hyunjcho](https://github.com/highjcho) && [🦕jonkim](https://github.com/dino9881)
<div align="center">
  <div>
    <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white">
    <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white">
    <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
  </div>
  <div>
    <img src="https://img.shields.io/badge/AmazonEC2-FF9900?style=for-the-badge&logo=AmazonEC2&logoColor=white">
    <img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">
    <img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
  </div>
  </div>
</div>
  
## 타임라인
  |날짜|내용|
  |:-:|--|
  |**23.08.10** ~ **23.08.13** |개발환경 세팅|
  |**23.08.14** ~ **23.08.17** |DB, API 설계|
  |**23.08.18** ~ **23.08.20** |API 개발|
  |**23.08.21** ~ **23.08.24** |IoT 기능 연결|
  |**23.08.25** ~ **23.08.28** |추가 기능 개발(슬랙봇)|
  |**23.08.29** ~ **23.08.31** |예외 처리|
  |**23.09.01** ~ **23.09.04** |테스트|
  |**23.09.04** ~ **23.09.06** |해커톤 진행|
  |**23.09.07** ~ **23.09.07** |최종 발표|
  
## 디렉토리

```

└── backend            
    ├── admin           # 관리자 기능
    ├── board           # 분실물 게시판
    ├── global
    │   ├── api         # 외부 api 요청
    │   │   └── mapper  # api json 역직렬화 매핑 클래스
    │   ├── config      # security 등 설정
    │   ├── exception   # 예외 처리
    │   ├── handler     # login/logout 등 handler
    │   ├── jwt         # jwt
    │   ├── oauth       # oauth login/logout
    │   └── reponse     # 응답 관련 
    ├── history         # 사용자 자리 기록
    ├── imac            # 클러스터 아이맥 자리
    ├── iot             # 회의실 IoT
    ├── meetingroom     # 회의실
    ├── member          # 멤버
    ├── slack           # 슬랙봇
    ├── starred         # 아이맥 즐겨찾기
    └── statistics      # 아이맥 즐겨찾기 및 회의실 사용 통계
```
## 아키텍쳐

  <img width="678" alt="스크린샷 2023-10-14 오후 3 16 27" src="https://github.com/42nomad/backend/assets/76129597/f3ca0c7f-e8a8-4701-91e7-7855b22530ea">

  
### DB 구조 
![DB_Diagrampdf ](https://github.com/42nomad/backend/assets/76129597/9ca9113a-02db-473e-b0be-b1beb6bdae85)


### 배포 과정
<img width="782" alt="스크린샷 2023-10-14 오후 3 36 00" src="https://github.com/42nomad/backend/assets/76129597/5d6c6c5d-01be-4e5b-aa44-569868c66491">


- Main Branch push 시 자동 배포
1. 프로젝트 빌드 후 S3 저장소 Push
2. CodeDeploy 실행
3. EC2 배포 명령
4. S3에 저장된 파일을 가져온뒤 저장된 스크립트를 통한 배포

## 인증 인가
![auth_flow_chart](https://github.com/42nomad/backend/assets/91729403/e283a7cd-2551-4240-8878-344f2a496af7)


## 기타 기능

### 통계
1. 공통
- 엑셀 다운로드 제공
- 기간별 및 조건별 조회 제공
2. 클러스터 아이맥
- 매주 월요일 즐겨찾기에 등록된 아이맥 좌석별 즐겨찾기 횟수 DB 저장
- 기간별 즐겨찾기 횟수 증감 추이를 통해 좌석 선호도 확인
3. 회의실
- 회의실 사용 시 사용 횟수 증가 및 사용 시간 DB 저장
- 회의실별 누적 사용 횟수 및 누적 사용 시간 정보를 통해 회의실 선호도 확인


## Rule
### Commit Rules
``` type: #(issue) title body  ```
- feat: 새로운 기능을 추가  
- fix: 버그 수정  
- style: 코드 맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우  
- refactor: 코드 리팩토링  
- comment: 필요한 주석 추가 및 변경  
- docs: 문서 수정  
- test: 테스트 코드, 리팩터링 테스크코드 추가  
- rename: 파일 혹은 폴더명은 수정하거나 옮기는 경우  
- remove: 파일을 삭제하는 경우  
- chore: 빌드 업무 수정, 패키지 매니저 수정, 패키지 관리자 구성 등 업데이트  
- !BREAKING CHANGE:	커다란 API 변경의 경우
- !HOTFIX:	급하게 치명적인 버그를 고쳐야하는 경우  
