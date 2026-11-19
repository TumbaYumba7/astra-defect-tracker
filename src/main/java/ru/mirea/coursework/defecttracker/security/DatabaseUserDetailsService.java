package ru.mirea.coursework.defecttracker.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mirea.coursework.defecttracker.domain.port.UserStore;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserStore users;
    public DatabaseUserDetailsService(UserStore users) { this.users = users; }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = users.findByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        return User.withUsername(user.username()).password(user.passwordHash()).roles(user.role().name()).build();
    }
}
