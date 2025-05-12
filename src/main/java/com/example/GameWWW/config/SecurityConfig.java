package com.example.GameWWW.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig  {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Order(1)
    @Configuration
    public class WebConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private UserDetailsService uds;
        @Autowired
        private BCryptPasswordEncoder encoder;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {

            auth.userDetailsService(uds).passwordEncoder(encoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.authorizeRequests()
                    .antMatchers("/static/**").permitAll()
                    .antMatchers("/", "/home", "/registration", "total/saveUser").permitAll()
                    .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .antMatchers("/user/**").hasAuthority("ROLE_USER")
                    .anyRequest().authenticated()

                    .and()
                    .formLogin()
                    .defaultSuccessUrl("/main")
                    .permitAll()

                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

                    .and()
                    .exceptionHandling()
                    .accessDeniedPage("/accessDenied")
                    .and()
                    .httpBasic();

        }

        @Bean
        public UserDetailsService uds() {

            UserDetails user = User.builder()
                    .username("user")
                    .password(encoder.encode("user"))
                    .roles("USER")
                    .build();

            UserDetails admin = User.builder()
                    .username("admin")
                    .password(encoder.encode("admin"))
                    .roles("ADMIN")
                    .build();

            return new InMemoryUserDetailsManager(user, admin);
        }
    }
    @Order(2)
    @Configuration
    @RequiredArgsConstructor
    public static class RestConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/**");
        }
    }
}
