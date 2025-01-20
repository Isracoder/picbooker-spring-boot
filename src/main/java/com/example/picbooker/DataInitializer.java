package com.example.picbooker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    // @Autowired
    // private RealmKingdomRepository realmKingdomRepository;

    // @Autowired
    // private RoleRepository roleRepository;

    @Bean
    public CommandLineRunner loadData() {
        return args -> {
            // initializeDefaultKingdoms();
            // loadRoles();
        };
    }

    @Transactional
    public void initializeDefaultKingdoms() {
        // GameVariables.kingdoms.forEach(defaultKingdom -> {
        // if (!realmKingdomRepository.existsByName(defaultKingdom.getName())) {

        // RealmKingdom realmKingdom =
        // RealmKingdom.builder().name(defaultKingdom.getName())
        // .motto(defaultKingdom.getMotto())
        // .description(defaultKingdom.getDescription()).build();
        // realmKingdomRepository.save(realmKingdom);
        // }
        // });

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
