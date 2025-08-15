package com.sangsangplus.productservice.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class UuidValidator {
    
    // UUID 정규표현식 패턴 (표준 UUID 형식)
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    /**
     * 문자열이 유효한 UUID 형식인지 검증
     * @param uuidString 검증할 문자열
     * @return 유효한 UUID 형식이면 true, 그렇지 않으면 false
     */
    public static boolean isValidUuid(String uuidString) {
        if (uuidString == null || uuidString.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = uuidString.trim();
        
        // 정규표현식으로 먼저 검증
        if (!UUID_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        
        try {
            // UUID.fromString()으로 최종 검증
            UUID.fromString(trimmed);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 문자열을 UUID로 안전하게 파싱
     * @param uuidString 파싱할 문자열
     * @return 파싱된 UUID 또는 null (유효하지 않은 경우)
     */
    public static UUID parseUuid(String uuidString) {
        if (!isValidUuid(uuidString)) {
            return null;
        }
        
        try {
            return UUID.fromString(uuidString.trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 문자열을 UUID로 파싱하고 예외 발생 시 기본값 반환
     * @param uuidString 파싱할 문자열
     * @param defaultValue 파싱 실패 시 반환할 기본값
     * @return 파싱된 UUID 또는 기본값
     */
    public static UUID parseUuidOrDefault(String uuidString, UUID defaultValue) {
        UUID parsed = parseUuid(uuidString);
        return parsed != null ? parsed : defaultValue;
    }
}