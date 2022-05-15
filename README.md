# spring-db
### H2 DB 
- [1.4.200](https://h2database.com/h2-2019-10-14.zip)
- JDBC URL
  - `test` DB 생성 
    - `jdbc:h2:~/test`
  - 접근 방법
    - `jdbc:h2:tcp://localhost/$TEST_DB_PATH`


## JDBC (Java Database Connectivity)
자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API

대표적으로 3가지 기능을 표준 인터페이스로 정의해서 제공
- `java.sql.Connection`
- `java.sql.Statement`
- `java.sql.ResultSet`

**JDBC 드라이버**

위 JDBC 인터페이스를 각각의 DB벤더에서 자신의 DB에 맞도록 구현해서 라이브러리로 제공
- 예시) Oracle DB에 접근할 수 있는 것은 Oracle JDBC 드라이버이다.


## JDBC를 편리하게 사용하는 기술
### SQL Mapper
- JDBC를 편리하게 사용하도록 도와준다.
  - SQL 응답 결과를 객체로 편리하게 변환
  - JDBC의 반복 코드를 제거
- 개발자가 SQL을 직접 작성하는 단점 
- 대표 기술
  - `Spring JdbcTemplate`
  - `MyBatis`
### ORM
객체를 관계형 데이터베이스 테이블과 매핑해주는 기술
- 반복적인 SQL을 직접 작성하지 않고 ORM이 SQL을 동적으로 만들어 실행
- 다른 SQL을 사용하는 문제돌 중간에서 해결
- 대표 기술
  - JPA (자바 진영의 ORM 표준 인터페이스)
  - 하이버네이트 (JPA의 구현체)
  - 이클립스링크 (JPA의 구현체)

