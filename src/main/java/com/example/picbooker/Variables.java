package com.example.picbooker;

import java.util.ArrayList;
import java.util.List;

import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.additionalService.AdditionalServiceType;
import com.example.picbooker.sessionType.SessionType;
import com.example.picbooker.sessionType.SessionTypeName;

public class Variables {

        public static ArrayList<SessionType> defaultSessionTypes = new ArrayList<>(List.of(
                        new SessionType(null, null, SessionTypeName.BABY_SHOWER,
                                        "Photography session for Baby Showers"),
                        new SessionType(null, null, SessionTypeName.BIRTHDAY, "Photography session for Birthdays"),
                        new SessionType(null, null, SessionTypeName.GRADUATION, "Photography session for Graduation"),
                        new SessionType(null, null, SessionTypeName.PERSONAL, "Photography for Personal sessions"),
                        new SessionType(null, null, SessionTypeName.PROFESSIONAL,
                                        "Photography for Professional/Marketing sessions"),
                        new SessionType(null, null, SessionTypeName.OTHER, "Photography for other types of sessions"),
                        new SessionType(null, null, SessionTypeName.WEDDING, "Photography session for Weddings")));

        public static ArrayList<AdditionalService> defaultAdditionalServices = new ArrayList<>(List.of(
                        new AdditionalService(null, null, AdditionalServiceType.MONTAGES,
                                        "Additional Service provided: Montages"),
                        new AdditionalService(null, null, AdditionalServiceType.PHOTO_ALBUM,
                                        "Additional Service provided: Photo Albums"),
                        new AdditionalService(null, null, AdditionalServiceType.PHOTO_EDITING,
                                        "Additional Service provided: Photo editing"),
                        new AdditionalService(null, null, AdditionalServiceType.PROPS,
                                        "Additional Service provided: Props with session"),
                        new AdditionalService(null, null, AdditionalServiceType.VIDEO_EDITING,
                                        "Additional Service provided: Video editing"),
                        new AdditionalService(null, null, AdditionalServiceType.OTHER,
                                        "Additional Service provided: other service specified by photographer/client")));

}
