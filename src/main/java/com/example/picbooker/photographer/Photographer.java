package com.example.picbooker.photographer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.picbooker.client.Client;
import com.example.picbooker.photo.Photo;
import com.example.picbooker.photographer_additionalService.PhotographerAdditionalService;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
import com.example.picbooker.user.User;
import com.example.picbooker.video.Video;
import com.example.picbooker.workhours.WorkHour;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
@SuperBuilder
public class Photographer extends User {

    @Column
    private String studio; // optional

    @Column
    private Integer bufferTimeMinutes; // buffer time between each sesion and the next

    @Column
    private Integer minimumNoticeBeforeSessionMinutes;
    // minimum notice time before booking a sesson in minutes ;

    // reviews clients wrote about me
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Review> reviews = new ArrayList<>();

    // clients that set me as one of their favorites
    @Default
    @ManyToMany(mappedBy = "favorite_photographers")
    private Set<Client> clients = new HashSet<>();

    // sessions that i'm the photographer for
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Session> sessions = new ArrayList<>();

    // additional services that I offer
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<PhotographerAdditionalService> additionalServices = new ArrayList<>();

    // session types that I specialize in
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<PhotographerSessionType> sessionTypes = new ArrayList<>();

    // my workhours
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<WorkHour> workhours = new ArrayList<>();

    // my photos
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Photo> photos = new ArrayList<>();

    // my videos
    @Default
    @OneToMany(mappedBy = "photographer", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Video> videos = new ArrayList<>();

}
