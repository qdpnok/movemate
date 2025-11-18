package com.human.movemate.service.impl;

import com.human.movemate.dao.UserDao;
import com.human.movemate.dao.UserProfileDao;
import com.human.movemate.dto.UserProDto;
import com.human.movemate.model.User;
import com.human.movemate.model.UserProfile;
import com.human.movemate.service.FileStorageService;
import com.human.movemate.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileStorageService fileStorageService;

    // 상속을 준 인터페이스 (UserService) 에
    // 생성자 (public), 반환타입 (boolean), 메서드 이름 (signup), 매개변수 (User user)가
    // 모두 일치하게 정의되어 있어야 함.
    @Override
    @Transactional
    public Long signup(UserProDto userProDto) {
        Long userId = userDao.save(new User(0L, userProDto.getName(),
                userProDto.getUserId(), userProDto.getPassword(), userProDto.getEmail(), userProDto.getPhoneNo(), null, null));

        userProfileDao.save(new UserProfile(0L, userId, userProDto.getGender(),
                userProDto.getAge(), null, userProDto.getRegion(),
                userProDto.getSportType(), userProDto.getPaceDetail(), userProDto.getProfileImageUrl()) );

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
    public UserProDto getByNo(Long no) {
        log.info("유저번호: {}", no);
        return userProfileDao.findByNo(no);
    }

    @Override
    public boolean updateProfile(Long no, String path) {
        return userProfileDao.updateProfile(no, path);
    }

    @Override
    @Transactional
    public boolean update(Long userNo, UserProDto userProDto, MultipartFile profileImage, boolean isImageDeleted) {
        UserProDto originalInfo = userProfileDao.findByNo(userNo);

        String pwd = userProDto.getPassword();
        if(pwd == null || pwd.isEmpty()) {
            pwd = originalInfo.getPassword();
            log.info("기존의 비밀번호: {}", pwd);
        }

        String imagePath = originalInfo.getProfileImageUrl();
        if(profileImage != null && !profileImage.isEmpty()) {
            fileStorageService.deleteIfExists(imagePath);
            imagePath = fileStorageService.storeFile(profileImage, "users", userNo);
        } else if (isImageDeleted) {
            fileStorageService.deleteIfExists(imagePath);
            imagePath = null;
        }

        boolean userUpdateSuccess = userDao.update(userNo, new User(userNo, userProDto.getName(),
                userProDto.getUserId(), pwd,
                userProDto.getEmail(), userProDto.getPhoneNo(), null, null));

        boolean profileUpdateSuccess = userProfileDao.update(userNo, new UserProfile(
                userProDto.getProfileId(), userNo, userProDto.getGender(),
                userProDto.getAge(), userProDto.getPreferMatchingType(), userProDto.getRegion(),
                userProDto.getSportType(), userProDto.getPaceDetail(), imagePath));

        return userUpdateSuccess && profileUpdateSuccess;
    }

    @Override
    @Transactional
    public boolean delete(Long userNo) {
        log.info("회원 탈퇴 (논리적 삭제) 시도: UserNo={}", userNo);

        // 프로필 이미지 경로 조회
        UserProDto originalInfo = userProfileDao.findByNo(userNo);
        String imagePath = originalInfo.getProfileImageUrl();

        // 파일 시스템에서 이미지 삭제
        if (imagePath != null && !imagePath.isEmpty()) {
            fileStorageService.deleteIfExists(imagePath);
            log.info("프로필 이미지 파일 삭제 완료: {}", imagePath);
        }

        // DB에서 논리적 삭제 처리
        return userDao.softDelete(userNo);
    }
}
