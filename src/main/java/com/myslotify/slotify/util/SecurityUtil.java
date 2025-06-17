package com.myslotify.slotify.util;

import com.myslotify.slotify.entity.Admin;
import com.myslotify.slotify.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static String extractEmail(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Admin admin) {
            return admin.getEmail();
        } else if (principal instanceof User user) {
            return user.getEmail();
        } else if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        } else if (principal instanceof String str) {
            return str;
        }
        return authentication.getName();
    }
}
