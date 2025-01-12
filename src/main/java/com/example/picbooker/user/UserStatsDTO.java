package com.example.picbooker.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private Long userId;
    private String username;
    private Integer mostSpyPoints;
    private Integer mostHunterPoints;
    private Integer games;
    // private Game lastGame;
}
