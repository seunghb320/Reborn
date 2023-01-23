package spring.reborn.domain.user;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import spring.reborn.config.BaseException;
import spring.reborn.config.BaseResponse;
import spring.reborn.config.secret.Secret;
import spring.reborn.domain.user.model.PostUserReq;
import spring.reborn.domain.user.model.PostUserRes;
import spring.reborn.domain.user.model.PostUserStoreReq;
import spring.reborn.domain.user.model.PostUserStoreRes;
import spring.reborn.domain.user.model.*;
import spring.reborn.utils.AES128;
import spring.reborn.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static spring.reborn.config.BaseResponseStatus.*;

@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************
    // 회원가입(POST)
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(postUserReq.getUserEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getUserPwd()); // 암호화코드
            postUserReq.setUserPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq);
            String userNickname = userDao.getUserNickname(userIdx);
//            return new PostUserRes(userIdx);

//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
//            jwt 발급.
        String jwt = jwtService.createJwt(userIdx);
        return new PostUserRes(userIdx,userNickname,jwt);
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
        throw new BaseException(DATABASE_ERROR);
        }
    }
    // 스토어 회원가입(POST)
    public PostUserStoreRes createUserStore(PostUserStoreReq postUserStoreReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkUserEmail(postUserStoreReq.getUserEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserStoreReq.getUserPwd()); // 암호화코드
            postUserStoreReq.setUserPwd(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        PostUserStoreRes postUserStoreRes;
        try {
            int storeId = userDao.createUserStore(postUserStoreReq);
            postUserStoreRes = userDao.getStoreInform(storeId);
         } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

        try {
            //  jwt 발급.
            String jwt = jwtService.createJwt(postUserStoreRes.getUserIdx());
            return new PostUserStoreRes(postUserStoreRes.getStoreIdx(), postUserStoreRes.getUserIdx(), postUserStoreRes.getStoreName() ,jwt);

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

    }

    // 포인트 적립, 취소 - hyerm
    @Transactional
    public PatchUserPointRes editUserPoint(@RequestBody PatchUserPointReq patchUserPointReq) {
        System.out.println("service 시작");

        return userDao.editUserPoint(patchUserPointReq);
    }

    // 이웃 회원탈퇴(Patch)
    @Transactional
    public void modifyUserStatus(PatchUserStatusReq patchUserStatusReq) throws BaseException {
        try {
            int result = userDao.modifyUserStatus(patchUserStatusReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERSTATUS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 스토어 회원탈퇴(Patch)
    @Transactional
    public void modifyStoreStatus(PatchStoreStatusReq patchStoreStatusReq) throws BaseException {
        try {
        int result = userDao.modifyStoreStatus(patchStoreStatusReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
        if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
            throw new BaseException(MODIFY_FAIL_STORESTATUS);
        }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
          throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원정보 수정(Patch)
    public void modifyUserInform(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserInform(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
}
