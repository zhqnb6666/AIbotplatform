package com.aibotplatform.service.impl;

import com.aibotplatform.exception.ApiException;
import com.aibotplatform.model.Bot;
import com.aibotplatform.model.BotRating;
import com.aibotplatform.model.User;
import com.aibotplatform.model.UserFeedback;
import com.aibotplatform.repository.BotRatingRepository;
import com.aibotplatform.repository.BotRepository;
import com.aibotplatform.repository.TransactionRepository;
import com.aibotplatform.repository.UserRepository;
import com.aibotplatform.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    final private UserRepository userRepository;
    final private BotRepository botRepository;
    final private BotRatingRepository botRatingRepository;
    final private TransactionRepository transactionRepository;
    final private FeedbackServiceImpl feedbackService;

    // return the path of the Excel file
    @Override
    public String exportInfo() {
        String fileName = null;
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet totalInfoSheet = workbook.createSheet("total_info");

            Long totalUserCnt = userRepository.count();
            Long adminCnt = userRepository.countByRole(User.Role.ADMIN);

            writeRow(totalInfoSheet, 0, 0,
                    "total_user_num",
                    String.valueOf(totalUserCnt),
                    "regular_user_num",
                    String.valueOf(totalUserCnt - adminCnt),
                    "admin_num",
                    String.valueOf(adminCnt));

            Long totalBotCnt = botRepository.count();
            Long officialBotCnt = botRepository.countByType(Bot.BotType.OFFICIAL);

            writeRow(totalInfoSheet, 1, 0,
                    "total_bot_num",
                    String.valueOf(totalBotCnt),
                    "custom_bot_num",
                    String.valueOf(totalBotCnt - officialBotCnt),
                    "official_bot_num",
                    String.valueOf(officialBotCnt));

            BigDecimal totalRevenue = transactionRepository.sumByAmount();
            writeRow(totalInfoSheet, 2, 0,
                    "total_revenue",
                    totalRevenue.toString());

            totalInfoSheet.autoSizeColumn(0);
            totalInfoSheet.autoSizeColumn(2);
            totalInfoSheet.autoSizeColumn(4);

            Sheet userInfoSheet = workbook.createSheet("user_info");
            writeRow(userInfoSheet, 0, 0,
                    "user_id", "username", "email",
                    "role", "credits", "token", "bio",
                    "avg_rating", "total_feedback_num",
                    "feedback_id", "commenter_id",
                    "rating", "content");
            List<User> userList = userRepository.findAll();
            userList = userList.stream()
                    .sorted(Comparator.comparingLong(User::getUserId))
                    .toList();
            int nowRow = 1;
            for (User user : userList) {
                List<UserFeedback> feedbackList = feedbackService.getUserFeedback(user.getUserId());
                long totalRatingCnt = feedbackList.size();
                float avgRating;
                if (totalRatingCnt == 0) {
                    avgRating = 0f;
                } else {
                    long totalRating = 0;
                    for (UserFeedback userFeedback : feedbackList) {
                        totalRating += userFeedback.getRating();
                    }
                    avgRating = (1.0f * totalRating / totalRatingCnt);

                }
                writeRow(userInfoSheet, nowRow, 0,
                        String.valueOf(user.getUserId()),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().toString(),
                        user.getCredits().toString(),
                        String.valueOf(user.getToken()),
                        user.getBio(),
                        String.format("%.2f", avgRating),
                        String.valueOf(feedbackList.size()));

                for (int i = 0; i < feedbackList.size(); i++) {
                    UserFeedback fb = feedbackList.get(i);
                    writeRow(userInfoSheet, nowRow + i, 9,
                            fb.getFeedbackId().toString(),
                            fb.getCommenter().getUserId().toString(),
                            fb.getRating().toString(),
                            fb.getContent());
                }
                nowRow += Math.max(1, feedbackList.size());
            }
            for (int i = 0; i <= 12; i++) {
                userInfoSheet.autoSizeColumn(i);
            }

            Sheet botInfoSheet = workbook.createSheet("bot_info");
            writeRow(botInfoSheet, 0, 0,
                    "bot_id", "bot_name", "creator_id",
                    "type", "model", "daily_limit",
                    "token_cost", "description",
                    "avg_rating");
            List<Bot> botList = botRepository.findByIsActiveTrue();
            for (int i = 0; i < botList.size(); i++) {
                Bot bot = botList.get(i);
                List<BotRating> ratings = botRatingRepository.getBotRatingsByBot(bot);
                long ratingSum = 0;
                for (BotRating rating : ratings) {
                    ratingSum += rating.getRating();
                }
                Float avgRating = ratings.isEmpty() ?
                        0f : ratingSum * 1f / ratings.size();
                writeRow(botInfoSheet, 1 + i, 0,
                        bot.getBotId().toString(),
                        bot.getName(),
                        bot.getCreator().getUserId().toString(),
                        bot.getType().toString(),
                        bot.getModel(),
                        bot.getDaily_limit().toString(),
                        bot.getTokenCost().toString(),
                        bot.getDescription(),
                        avgRating.toString());
            }
            for (int i = 0; i <= 8; i++) {
                botInfoSheet.autoSizeColumn(i);
            }

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            fileName = "info_" + currentDateTime.format(formatter) + ".xlsx";

            try (FileOutputStream fileOut = new FileOutputStream("export/" + fileName)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            throw new ApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return fileName;
    }

    private static void writeRow(Sheet sheet, int rowIndex,int startColumn, String... data) {
        if (sheet.getRow(rowIndex) == null) {
            sheet.createRow(rowIndex);
        }
        Row row = sheet.getRow(rowIndex);
        Cell cell = null;
        for (int i = 0; i < data.length; i++) {
            cell = row.createCell(startColumn +i);
            cell.setCellValue(data[i]);
        }
    }

    public void rewardUser(Long userId, Long token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (token <= 0) {
            throw new ApiException("Token number should be positive", HttpStatus.BAD_REQUEST);
        }
        user.setToken(user.getToken() + token);
        userRepository.save(user);
    }
}
