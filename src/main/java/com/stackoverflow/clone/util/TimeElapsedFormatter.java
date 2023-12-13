package com.stackoverflow.clone.util;

import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;


public class TimeElapsedFormatter {
    public static String formatTimeElapsed(Date createdAt) {
        Instant now = Instant.now();
        Instant createdAtInstant = createdAt.toInstant();
        LocalDateTime createdAtDateTime = LocalDateTime.ofInstant(createdAtInstant, ZoneOffset.UTC);
        LocalDateTime nowDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);

        Duration duration = Duration.between(createdAtDateTime, nowDateTime);
        Period period = Period.between(createdAtDateTime.toLocalDate(), nowDateTime.toLocalDate());
        long days = duration.toDays();
        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        if (days > 0) {
            return days>1 ? days + " days ago" : days + " day ago";
        } else if (hours > 0) {
            return hours>1 ? hours + " hours ago" : hours + " hour ago";
        } else {
            return minutes>0 ? minutes + " minutes ago" : minutes + " minute ago";
        }
    }

    public static String formatTimeMember(Date createdAt) {
        Instant now = Instant.now();
        Instant createdAtInstant = createdAt.toInstant();
        LocalDateTime createdAtDateTime = LocalDateTime.ofInstant(createdAtInstant, ZoneOffset.UTC);
        LocalDateTime nowDateTime = LocalDateTime.ofInstant(now, ZoneOffset.UTC);

        Duration duration = Duration.between(createdAtDateTime, nowDateTime);
        Period period = Period.between(createdAtDateTime.toLocalDate(), nowDateTime.toLocalDate());

        int years = period.getYears();
        int months = period.getMonths();

        if (years > 0 && months > 0) {
            return "Member for " + years + " years, " + months + " months";
        } else if (years > 0) {
            return "Member for " + years + " years";
        } else if (months > 0) {
            return "Member for " + months + " months";
        } else {
            return formatTimeElapsed(createdAt);
        }
    }
}
