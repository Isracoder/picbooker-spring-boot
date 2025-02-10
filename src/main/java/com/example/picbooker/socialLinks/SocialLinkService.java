package com.example.picbooker.socialLinks;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;

@Service
public class SocialLinkService {

    @Autowired
    private SocialLinkRepository socialLinkRepository;

    public SocialLink create(Photographer photographer, Platform platform, String linkUrl) {
        return new SocialLink(null, photographer, linkUrl, platform);
    }

    public Optional<SocialLink> findById(Long id) {
        return socialLinkRepository.findById(id);
    }

    public SocialLink findByIdThrow(Long id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Social Link not found"));
    }

    public SocialLink save(SocialLink socialLink) {
        return socialLinkRepository.save(socialLink);
    }

    public void findForPhotographer() {
    }

    public void updateForPhotographer() {
    }
}
