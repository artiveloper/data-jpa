package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.iterable;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    void testMember() {
        Member member = new Member("Member A");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("Member 1");
        Member member2 = new Member("Member 2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long countAfterDeleteAll = memberRepository.count();
        assertThat(countAfterDeleteAll).isEqualTo(0);
    }

    @Test
    void queryTest() {
        Member member1 = new Member("Member 1", 11);
        Member member2 = new Member("Member 2", 12);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMember = memberRepository.findMember("Member 1", 11);

        assertThat(findMember.get(0).getUsername()).isEqualTo("Member 1");
    }

    @Test
    void findMemberDtoTest() {
        Team team = new Team("Team A");
        Member member1 = new Member("Member 1", 11, team);

        teamRepository.save(team);
        memberRepository.save(member1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println(dto.getUsername());
            System.out.println(dto.getTeamName());
        }
    }

    @Test
    void findByUsernamesTest() {
        Member member1 = new Member("Member 1", 11);
        Member member2 = new Member("Member 2", 12);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMember = memberRepository.findByUsernames(Arrays.asList("Member 1", "Member 2"));

        assertThat(findMember.size()).isEqualTo(2);
    }

    @Test
    void pagingTest() {
        Member member;
        for (int i = 1; i <= 10; i++) {
            member = new Member("Member " + String.valueOf(i), 10);
            memberRepository.save(member);
        }

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> members = page.getContent();

        assertThat(members.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(10);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext());
    }

    @Test
    void bulkUpdateTest() {
        memberRepository.save(new Member("Member 1", 11));
        memberRepository.save(new Member("Member 2", 12));
        memberRepository.save(new Member("Member 3", 24));
        memberRepository.save(new Member("Member 4", 42));
        memberRepository.save(new Member("Member 5", 31));

        int resultCount = memberRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void fetchJoinTest() {
        Team team1 = new Team("Team A");
        Team team2 = new Team("Team B");

        Member member1 = new Member("Member 1", 11, team1);
        Member member2 = new Member("Member 2", 19, team2);

        teamRepository.save(team1);
        teamRepository.save(team2);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> membersFetchJoin = memberRepository.findMembersFetchJoin();
        for (Member member : membersFetchJoin) {
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
            System.out.println("==================================================");
        }
    }

}