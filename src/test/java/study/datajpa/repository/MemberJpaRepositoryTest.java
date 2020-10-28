package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("Member A");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("Member 1");
        Member member2 = new Member("Member 2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        long countAfterDeleteAll = memberJpaRepository.count();
        assertThat(countAfterDeleteAll).isEqualTo(0);
    }

    @Test
    void pagingTest() {
        Member member;
        for (int i = 1; i <= 10; i++) {
            member = new Member("Member " + String.valueOf(i), 10);
            memberJpaRepository.save(member);
        }

        int age = 10;
        int offset = 3;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(10);
    }

    @Test
    void bulkUpdateTest() {
        memberJpaRepository.save(new Member("Member 1", 11));
        memberJpaRepository.save(new Member("Member 2", 12));
        memberJpaRepository.save(new Member("Member 3", 24));
        memberJpaRepository.save(new Member("Member 4", 42));
        memberJpaRepository.save(new Member("Member 5", 31));

        int resultCount = memberJpaRepository.bulkAgePlus(20);

        em.flush();
        em.clear();

        assertThat(resultCount).isEqualTo(3);
    }

}