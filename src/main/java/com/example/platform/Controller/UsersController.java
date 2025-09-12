package com.example.platform.Controller;

import com.example.platform.Service.UsersService;
import com.example.platform.dto.UsersDto;
import com.example.platform.model.Users;
import com.example.platform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000") // React portu
public class UsersController {

    private final UsersService usersService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    // Kayıt endpointi
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsersDto body) {
        String username = body.getUsername();
        String password = body.getPassword();
        List<String > roles=body.getRoles();

        Users savedUser = usersService.registerUser(username, password,roles);
        return ResponseEntity.ok(Map.of("message", "Kullanıcı kaydedildi"));
    }

   //Login Endpointi
    @PreAuthorize("permitAll()")
    @PostMapping("/logintoken2")
    public Map<String,String > loginToken2(@RequestBody UsersDto usersDto)
    {
        String username=usersDto.getUsername();
        String password=usersDto.getPassword();

        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
        List<String> roles=authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        String token=jwtUtil.generateToken(username,roles);
        return  Map.of("token",token);
    }



    @GetMapping("/admin-dashboard")
    public ResponseEntity<String> adminDashboard(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Admin dashboarda hoş geldiniz: " + authentication.getName());
    }

    @GetMapping("/sidebar-dashboard")
    public ResponseEntity<String> sidebarDashboard(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Sidebar dashboarda hoş geldiniz: " + authentication.getName());
    }



    @GetMapping("/admin-dashboard/user")
    public ResponseEntity<String> platformYatirimciDashboard(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Platform Yatırımcı kaydetme sistemine hoş geldiniz: " + authentication.getName());
    }
    @GetMapping("/admin-dashboard/investor")
    public ResponseEntity<String> platformInvestorDashboard(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Platform Yatırımcı görüntüleme ve düzenleme sistemine hoş geldiniz: " + authentication.getName());
    }

    @GetMapping("/admin-dashboard/crypto")
    public ResponseEntity<String> cryptoListandEditing(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Platform Yatırımcı görüntüleme ve düzenleme sistemine hoş geldiniz: " + authentication.getName());
    }

    @GetMapping("/admin-dashboard/platformcrypto")
    public ResponseEntity<String> addPlatformCrypto(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return ResponseEntity.status(403).body("Bu sayfaya erişim izniniz yok!");
        }

        return ResponseEntity.ok("Platforma kripto ekleme sistemine hoş geldiniz: " + authentication.getName());
    }


}
