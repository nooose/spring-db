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
- 개발자는 SQL만 작성하면 해당 SQL의 결과를 객체로 편리하게 매핑해준다.
- JDBC를 직접 적용할 때 발생하는 여러가지 중복을 제거해주고, 기타 개발자에게 여러가지 편리한 기능을 제공
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
  - Spring Data JPA
  - Querydsl
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

## 트랜잭션 - 개념
데이터베이스에서 하나의 거래를 안전하게 처리하도록 보장해주는 것을 뜻함

### 트랜잭션 ACID
- A(Automicity) 원자성: 트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공 하거나 모두 실패해야 한다.
- C(Consistency) 일관성: 모든 트랜잭션은 일관성 있는 데이터베이스 상태를 유지해야 한다.
  - 데이터베이스에서 정한 무결성 제약 조건을 항상 만족해야한다.
- I(Isolation) 격리성: 동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않도록 격리한다.
  - 동시에 같은 데이터를 수정하지 못하도록 해야한다.
  - 격리성은 동시성과 관련된 성능 이슈로 인해 트랜잭션 격리 수준(Isolation level)을 선택할 수 있다.
- D(Durability) 지속성: 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다.
  - 중간에 시스템에 문제가 발생해도 데이터베이스 로그 등을 사용해서 성공한 트랜잭션 내용을 복구해야한다.


**ANSI 표준 트랜잭션 격리 수준 - Isolation Level**
- READ UNCOMMITED (커밋되지 않은 읽기)
  - 성능 우수하지만 데이터 보장 X
- READ COMMITED (커밋된 읽기)
  - 보통 많이 사용
- REPEATABLE READ (반복 가능한 읽기)
- SERIALIZABLE (직렬화 기능)

## DB 락
- 업데이트
  - 트랜잭션 시작을 할 때 점유, 트랜잭션이 끝나고 락을 반납한다.
- 조회
  - 일반적인 조회는 락을 사용하지 않는다.
  - 조회 시점에 락이 필요한 경우 (`select for update`)
    - 트랜잭션 종료 시점까지 해당 데이터를 다른 곳에서 변경하지 못하도록 강제로 막는다.
    - 예시) 중요한 연산이어서 연산을 완료할 때 까지 다른 곳에서 변경하면 안되는 경우

# **참고** 
## 권장하는 식별자 선택 전략
1. `null` 값은 허용하지 않는다.
2. 유일해야 한다.
3. 변해선 안 된다.

## 테이블의 기본 키를 선택하는 전략은 크게 2가지가 있다.
- 자연 키
  - 비즈니스에 의미가 있는 키
    - ex) 주민등록번호, 이메일, 전화번호
- 대리 키
  - 비즈니스와 관련 없는 임의로 만들어진 키, 대체 키로도 불린다.
    - ex) 오라클 시퀀스, auto_increment, identity, 키 생성 테이블 사용

### 자연 키보다는 대리 키를 권장한다.
- 비즈니스 환경은 변한다.
- 정부 정책이 변경되면서 테이블 구조가 변경되는 경우
- 대리 키는 비즈니스와 무관한 임의의 값이므로 요구사항이 변경되어도 기본 키가 변경되는 일은 드물다.

# JDBC Template
- spring-jdbc 라이브러리에 포함되어 있는데, 이 라이브러리는 스프링 JDBC를 사용할 때 기본으로 사용되는 라이브러리.
- 반복문제 해결
  - 커넥션 획득
  - `statement`를 준비하고 실행
  - 결과를 반복하도록 루프를 실행
  - 커넥션 종료, `statement`, `resultset` 종료
  - 트랜잭션 다루기 위한 커넥션 동기화
  - 예외 발생시 스프링 예외 변환기 실행

- 단점
 - 동적 SQL을 해결하기 어렵다.

# 스프링 트랜잭션

- `@Transactional` Annotation이 특정 클래스나 메서드에 하나라도 있으면 트랜잭션 AOP는 프록시를 만들어 스프링 컨테이너에 등록한다. 그리고 프록시는 내부에 실제 클래스를 참조하게 된다.
- 프록시는 대상 클래스를 상속해서 만들어지기 때문에 다형성을 활용할 수 있다.

## 트랜잭션 AOP 주의 사항 - 프록시 내부 호출
메서드 앞에 별도의 참조가 없으면 `this` 가 붙어 자기 자신의 인스턴스를 가리킨다.

결과적으로 내부 메서드를 호출하는 `this.internal()`이 되므로, 실제 객체(`target`)이 호출된다. 따라서 트랜잭션을 적용할 수 없다.

> public 메서드만 트랜잭션이 적용된다.

### 프록시 내부 호출 문제 해결
- 클래스 분리

## 트랜잭션 AOP 주의 사항 - 초기화 시점
`@PostConstuct` 와 `@Transactional` 을 함께 사용하면 트랜잭션이 적용되지 않는다.

이유는 초기화 코드가 먼저 호출되고, 그 다음에 트랜잭션 AOP가 적용되기 때문이다.

### 초기화 시점 문제 해결
`@EventListener(value = ApplicationReadyEvent.class)` 이벤트를 사용

스프링이 컨테이너가 완전히 생성되고 난 다음에 이벤트가 붙은 메서드를 호출해준다.

## `rollBackFor`
예외 발생 시 스프링 트랜잭션의 기본 정책
- 언체크 예외 → 롤백
- 체크 예외 → 커밋

```java
@Transactional(rollbackFor = Exception.class)
```
이렇게 지정하면 체크 예외인 `Exception` 이 발생해도 롤백한다.


