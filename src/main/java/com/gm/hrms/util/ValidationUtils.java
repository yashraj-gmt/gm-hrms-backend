package com.gm.hrms.util;

import com.gm.hrms.dto.request.BankDetailsRequestDTO;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.EmploymentType;
import com.gm.hrms.enums.InternShipType;
import com.gm.hrms.exception.InvalidRequestException;

import java.util.regex.Pattern;

public class ValidationUtils {

    private ValidationUtils() {}

    // =====================================================
    // ================= STRING =============================
    // =====================================================

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static void requireNonBlank(String value, String message) {
        if (isBlank(value)) {
            throw new InvalidRequestException(message);
        }
    }

    // =====================================================
    // ================= EMAIL ==============================
    // =====================================================

//    public static void validateOfficeEmail(String email, EmploymentType type) {
//
//        boolean isIntern = type == EmploymentType.INTERN;
//
//        if (!isIntern && isBlank(email)) {
//            throw new InvalidRequestException("Office email is required");
//        }
//    }

    // =====================================================
    // ================= BANK ===============================
    // =====================================================

    public static void validateBankIfRequired(
            PersonalInformation person,
            BankDetailsRequestDTO dto,
            Intern intern
    ) {

        if (person == null) return;

        EmploymentType type = person.getEmploymentType();

        boolean isMandatory = false;

        // EMPLOYEE
        if (type == EmploymentType.EMPLOYEE) {
            isMandatory = true;
        }

        // TRAINEE
        if (type == EmploymentType.TRAINEE) {
            isMandatory = true;
        }

        // INTERN (PAID)
        if (type == EmploymentType.INTERN &&
                intern != null &&
                intern.getInternshipDetails() != null &&
                intern.getInternshipDetails().getInternshipType() == InternShipType.PAID) {

            isMandatory = true;
        }

        // FINAL CHECK
        if (isMandatory) {

            if (dto == null ||
                    isBlank(dto.getBankName()) ||
                    isBlank(dto.getAccountNumber()) ||
                    isBlank(dto.getIfscCode())) {

                throw new InvalidRequestException(
                        "Bank details (Bank Name, Account Number, IFSC) are mandatory"
                );
            }
        }
    }

    // =====================================================
    // ================= GENERIC ============================
    // =====================================================

    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new InvalidRequestException(message);
        }
    }

    // ── Phone patterns ─────────────────────────────────────────────────────────
    /** Indian mobile: 10 digits, starting with 6-9 */
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[6-9]\\d{9}$");

    /** Generic 10-digit numeric */
    private static final Pattern PHONE_NUMERIC_10 =
            Pattern.compile("^\\d{10}$");

    // ── Email patterns ─────────────────────────────────────────────────────────
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ── PAN / Aadhaar ──────────────────────────────────────────────────────────
    private static final Pattern PAN_PATTERN     = Pattern.compile("^[A-Z]{5}\\d{4}[A-Z]$");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^\\d{12}$");

    // ── Office e-mail domain per employment type ───────────────────────────────
    private static final Pattern OFFICE_EMAIL_GENERIC =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ═══════════════════════════════════════════════════════════════════════════
    // Phone
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Validates an Indian mobile number (10 digits, starts with 6-9).
     *
     * @param phone     raw input (may contain spaces / dashes)
     * @param fieldName used in the error message
     */
    public static void validatePhone(String phone, String fieldName) {
        if (phone == null || phone.isBlank())
            throw new InvalidRequestException(fieldName + " is required");

        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        if (!PHONE_PATTERN.matcher(cleaned).matches())
            throw new InvalidRequestException(
                    fieldName + " must be a valid 10-digit Indian mobile number (starting with 6-9)");
    }

    /**
     * Validates any generic 10-digit phone number (without country-specific prefix check).
     */
    public static void validatePhone10(String phone, String fieldName) {
        if (phone == null || phone.isBlank())
            throw new InvalidRequestException(fieldName + " is required");

        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        if (!PHONE_NUMERIC_10.matcher(cleaned).matches())
            throw new InvalidRequestException(
                    fieldName + " must be exactly 10 digits");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Email
    // ═══════════════════════════════════════════════════════════════════════════

    public static void validateEmail(String email, String fieldName) {
        if (email == null || email.isBlank())
            throw new InvalidRequestException(fieldName + " is required");
        if (!EMAIL_PATTERN.matcher(email).matches())
            throw new InvalidRequestException(fieldName + " is not a valid email address");
    }

    /**
     * Validates office email format.
     * Can be extended to enforce domain rules per employment type.
     */
    public static void validateOfficeEmail(String email, EmploymentType type) {
        if (email == null || email.isBlank()) return;   // office email is optional for some types
        if (!OFFICE_EMAIL_GENERIC.matcher(email).matches())
            throw new InvalidRequestException("Office email is not a valid email address");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // PAN / Aadhaar
    // ═══════════════════════════════════════════════════════════════════════════

    public static void validatePan(String pan) {
        if (pan == null || pan.isBlank()) return;
        if (!PAN_PATTERN.matcher(pan.toUpperCase()).matches())
            throw new InvalidRequestException("PAN number format is invalid (e.g. ABCDE1234F)");
    }

    public static void validateAadhaar(String aadhaar) {
        if (aadhaar == null || aadhaar.isBlank()) return;
        String cleaned = aadhaar.replaceAll("\\s", "");
        if (!AADHAAR_PATTERN.matcher(cleaned).matches())
            throw new InvalidRequestException("Aadhaar number must be exactly 12 digits");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Numeric fields
    // ═══════════════════════════════════════════════════════════════════════════

    public static void validatePositiveNumber(Number value, String fieldName) {
        if (value == null) return;
        if (value.doubleValue() <= 0)
            throw new InvalidRequestException(fieldName + " must be a positive number");
    }

    public static void validateNonNegativeInteger(Integer value, String fieldName) {
        if (value == null) return;
        if (value < 0)
            throw new InvalidRequestException(fieldName + " cannot be negative");
    }
}