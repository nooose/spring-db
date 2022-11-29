package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON
     */
    @Test
    void outerTxOffSuccess() {
        String username = "outerTxOffSuccess";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON Exception
     */
    @Test
    void outerTxOffFail() {
        String username = "로그예외_outerTxOffFail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * memberService @Transactional:ON
     * memberRepository @Transactional:OFF
     * logRepository @Transactional:OFF
     */
    @Test
    void singleTx() {
        String username = "singleTx";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON
     */
    @Test
    void outerTxOnSuccess() {
        String username = "outerTxOnSuccess";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON Exception
     */
    @Test
    void outerTxOnFail() {
        String username = "로그예외_outerTxOnFail";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * memberService @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON Exception
     */
    @Test
    void recoverExceptionFail() {
        String username = "로그예외_recoverExceptionFail";

        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * memberService @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository @Transactional:ON (REQUIRES_NEW) Exception
     */
    @Test
    void recoverExceptionSuccess() {
        String username = "로그예외_recoverExceptionSuccess";

        memberService.joinV2(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }
}
