package com.odisha.floodrelief.dto.response;

import com.odisha.floodrelief.entity.enums.PopupType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomePopupResponse {
    private Long id;
    private String title;
    private String message;
    private PopupType type;
    private Boolean isActive;
    private Integer priority;
    private String createdByName;
    private LocalDateTime createdAt;
}
