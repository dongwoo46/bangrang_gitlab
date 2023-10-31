package com.ssafy.bangrang.global.security.login.handler;

import com.ssafy.bangrang.domain.member.entity.AppMember;
import com.ssafy.bangrang.domain.member.repository.AppMemberRepository;
import com.ssafy.bangrang.global.security.jwt.JwtService;
import com.ssafy.bangrang.global.security.redis.RedisRefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * JWT를 활용한 일반 로그인 성공 처리
 */
@RequiredArgsConstructor
public class AppLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final AppMemberRepository appMemberRepository;

    private final RedisRefreshTokenService redisRefreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException {

        // 인증 정보에서 username(id) 추출
        String id = extractUsername(authentication);

        // AccessToken & RefreshToken 발급
        String accessToken = jwtService.createAccessToken(id);
        String refreshToken = jwtService.createRefreshToken();

        httpServletResponse.addHeader(jwtService.getAccessHeader(), accessToken);
        httpServletResponse.addHeader(jwtService.getRefreshHeader(), refreshToken);

        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");

        // response header에 AccessToken, RefreshToken 실어서 보내기
//        jwtService.sendAccessAndRefreshToken(httpServletResponse, accessToken, refreshToken);

        AppMember user = appMemberRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("해당 유저는 존재하지 않습니다.", 1));

        if(user != null) {

            // 데이터를 JSON 형식으로 만듭니다.
            String json = "{\"userIdx\": \"" + user.getIdx() + "\", \"userNickname\": \"" + user.getNickname() + "\", \"userImage\": \"" + user.getImgUrl() + "\"," +
                    "\"userAlarm\": \"" + user.getAlarms() + "\"}";

            // Get the PrintWriter
            PrintWriter out = httpServletResponse.getWriter();
            // Write data to the response body
            out.println(json);
            // Close the PrintWriter
            out.close();

            // Redis에 RefreshToken 저장
            redisRefreshTokenService.setRedisRefreshToken(refreshToken, id);

//            if(user.getRole() == Role.FIRST) {
//                // 첫 로그인 이라는 역할 함께 넣어줌
//                httpServletResponse.addHeader("user_role", "first");
//
//                user.updateFirstRole();
//                webMemberRepository.save(user);
//            }
        }
        else
            throw new NullPointerException("해당 유저가 존재하지 않습니다.");
    }

    /**
     * Authentication(인증 정보)로부터 username(id) 추출하기
     */
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}