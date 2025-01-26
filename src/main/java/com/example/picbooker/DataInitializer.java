package com.example.picbooker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.additionalService.AdditionalServiceRepository;
import com.example.picbooker.sessionType.SessionType;
import com.example.picbooker.sessionType.SessionTypeRepository;

@Configuration
public class DataInitializer {

    @Autowired
    private SessionTypeRepository sessionTypeRepository;

    @Autowired
    private AdditionalServiceRepository additionalServiceRepository;

    // @Autowired
    // private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            initializeDefaultSessionTypes();
            initializeDefaultAdditionalServices();
        };
    }

    @Transactional
    public void initializeDefaultSessionTypes() {
        Variables.defaultSessionTypes.forEach(sessionType -> {
            if (!sessionTypeRepository.existsByType(sessionType.getType())) {

                SessionType st = SessionType.builder().type(sessionType.getType())
                        .description(sessionType.getDescription()).build();
                sessionTypeRepository.save(st);
            }
        });

    }

    @Transactional
    public void initializeDefaultAdditionalServices() {
        Variables.defaultAdditionalServices.forEach(additionalService -> {
            if (!additionalServiceRepository.existsByType(additionalService.getType())) {

                AdditionalService st = AdditionalService.builder().type(additionalService.getType())
                        .description(additionalService.getDescription()).build();
                additionalServiceRepository.save(st);
            }
        });

    }

    @Transactional
    private void loadRoles() {
        // RoleEnum[] roleNames = new RoleEnum[] { RoleEnum.ROLE_PLAYER,
        // RoleEnum.ROLE_ROOM_ADMIN };
        // Map<RoleEnum, String> roleDescriptionMap = Map.of(
        // RoleEnum.ROLE_PLAYER, "Default user role which is a player",
        // RoleEnum.ROLE_ROOM_ADMIN, "Player that is also the room admin"

        // );

        // Arrays.stream(roleNames).forEach((roleName) -> {
        // Optional<Role> optionalRole = roleRepository.findByName(roleName);

        // optionalRole.ifPresentOrElse(System.out::println, () -> {
        // Role roleToCreate = new Role(null, roleName,
        // roleDescriptionMap.get(roleName));

        // roleRepository.save(roleToCreate);
        // });
        // });
    }
}
