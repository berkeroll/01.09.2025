package com.example.platform.Service;

import com.example.platform.Repository.RoleRepository;
import com.example.platform.Repository.UsersRepository;
import com.example.platform.model.Role;
import com.example.platform.model.Users;
import com.example.platform.util.JwtUtil;
import com.fasterxml.jackson.annotation.OptBoolean;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsersService implements UserDetailsService {
@Autowired
    private UsersRepository usersRepository;
@Autowired
    private JwtUtil jwtUtil;
@Autowired
    private RoleRepository roleRepository;
private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

//Kullanıcı Kaydı
public Users registerUser(String username, String rawpassword, List<String > roleNames)
{
    if (roleNames==null || roleNames.isEmpty())
    {
        roleNames=List.of("ROLE_USER");

    }

    Set<Role> roles = roleNames.stream()
            .map(name -> roleRepository.findByName(name)
                    .orElseThrow(() -> new RuntimeException("Rol bulunamadı: " + name)))
            .collect(Collectors.toSet());

    String encodedPassworod=passwordEncoder.encode(rawpassword);
Users user=new Users();
user.setUsername(username);
user.setPassword(encodedPassworod);
user.setRoles(roles);
return usersRepository.save(user);

}

public String loginAndGenerateTokenReact(String username,String rawpassword)
{
    Optional<Users> userOpt=usersRepository.findByUsername(username);

    if (userOpt.isPresent()&& passwordEncoder.matches(rawpassword,userOpt.get().getPassword()))
    {
        return jwtUtil.generateTokenReact(username);
    }
    return null;

}

    // Kullanıcı girişi
    public boolean checkLogin(String username, String rawPassword) {
        Optional<Users> userOpt = usersRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users user=usersRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Kullanıcı bulunamadı"+ username));
        return new  org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );

    }
}
