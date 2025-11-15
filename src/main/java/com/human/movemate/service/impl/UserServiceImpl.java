package com.human.movemate.service.impl;

import com.human.movemate.dao.UserDao;
import com.human.movemate.dao.UserProfileDao;
import com.human.movemate.dto.UserPro;
import com.human.movemate.model.User;
import com.human.movemate.model.UserProfile;
import com.human.movemate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @ 붙은 애들을 어노테이션이라고 부름

// Repository : 스프링에 Service(dao를 활용해 실제 기능을 구현하는 역할)로 등록
// RequiredArgsConstructor : 의존성 주입을 축약해줌
// Slf4j : 오류 로그 출력 기능을 제공
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    // UserDao 클래스의 기능을 사용하기 위한 의존성 주입
    private final UserDao userDao;
    private final UserProfileDao userProfileDao;

    // 상속을 준 인터페이스 (UserService) 에
    // 생성자 (public), 반환타입 (boolean), 메서드 이름 (signup), 매개변수 (User user)가
    // 모두 일치하게 정의되어 있어야 함.
    @Override
    @Transactional
    public Long signup(UserPro userPro) {
        Long userId = userDao.save(new User(0L, userPro.getName(),
                userPro.getUserId(), userPro.getPassword(), userPro.getEmail(), userPro.getPhoneNo()));

        userProfileDao.save(new UserProfile(0L, userId, userPro.getGender(),
                userPro.getAge(), null, userPro.getRegion(),
                userPro.getSportType(), userPro.getPaceDetail(), userPro.getProfileImageUrl()) );

        return userId;
    }

    @Override
    public User login(User user) {
        log.info("로그인을 위한 정보: {}", user);
        User userRes = userDao.findById(user.getUserId());
        if(userRes == null || !userRes.getPassword().equals(user.getPassword())) {
            return null;
        }
        return userRes;
    }

    @Override
    public UserPro getByNo(Long no) {
        log.info("유저번호: {}", no);
        return userProfileDao.findByNo(no);
    }

    @Override
    public boolean updateProfile(Long no, String path) {
        return userProfileDao.updateProfile(no, path);
    }

    @Override
    public boolean update(Long no, User user) {
        return userDao.update(no, user);
    }

    @Override
    public boolean delete(Long no) {
        return userDao.delete(no);
    }
}
