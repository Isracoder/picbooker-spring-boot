package com.example.picbooker.photographer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.picbooker.client.Client;
import com.example.picbooker.media.Media;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
import com.example.picbooker.socialLinks.SocialLink;
import com.example.picbooker.user.User;
import com.example.picbooker.workhours.WorkHour;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@SuperBuilder(toBuilder = true)
public class Photographer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;

    @Column
    @Default
    private Boolean enabledOnlinePayment = false;
    // @OneToOne(optional = true)
    // @JoinColumn(name = "profilePic_id", referencedColumnName = "id", unique =
    // true)
    // private Media profilePhoto;
    @Column(nullable = true)
    private String profilePhotoUrl;

    @Column
    private String personalName;

    @Column
    private String studio; // optional

    @Column
    private String bio; // optional

    @Default
    @Column
    private Integer bufferTimeMinutes = 15; // buffer time between each session and the next

    @Column
    @Default
    private Integer minimumNoticeBeforeSessionMinutes = 1440 * 2; // 2 days minimum
    // minimum notice time before booking a session in minutes ;

    @Column
    @Default
    private Integer cancellationStrikes = 0; // optional

    // reviews clients wrote about me
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Review> reviews = new ArrayList<>();

    // clients that set me as one of their favorites
    @Default
    @ManyToMany(mappedBy = "favoritePhotographers")
    private Set<Client> clients = new HashSet<>();

    // @JsonManagedReference
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL)
    private List<SocialLink> socialLinks;

    // sessions that i'm the photographer for
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Session> sessions = new ArrayList<>();

    // additional services that I offer
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<PhotographerAddOn> additionalServices = new ArrayList<>();

    // session types that I specialize in
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<PhotographerSessionType> sessionTypes = new ArrayList<>();

    // my workHours
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<WorkHour> workHours = new ArrayList<>();

    // my photos
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    List<Media> mediaUploads = new ArrayList<>();

}
