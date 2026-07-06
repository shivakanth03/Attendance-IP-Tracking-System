package com.attendance.qr;

import com.attendance.exception.QrCodeException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Service for QR Code generation and AES-256 payload encryption/decryption.
 * Every QR payload is AES-256-CBC encrypted to prevent forgery.
 */
@Slf4j
@Service
public class QrCodeService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";

    @Value("${app.qr.encryption-key}")
    private String encryptionKey;

    @Value("${app.qr.image-width:300}")
    private int imageWidth;

    @Value("${app.qr.image-height:300}")
    private int imageHeight;

    // ============================================================
    // QR Code Generation
    // ============================================================

    /**
     * Generate a QR code image as PNG bytes from a plaintext payload.
     * The payload is AES-256 encrypted before encoding.
     *
     * @param payload the plaintext session data (JSON string)
     * @return PNG image bytes
     */
    public byte[] generateQrCode(String payload) {
        try {
            String encrypted = encrypt(payload);
            return generateQrImage(encrypted);
        } catch (Exception e) {
            throw new QrCodeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a QR code image directly from a (possibly already encrypted) string.
     */
    public byte[] generateQrImage(String content) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 2);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(
                content, BarcodeFormat.QR_CODE, imageWidth, imageHeight, hints);

            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);

            log.debug("QR code generated successfully, size: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (WriterException | IOException e) {
            throw new QrCodeException("Failed to generate QR image: " + e.getMessage(), e);
        }
    }

    // ============================================================
    // AES-256 Encryption / Decryption
    // ============================================================

    /**
     * Encrypt plaintext using AES-256-CBC.
     *
     * @param plainText the data to encrypt
     * @return Base64-encoded ciphertext with IV prepended
     */
    public String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = getKeySpec();
            byte[] iv = generateIV();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to encrypted bytes
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
        } catch (Exception e) {
            throw new QrCodeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt AES-256-CBC ciphertext.
     *
     * @param cipherText Base64-encoded ciphertext with IV prepended
     * @return decrypted plaintext
     */
    public String decrypt(String cipherText) {
        try {
            byte[] combined = Base64.getUrlDecoder().decode(cipherText);

            // Extract IV (first 16 bytes)
            byte[] iv = Arrays.copyOf(combined, 16);
            byte[] encrypted = Arrays.copyOfRange(combined, 16, combined.length);

            SecretKeySpec keySpec = getKeySpec();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new QrCodeException("Decryption failed: Invalid or tampered QR code");
        }
    }

    // ============================================================
    // Helpers
    // ============================================================

    private SecretKeySpec getKeySpec() throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(encryptionKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, AES);
    }

    private byte[] generateIV() {
        byte[] iv = new byte[16];
        new java.security.SecureRandom().nextBytes(iv);
        return iv;
    }
}
