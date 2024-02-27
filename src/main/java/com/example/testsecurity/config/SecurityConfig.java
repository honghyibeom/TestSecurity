package com.example.testsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // 계층 권한 메소드
    public RoleHierarchy roleHierarchy() {

        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();

        hierarchy.setHierarchy("ROLE_C > ROLE_B\n" +
                "ROLE_B > ROLE_A");

        return hierarchy;
        // 적용할 때는 requestMatchers("/").hasAnyRole("")
        // hasAnyRole안에 낮은 낮은 순에서 높은 순으로 작성하면 됨 - A - B - C
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/loginProc", "/join", "/joinProc").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .requestMatchers("/my/**").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated()
                );


        http
                .formLogin((auth) -> auth.loginPage("/login")
                        .loginProcessingUrl("/loginProc")
                        .permitAll()
                );
//security config 클래스에서 csrf.disable() 설정을 진행하지 않으면 자동으로 enable 설정이 진행된다.
//enable 설정시 스프링 시큐리티는 CsrfFilter를 통해 POST, PUT, DELETE 요청에 대해서 토큰 검증을 진행한다.
//        http csrf
//                .csrf((auth) -> auth.disable());

        http //다중 로그인 설정로직
                .sessionManagement((auth) -> auth
                        .maximumSessions(1) // 하나의 아이디의 다중 로그인 갯수
                        .maxSessionsPreventsLogin(true)); // 윗값을 초과했을 경우 처리방법
        // true : 초과시 새로운 로그인 차단
        // false : 초과시 기존 세션 하나 삭제

        http //세션 고정 보호 설정
                .sessionManagement((auth) -> auth
                        .sessionFixation()
                        .changeSessionId());
        //- sessionManagement().sessionFixation().none() : 로그인 시 세션 정보 변경 안함
        //- sessionManagement().sessionFixation().newSession() : 로그인 시 세션 새로 생성
        //- sessionManagement().sessionFixation().changeSessionId() : 로그인 시 동일한 세션에 대한 id 변경

        http // get방식으로 로그아웃시 필요한 작업
                .logout((auth) -> auth.logoutUrl("/logout")
                        .logoutSuccessUrl("/"));

        return http.build();
    }
}