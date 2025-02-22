package com.example.picbooker.photographer_sessionType;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotographerSessionTypeId implements Serializable {

    @Column(name = "photographer_id")
    private Long photographerId;

    @Column(name = "session_type_id")
    private Long sessionTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhotographerSessionTypeId))
            return false;
        PhotographerSessionTypeId photographerSessionTypeId = (PhotographerSessionTypeId) o;
        return Objects.equals(photographerId, photographerSessionTypeId.photographerId) &&
                Objects.equals(sessionTypeId, photographerSessionTypeId.sessionTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photographerId, sessionTypeId);
    }
}