> CheckedException 을 롤백하지 않고 커밋하는 이유는 우리가 처리할 수 있는 예외이기 때문이다. 반면 Runtime Exception 은 대부분 예상할 수 없는 시점에 발생하기 때문에 처리가 제대로 되지 않을 수 있다.

## `noRollbackFor`
`rollBackFor`와 반대이다.

## isolation 
트랜잭션 격리 수준을 지정할 수 있다. 기본 값은 데이터베이스에서 설정한 트랜잭션 격리 수준을 사용하는 `DEFAULT`이다.

대부분 데이터베이스에서 설정한 기준을 따른다. 

- `DEFAULT`: 데이터베이스에서 설정한 격리 수준을 따른다.
- `READ_UNCOMMITTED`: 커밋되지 않은 읽기
- `READ_COMMITTED`: 커밋된 읽기
- `REPEATABLE_READ`: 반복 가능한 읽기
- `SERIALIZABLE`: 직렬화 기능

> JPA(하이버네이트)는 읽기 전용 트랜잭션의 경우 커밋 시점에 플러시를 호출하지 않는다. 읽기 전용이니 변경에 사용되는 플러시를 호출할 필요가 없다. 추가로 변경 감지를 위한 스냅샷 객체도 생성하지 않는다.

> 데이터베이스에 따라 읽거 전용 트랜잭션의 경우 읽기만 하면 되므로, 내부에서 성능 최적화가 발생한다.

# 트랜잭션 전파(propagation)
트랜잭션이 수행중인 상태에서 내부적으로 트랜잭션을 수행시키는 경우 외부 트랜잭션과 내부 트랜잭션을 묶어 하나의 트랜잭션으로 만들어준다.

이것이 기본 옵션인 `REQUIRED`이다. 옵션을 통해 다른 동작방식을 선택할 수 있다.

## 물리 트랜잭션/논리 트랜잭션
- 논리 트랜잭션은 하나의 물리 트랜잭션으로 묶인다.
  - 트랜잭션 매니저를 통해 트랜잭션을 사용하는 단위이다.
  - 트랜잭션이 진행되는 중에 내부에 추가로 트랜잭션을 사용하는 경우에 사용하는 개념이다.
- 물리 트랜잭션은 실제 데이터베이스에서 적용되는 트랜잭션을 뜻한다.
  - 실제 커넥션을 통해서 커밋, 롤백하는 단위

**원칙**
- 모든 논리 트랜잭션이 커밋되어야 물리 트랜잭션이 커밋된다.
  - 모든 트랜잭션 매니저를 커밋해야 물리 트랜잭션이 커밋된다.
- 하나의 논리 트랜잭션이라도 롤백되면 물리 트랜잭션은 롤백된다.
  - 하나의 트랜잭션 매니저라도 롤백하면 물리 트랜잭션은 롤백된다.

**즉, 처음 트랜잭션을 시작한 외부 트랜잭션이 실제 물리 트랜잭션을 관리한다.**

> 트랜잭션 참여



### **내부 트랜잭션이 롤백을 수행하고 외부 트랜잭션이 커밋을 하는 경우**
- 내부 트랜잭션 롤백
  - `Participating transaction failed - marking existing transaction as rollback-only`
  - 내부 트랜잭션을 롤백하면 실제 물리 트랜잭션은 롤백하지 않는다.
  - 대신 기존 트랜잭션을 롤백 전용으로 표시한다.
    - 트랜잭션 동기화 매니저에 마크한다. `rollbackOnly=true`
  - 
- 외부 트랜잭션 커밋
  - `Global transaction is marked as rollback-only but transactional code requested commit`
  - 커밋은 호출했지만, 전체 트랜잭션이 롤백 전용으로 표시되어 있다. 따라서 물리 트랜잭션을 롤백한다.
  - `UnexpectedRollbackException` 런타임 예외 발생

## REQUIRES_NEW
외부 트랜잭션과 내부 트랜잭션을 완전히 분리해서 사용하는 방법
- 커밋과 롤백도 각각 별도로 이루어지게 된다.

```java
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute()); // con0
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());


        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 항상 신규 트랜잭션을 만드는 옵션
        // 기본 옵션은 REQUIRED
        TransactionStatus inner = txManager.getTransaction(definition); // con1
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        // inner.isNewTransaction()=true
```

## 다양한 전파 옵션
**REQUIRED**, **REQUIRES_NEW** 를 제외하고 나머지는 거의 사용하지 않는다.

- **REQUIRED**
  - 가장 많이 사용하는 기본 설정
  - 기존 트랜잭션이 없으면 생성하고, 있으면 참여
- **REQUIRES_NEW**
  - 항상 새로운 트랜잭션을 생성
- **SUPPORT**
  - 기존 트랜잭션이 없으면, 없는대로 진행하고, 있으면 참여
- **NOT_SUPPORT**
  - 기존 트랜잭션이 없으면, 없는대로 진행하고, 있어도 트랜잭션은 보류한다.
- **MANDATORY**
  - 트랜잭션이 반드시 있어야 한다.
  - 기존 트랜잭션이 없으면 `IllegalTransactionStateException` 예외 발생
- **NEVER**
  - 트랜잭션을 사용하지 않음
- **NESTED**
  - 중첩 트랜잭션을 만든다.
  - 외부 트랜잭션의 영향을 받지만, 중첩 트랜잭션은 외부에 영향을 주지 않는다.
  - JDBC savepoint 기능을 사용한다.
    - JPA에서는 사용할 수 없다.
