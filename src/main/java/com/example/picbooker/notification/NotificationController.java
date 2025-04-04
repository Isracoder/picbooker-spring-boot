package com.example.picbooker.notification;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.user.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

        @Autowired
        private NotificationService notificationService;

        // to do paginate
        @GetMapping("/messages")
        public ApiResponse<List<NotificationDTO>> getMessageHistory(
                        @PageableDefault Pageable pageable) {
                List<NotificationDTO> messages = notificationService.getLastNotificationsForUser(
                                UserService.getLoggedInUserThrow().getId(), pageable);
                return ApiResponse.<List<NotificationDTO>>builder()
                                .content(messages)
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/{notificationId}/read")
        public ApiResponse<Map<String, String>> markRead(@PathVariable("notificationId") Long notificationId) {
                // send notification room id
                notificationService.markAsRead(UserService.getLoggedInUserThrow().getId(), notificationId);
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();

        }

        @PutMapping("/read")
        public ApiResponse<Map<String, String>> markAllRead() {
                // send notification room id
                notificationService.markAllAsRead(UserService.getLoggedInUserThrow().getId());
                return ApiResponse.<Map<String, String>>builder()
                                .content(Map.of("status", "Success"))
                                .status(HttpStatus.OK)
                                .build();
        }

        @GetMapping("/{notificationRoomId}/unread")
        public ApiResponse<List<NotificationDTO>> getUnreadMessages(
                        @PathVariable("notificationRoomId") Long notificationRoomId) {
                List<NotificationDTO> messages = notificationService.getUnreadNotifications(
                                UserService.getLoggedInUserThrow().getId());
                return ApiResponse.<List<NotificationDTO>>builder()
                                .content(messages)
                                .status(HttpStatus.OK)
                                .build();

        }

}