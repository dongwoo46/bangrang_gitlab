package com.ssafy.bangrang.global.security.jwt;

import com.ssafy.bangrang.domain.member.entity.Member;
import com.ssafy.bangrang.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.ssafy.bangrang.global.security.redis.RedisRefreshTokenService;

/**
 * JWT 인증 필터
 * /login 이외의 API 요청 처리 필터
 *
 * AccessToken 만료 X => AccessToken만 헤더에 담아서 요청
 * AccessToken 만료 => RefreshToken, AccessToken 모두 헤더에 담아서 요청
 *
 * 1. RefreshToken X && AccessToken OK => 인증 성공, RefreshToken 재발급 X
 * 2. RefreshToken X && AccessToken X or InValid => 인증 실패, 403 ERROR
 * 3. RefreshToken O => 인증 실패 처리, Redis의 RefreshToken과 비교하여 일치 하면 AccessToken / RefreshToken 재발급 (RTR 방식)
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 제외해야하는 api 요청 (일반 로그인_웹)

    private final JwtService jwtService;

    private final MemberRepository memberRepository;

    private final RedisRefreshTokenService redisRefreshTokenService;

    private GrantedAuthoritiesMapper grantedAuthoritiesMapper = new NullAuthoritiesMapper();
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
  
        // token 검증이 필요없는 요청일 때
        if(httpServletRequest.getRequestURI().equals("/api/member/login") ||
                httpServletRequest.getRequestURI().equals("/api/web/login") ||
                httpServletRequest.getRequestURI().equals("/api/web/signup") ||
                httpServletRequest.getRequestURI().contains("/api/member/nicknameCheck") ||
                httpServletRequest.getRequestURI().contains("/api/web/idCheck")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            // 더 이상 필터를 진행하지 않고 return!
            return;
        }

        // 검증이 필요한 요청은 진행

        if(httpServletRequest.getRequestURI().equals("/api/refresh")) {
            // 요청 헤더에서 RefreshToken 추출 - 없거나 유효하지 않으면 null 반환
            String refreshToken = jwtService.extractRefreshToken(httpServletRequest)
                    .filter(jwtService::isTokenValid)
                    .orElse(null);
            // 요청 헤더에 RefreshToken이 존재한다면
            if(refreshToken != null)
                // 헤더의 RefreshToken과 Redis의 RefreshToken 비교 => 일치한다면 AccessToken 재발급
                checkRefreshTokenAndReIssueAccessToken(httpServletResponse, refreshToken);
        }

        else
            // AccessToken 검사 및 인증 처리
            // AccessToken이 존재하지 않거나 유효하지 않다면 => 인증 객체가 담기지 않은 상태로 인증 실패(403)
            // AccessToken이 유효하다면 => 인증 객체가 담긴 상태로 인증 성공
            checkAccessTokenAndAuthentication(httpServletRequest, httpServletResponse, filterChain);
    }
    /**
     * AccessToken / RefreshToken 생성 및 재발급 메서드
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse httpServletResponse, String refreshToken) {
        // 추후 access token으로 유효한지 판단해보기
        String redisEmail = redisRefreshTokenService.getRedisEmail(refreshToken);

        if(redisEmail != null) {
            Optional<Member> user = memberRepository.findById(redisEmail);

            if(user.isPresent()) {
                String reIssuedRefreshToken = reIssueRefreshToken(user.get());

                jwtService.sendAccessAndRefreshToken(
                        httpServletResponse,
                        jwtService.createAccessToken(user.get().getId()),
                        reIssuedRefreshToken
                );
            }
        }
        else
            throw new NullPointerException("Redis에 해당 RefreshToken이 존재하지 않습니다.");
    }
    /**
     * RefreshToken 재발급 메서드
     */
    public String reIssueRefreshToken(Member user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();

        redisRefreshTokenService.setRedisRefreshToken(reIssuedRefreshToken, user.getId());

        return reIssuedRefreshToken;
    }

    /**
     * AccessToken 검증 및 인증 처리 메서드
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                                  FilterChain filterChain)
            throws ServletException, IOException {

        // AccessToken 추출
        jwtService.extractAccessToken(httpServletRequest)
                .ifPresent(accessToken -> {
                    if (jwtService.isTokenValid(accessToken)) {
                        // AccessToken이 유효한 경우 email(Claim) 추출
                        jwtService.extractEmail(accessToken)
                                .ifPresent(id -> memberRepository.findById(id)
                                        .ifPresent(this::saveAuthentication)); // 인증 처리
                    } else {
                        // AccessToken이 유효하지 않은 경우, 예외를 던집니다.
                        throw new RuntimeException("해당 토큰은 유효하지 않습니다.");
                    }
                });


        // 필터에서의 처리를 마치고 다음 필터 또는 서블릿으로 요청을 전달하는 역할, 필터 체인의 다음 단계에서 추가적인 처리가 가능
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
    /**
     * 인증 허가 메서드
     * Parameter의 User : 우리가 만든 User 객체
     * Builder의 User : UserDetails의 User 객체
     */
    public void saveAuthentication(Member myUser) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getId())
                .password(myUser.getPassword())
//                .roles(myUser.getRole().name())
                .build();

        // 인증 객체 생성
        // UsernamePasswordAuthenticationToken의 파라미터
        // 1. UserDetails 객체 (유저 정보)
        // 2. credential (보통 비밀번호, 인증 시에는 보통 null 표시)
        // 3. Collection<? extends GrantedAuthority>
        // - UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
        // - new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                grantedAuthoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        // SecurityContext를 꺼낸 후 setAuthentication()을 이용하여 Authentication 인증 객체 인증 허가 처리
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
