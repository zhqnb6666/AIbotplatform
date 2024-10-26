package com.aibotplatform.config;

import com.aibotplatform.model.Bot;
import com.aibotplatform.model.Conversation;
import com.aibotplatform.model.User;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.repository.ConversationRepository;
import com.aibotplatform.repository.UserRepository;
import com.aibotplatform.service.impl.UserServiceImpl;
import com.aibotplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

@Configuration
public class DatabaseInitializer {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    private final UserRepository userRepository;

    private final BotRepository botRepository;
    private final ConversationRepository conversationRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserServiceImpl userService;

    private final JwtUtil jwtUtil;

    public DatabaseInitializer(UserRepository userRepository, BotRepository botRepository, ConversationRepository conversationRepository, PasswordEncoder passwordEncoder, UserServiceImpl userService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.botRepository = botRepository;
        this.conversationRepository = conversationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public CommandLineRunner initializeDatabase() {
        return args -> {
            // 只在 ddl-auto = "create" 时执行初始化
            if ("create".equalsIgnoreCase(ddlAuto)) {
                initDatabase();
                generateAndPrintAdminJwt();
            } else {
                generateAndPrintAdminJwt();
                System.out.println("DDL-Auto is not set to 'create', skipping database initialization.");
            }
        };
    }


    public void initDatabase() {
        createAdmin();
        createOfficialBots();
        for (long i = 1; i < 15; i++) {
            createConversation(i);
        }
    }

    public void createAdmin() {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash(passwordEncoder.encode("admin"));
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setCreatedAt(Timestamp.from(Instant.now()));
        adminUser.setUpdatedAt(Timestamp.from(Instant.now()));
        adminUser.setCredits(BigDecimal.valueOf(1000000));
        adminUser.setToken(1000000L);
        userRepository.save(adminUser);
        System.out.println("Admin user created.");
    }

    public void createOfficialBots() {
        createAndSaveBot("GPT-3-5-TURBO", "An official chatbot powered by GPT-3.5.", "GPT_3_5_TURBO");
        createAndSaveBot("GPT-4-32K", "An official chatbot powered by GPT-4.", "GPT_4_32K");
        createAndSaveBot("GPT-4-O", "An official chatbot powered by GPT-4-O.", "GPT_4_O");
        createAndSaveBot("GPT-4-O-MINI", "An official chatbot powered by GPT-4-O-MINI.", "GPT_4_O_MINI");
        createAndSaveBot("ERNIE-Bot", "An official chatbot powered by ERNIE-Bot.", "ERNIE-Bot");
        createAndSaveBot("BLOOMZ-7B", "An official chatbot powered by BLOOMZ-7B.", "BLOOMZ-7B");
        createAndSaveBot("Llama-2-7b-chat", "An official chatbot powered by Llama-2-7B.", "Llama-2-7b-chat");
        createAndSaveBot("Llama-2-13b-chat", "An official chatbot powered by Llama-2-13B.", "Llama-2-13b-chat");
        createAndSaveBot("Llama-2-70b-chat", "An official chatbot powered by Llama-2-70B.", "Llama-2-70b-chat");
        createAndSaveBot("Chinese-Llama-2-7B", "An official chatbot powered by Chinese Llama-2-7B.", "Qianfan-Chinese-Llama-2-7B");
        createAndSaveBot("ChatGLM2-6B-32K", "An official chatbot powered by ChatGLM2-6B-32K.", "ChatGLM2-6B-32K");
        createAndSaveBot("AquilaChat-7B", "An official chatbot powered by AquilaChat-7B.", "AquilaChat-7B");
        createAndSaveBot("Stable-Diffusion-XL", "An official chat bot that generate images by Stable-Diffusion-XL", "Stable-Diffusion-XL");
        createAndSaveBot("Calculator-Bot", "An official chatbot that calculates precisely.", "Calculator-Bot");
        System.out.println("All official bots created.");
    }

    private void createAndSaveBot(String name, String description, String model) {
        Bot bot = new Bot();
        bot.setName(name);
        bot.setDescription(description);
        bot.setCreator(userRepository.findByUsername("admin"));
        bot.setType(Bot.BotType.OFFICIAL);
        bot.setModel(model);
        bot.setIsActive(true);
        bot.setTokenCost(1);
        bot.setDaily_limit(50);
        bot.setCreatedAt(Timestamp.from(Instant.now()));
        bot.setUpdatedAt(Timestamp.from(Instant.now()));
        botRepository.save(bot);
        System.out.println("Official bot " + name + " created.");
    }

    private void createConversation(Long botId) {
        Conversation conversation = new Conversation(null, userRepository.findByUsername("admin"), botRepository.findById(botId).orElse(null), "test conversation", Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), true);
        conversationRepository.save(conversation);
    }

    private void generateAndPrintAdminJwt() {
        UserDetails adminDetails = userService.loadUserByUsername("admin");
        String jwt = jwtUtil.generateToken(adminDetails);
        System.out.println("Generated JWT for admin: " + jwt);
    }

}
