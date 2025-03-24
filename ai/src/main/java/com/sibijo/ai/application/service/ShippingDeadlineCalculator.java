package com.sibijo.ai.application.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class ShippingDeadlineCalculator {

    /**
     * 주어진 요청 정보(requestInfo)와 예상 소요시간(expectedDurationMinutes, 분 단위)를 이용하여 최종 발송 시한을 계산합니다.
     * 예시: "12월 12일 3시까지 보내주세요!"에서 예상 소요시간이 60분이면, 최종 발송 시한은 요청시간에서 60분을 차감한 결과가 됩니다.
     *
     * @param requestInfo 주문 요청 시간 정보 (예: "12월 12일 3시까지 보내주세요!")
     * @param expectedDurationMinutes 예상 소요시간(분 단위)
     * @return 계산된 최종 발송 시한 문자열 (예: "12월 12일 오후 2시")
     */
    public static String computeFinalShippingDeadline(String requestInfo, int expectedDurationMinutes) {
        Pattern pattern = Pattern.compile("(\\d{1,2})월\\s*(\\d{1,2})일\\s*(\\d{1,2})시");
        Matcher matcher = pattern.matcher(requestInfo);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group(1));
            int day = Integer.parseInt(matcher.group(2));
            int hour = Integer.parseInt(matcher.group(3));

            // 연도는 현재 연도로 가정
            int year = LocalDate.now().getYear();
            LocalDateTime requestedDeadline = LocalDateTime.of(year, month, day, hour, 0);

            // 요청된 시간에서 예상 소요시간을 차감하여 최종 발송 시한 계산
            LocalDateTime finalDeadline = requestedDeadline.minusMinutes(expectedDurationMinutes);

            // 예: "M월 d일 a h시" 형식 (예: "12월 12일 오후 2시")
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 a h시");
            return finalDeadline.format(formatter);
        } else {
            throw new IllegalArgumentException("요청 사항에서 날짜/시간 정보를 추출할 수 없습니다: " + requestInfo);
        }
    }
}