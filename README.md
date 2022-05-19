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

## 커넥션 풀
**데이터베이스 커넥션을 매번 획득**

1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회
2. DB 드라이버는 DB와 TCP/IP 커넥션을 연결 (3-way-handshaking 포함)
3. 커넥션이 연결되면 ID/PW 기타 부가정보를 DB에 전달
4. ID/PW를 통해 내부 인증을 완료하고, DB 세션을 생성
5. DB는 커넥션 생성이 완료되었다는 응답을 보냄
6. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에 반환

**매번 커넥션을 새로 생성하기 위한 리소스를 매번 사용해야하고 그 만큼 시간이 소비된다.**

위 문제를 해결하기 위해 커넥션을 미리 생성해두고 재사용하는 **커넥션 풀**이 있다.

애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관한다.

보통 얼마나 보관할 지는 서비스의 특징과 서버 스펙에 다르지만 기본값은 보통 10개이다.

- 커넥션 풀에 있는 커넥션은 이미 DB와 연결되어있기 때문에 즉시 SQL을 DB에 전달이 가능
- 애플리케이션 로직에서는 DB 드라이버를 통해서 새로운 커넥션을 획득하는 것이 아니다.
- 커넥션 풀에서 이미 생성되어 있는 커넥션을 객체 참조로 그냥 가져다 쓰기만 하면된다.
- 커넥션 풀에 커넥션을 요청하면 커넥션 풀은 자신이 가지고 있는 커넥션 중에 하나를 반환
- 커넥션을 모두 사용하고 나면 다음에 다시 사용할 수 있도록 해당 커넥션을 그대로 커넥션 풀에 반환

대표적인 커넥션 풀 오픈소스
- `commons-dbcp2`
- `tomcat-jdbc pool`
- `HikariCP`
  - 스프링 부트 2.0부터는 기본적으로 제공


## DataSource 이해
커넥션을 얻는 방법은 앞서 학습한 JDBC `DriverManager`를 직접 사용하거나, 커넥션 풀을 사용하는 등 다양한 방법이 존재

**커넥션을 획득하는 방법을 추상화**
- `javax.sql.DataSource` 인터페이스 제공
- `DataSource`는 **커넥션을 획득하는 방법을 추상화**하는 인터페이스
- 이 인터페이스의 핵심 기능은 커넥션 조회

```java
public interface DataSource {
  Connection getConnection() throws SQLException;
}
```

- `DBCP2 커넥션 풀`, `HikariCP` 커넥션 풀의 코드를 직접 의존하지 않고 `DataSource` 인터페이스에만 의존하도록 로직을 작성하면 된다.
- 커넥션 풀 구현 기술을 변경하고 싶으면 해당 구현체로 갈아끼우기만 하면 된다.
- DriverManager에서 DataSource 기반 커넥션 풀을 사용하려면 DriverManagerDataSource라는 클래스를 사용해야한다.