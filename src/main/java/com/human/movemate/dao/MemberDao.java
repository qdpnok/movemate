package com.human.movemate.dao;

import com.human.movemate.model.Member;
import lombok.RequiredArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// @ 붙은 애들을 어노테이션이라고 부름

// Repository : 스프링에 Repository(DB와 직접적으로 상호작용하는 역할)로 등록
// RequiredArgsConstructor : 의존성 주입을 축약해줌
@Repository
@RequiredArgsConstructor
public class MemberDao {
    // 의존성 주입
    private final JdbcTemplate jdbc;


    // 회원 가입 INSERT 예제

    // public -> 접근제한자. 해당 메서드를 이 클래스 외부에서도 호출할 수 있음을 의미
    // boolean -> 반환 타입. boolean 타입으로 반환. [return] 으로 반환하려는 값과 [같은 자료형]을 적어야 함.
    // (Member member) -> Member 타입의 member 매개변수.
    //                    Member 타입의 값를 받아서 해당 매서드 내에서 member라는 이름으로 부름
    public boolean save(Member member) {
        // INSERT INTO [테이블명] ( [칼럼1], [칼럼2], ... , [칼럼n] )
        // VALUES ( ?, ?, ..., ? ) <- 위에서 작성한 [삽입할 칼럼 개수]와 [? 개수]가 맞아야 함.
        @Language("SQL")
        String sql = """
        INSERT INTO member (name, user_id, password, email, phone_no)
        VALUES (?, ?, ?, ?, ?)
        """;

        // jdbc.update : 데이터 삽입, 수정, 삭제에 사용함.
        // update( sql, ?에 담을 값 1, ... , ?에 담을 값 n);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // jdbc.update는 삽입, 수정, 삭제에 성공한 행의 갯수를 반환함.
        // 때문에 결과값이 0보다 큰지 검사해서 성공/실패 여부를 확인
        return jdbc.update(sql, member.getName(), member.getUserId(), member.getPassword(), member.getEmail(), member.getPhoneNo()) > 0;
    }


    // ID로 값 검색 SELECT 예제

    // public -> 접근제한자. 해당 메서드를 이 클래스 외부에서도 호출할 수 있음을 의미
    // Member -> 반환 타입. Member 타입으로 반환. [return] 으로 반환하려는 값과 [같은 자료형]을 적어야 함.
    // (Long id) -> Long 타입의 id 매개변수.
    //              Long 타입의 값를 받아서 해당 매서드 내에서 id라는 이름으로 부름
    public Member findById(String id) {
        // SELECT [칼럼 1], [칼럼 2], ... , [칼럼 n] (또는 * - ALL)
        // FROM [테이블명]
        // WHERE [조건] -> 특별한 조건이 없다면 생략, ?로 값 대입 가능
        @Language("SQL")
        String sql = "SELECT * FROM member WHERE user_id = ?";

        // jdbc.query : 여러 행의 값을 가져올 때 사용함
        // query( sql, new RowMapper(), ?에 답을 값1, ... , ?에 담을 값 );
        List<Member> list = jdbc.query(sql, new MemberRowMapper(), id);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // DB에서 일치하는 id 가 없으면 빈 리스트가 넘어옴.

        // 값을 하나만 가져오는 queryForObject는 반드시 하나의 값이 검색되는 것이 보장되어야 하기 때문에
        // query를 사용해서 리스트로 값을 가져온다음, 값이 있는지 없는지 체크해서
        // 값이 없다면 null을, 있다면 첫 번째 값을 반환함.
        return list.isEmpty() ? null : list.get(0);
    }


    // ID로 값 변경 UPDATE 예제

    // public -> 접근제한자. 해당 메서드를 이 클래스 외부에서도 호출할 수 있음을 의미
    // boolean -> 반환 타입. boolean 타입으로 반환. [return] 으로 반환하려는 값과 [같은 자료형]을 적어야 함.
    // (Long id, Member member) -> Long 타입의 id 매개변수, Member 타입의 member 매개변수.
    //                             Long 타입의 값를 받아서 해당 매서드 내에서 id라는 이름으로 부름
    //                             Member 타입의 값를 받아서 해당 매서드 내에서 member라는 이름으로 부름
    public boolean update(Long no, Member member){
        // UPDATE [테이블명] SET [변경할 칼럼명1] = ?, ... , [변경할 칼럼명n] = ? WHERE [조건]
        @Language("SQL")
        String sql = "UPDATE member SET pwd = ?, name =? WHERE user_no = ?";

        // jdbc.update : 데이터 삽입, 수정, 삭제에 사용함.
        // update( sql, ?에 담을 값 1, ... , ?에 담을 값 n);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // jdbc.update는 삽입, 수정, 삭제에 성공한 행의 갯수를 반환함.
        // 때문에 결과값이 0보다 큰지 검사해서 성공/실패 여부를 확인
        return jdbc.update(sql, member.getPassword(), member.getName(), no) > 0;
    }


    // ID로 값 삭제 DELETE 예제

    // public -> 접근제한자. 해당 메서드를 이 클래스 외부에서도 호출할 수 있음을 의미
    // boolean -> 반환 타입. boolean 타입으로 반환. [return] 으로 반환하려는 값과 [같은 자료형]을 적어야 함.
    // (Long id) -> Long 타입의 id 매개변수, Member 타입의 member 매개변수.
    //              Long 타입의 값를 받아서 해당 매서드 내에서 id라는 이름으로 부름
    public boolean delete(Long no){
        // DELETE [테이블명] WHERE [조건]
        @Language("SQL")
        String sql = "DELETE member WHERE user_no = ?";

        // jdbc.update : 데이터 삽입, 수정, 삭제에 사용함.
        // update( sql, ?에 담을 값 1, ... , ?에 담을 값 n);

        // return 으로 반환하는 값은 메서드의 반환 타입과 일치해야함.

        // jdbc.update는 삽입, 수정, 삭제에 성공한 행의 갯수를 반환함.
        // 때문에 결과값이 0보다 큰지 검사해서 성공/실패 여부를 확인
        return jdbc.update(sql, no) > 0;
    }


    // DB의 member 테이블에서 가져온 값을 Member 객체에 담을 때 사용
    static class MemberRowMapper implements RowMapper<Member> {

        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Member(
                    // rs -> 가져온 데이터
                    // getLong ("[칼럼명]") -> 칼럼명이 [칼럼명]인 Long 타입의 데이터를 가져온다.
                    // 데이터베이스의 칼럼 명과 [칼럼명] 실제로 일치해야함.
                    rs.getLong("user_no"),
                    rs.getString("name"),
                    rs.getString("user_id"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("phone_no")
                    // 오라클에서 날짜 타입은 timestamp 밖에 없기 때문에 날짜 타입을 가져올 때
                    // .toLocalDateTime() 메서드를 사용해 localDateTime 타입으로 변환을 해줘야함.
            );
        }
    }
}
