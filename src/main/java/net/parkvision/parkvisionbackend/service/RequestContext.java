package net.parkvision.parkvisionbackend.service;

import net.parkvision.parkvisionbackend.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class RequestContext {
    public static User getUserFromRequest() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user instanceof User) {

            return (User) user;
        }
        return null;
    }
}
