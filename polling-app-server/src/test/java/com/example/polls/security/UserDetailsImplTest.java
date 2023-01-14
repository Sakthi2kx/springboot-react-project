package com.example.polls.security;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.example.polls.model.Poll;
import com.example.polls.model.User;


@SpringBootTest
class UserDetailsImplTest {
    static UserPrincipal userDetailsImpl;

    @BeforeAll
    static void setup() {
        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        userDetailsImpl = new UserPrincipal(1L,"name", "userName","email","password", authorities);
    }

    @Test
    void testGetId() {
        Long expectedUserId = 1L;
        assertEquals(expectedUserId, userDetailsImpl.getId());
    }

    @Test
    void testGetPassword() {
        assertEquals("password", userDetailsImpl.getPassword());
    }

    @Test
    void testGetName() {
        assertEquals("name", userDetailsImpl.getName());
    }

    @Test
    void testGetEmail() {
        assertEquals("email", userDetailsImpl.getEmail());
    }

    @Test
    void testGetUsername() {
        assertEquals("userName", userDetailsImpl.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertEquals(true, userDetailsImpl.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertEquals(true, userDetailsImpl.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertEquals(true, userDetailsImpl.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertEquals(true, userDetailsImpl.isEnabled());
    }

    @Test
    void testEquals() {
        assertEquals(true, userDetailsImpl.equals(userDetailsImpl));
        assertEquals(false, userDetailsImpl.equals(null));
        assertEquals(false, userDetailsImpl.equals(new Poll()));
        Object obj = new UserPrincipal();
        assertEquals(false, userDetailsImpl.equals(obj));
    }

    @Test
    void testGetAuthorities() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        assertEquals(authorities, userDetailsImpl.getAuthorities());
    }

    @Test
    void testBuild() {
        User user = new User();
        user.setId(1L);
        user.setName("userName");
        user.setPassword("password");
        user.setEmail("email");
        assertEquals(userDetailsImpl, UserPrincipal.create(user));
    }
}