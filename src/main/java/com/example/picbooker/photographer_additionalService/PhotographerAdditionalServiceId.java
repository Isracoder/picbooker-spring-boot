package com.example.picbooker.photographer_additionalService;

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
public class PhotographerAdditionalServiceId implements Serializable {

    @Column(name = "photographer_id")
    private Long photographerId;

    @Column(name = "additional_service_id")
    private Long additionalServiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PhotographerAdditionalServiceId))
            return false;
        PhotographerAdditionalServiceId photographerAdditionalServiceId = (PhotographerAdditionalServiceId) o;
        return Objects.equals(additionalServiceId, photographerAdditionalServiceId.additionalServiceId) &&
                Objects.equals(photographerId, photographerAdditionalServiceId.photographerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photographerId, additionalServiceId);
    }
}
