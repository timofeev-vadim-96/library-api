package com.example.restapi.security.commonConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("security_common")
public class WebSecurityConfig{

    /**
     * Бин, позволяющий разграничить доступ в зависимости от прав
     * @param httpSecurity - такой бин уже существует под капотом спринга
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .authorizeHttpRequests(registry-> registry
                        .requestMatchers("/ui/issue/**").hasAuthority("manager")
                        .requestMatchers("/ui/reader/**", "/ui/issue/**").hasAuthority("admin") //конкретная роль
                        .requestMatchers("/ui/book/**").authenticated() //любой авторизованный
//                        .requestMatchers("/book/**", "/issue/**", "/reader/**").permitAll() //доступ всем
                        .anyRequest().permitAll() //для всех остальных ресурсов - запрет всем (необязательно)
                )
                .formLogin(Customizer.withDefaults()) //если не авторизовались по фильтрам выше - выдать форму авторизации
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
