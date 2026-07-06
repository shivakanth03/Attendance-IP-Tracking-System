package com.attendance.network;

import com.attendance.exception.NetworkValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * Service for validating whether a request originates from the approved campus network.
 *
 * <p>Supports CIDR notation (e.g., 192.168.1.0/24).
 * Configured via {@code app.network.allowed-subnets} property.
 */
@Slf4j
@Service
public class NetworkValidationService {

    @Value("${app.network.allowed-subnets:127.0.0.1/32}")
    private String allowedSubnets;

    @Value("${app.network.strict-mode:false}")
    private boolean strictMode;

    /**
     * Validates that the client IP address is within the allowed subnet range.
     * In non-strict mode (dev), all IPs are allowed.
     *
     * @param request the HTTP request
     * @throws NetworkValidationException if the IP is outside the allowed network
     */
    public void validateClientNetwork(HttpServletRequest request) {
        String clientIp = extractClientIp(request);
        log.debug("Validating network for IP: {}", clientIp);

        if (!strictMode) {
            log.debug("Strict mode disabled — skipping network validation for IP: {}", clientIp);
            return;
        }

        if (!isIpAllowed(clientIp)) {
            log.warn("Network access blocked for IP: {}", clientIp);
            throw new NetworkValidationException(
                "Attendance can only be marked from the campus network. " +
                "Your IP (" + clientIp + ") is not in the allowed range.");
        }

        log.debug("IP {} passed network validation", clientIp);
    }

    /**
     * Check if a given IP is within any of the allowed subnets.
     */
    public boolean isIpAllowed(String ipAddress) {
        List<String> subnets = Arrays.asList(allowedSubnets.split(","));
        return subnets.stream()
            .map(String::trim)
            .anyMatch(subnet -> isIpInSubnet(ipAddress, subnet));
    }

    /**
     * Extract the real client IP address, handling proxies and load balancers.
     */
    public String extractClientIp(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For may contain multiple IPs; take the first (real client)
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * Get the hostname for a given IP address.
     */
    public String getHostname(String ipAddress) {
        try {
            InetAddress addr = InetAddress.getByName(ipAddress);
            return addr.getHostName();
        } catch (UnknownHostException e) {
            return ipAddress;
        }
    }

    /**
     * Get the network ID from an IP and subnet mask.
     */
    public String getNetworkInfo(String ipAddress) {
        try {
            InetAddress addr = InetAddress.getByName(ipAddress);
            return addr.getHostAddress() + " / " + addr.getHostName();
        } catch (UnknownHostException e) {
            return ipAddress;
        }
    }

    // ============================================================
    // CIDR Subnet Matching
    // ============================================================

    private boolean isIpInSubnet(String ipAddress, String subnet) {
        try {
            if (!subnet.contains("/")) {
                // Plain IP match
                return ipAddress.equals(subnet);
            }

            String[] parts = subnet.split("/");
            String subnetIp = parts[0];
            int prefixLength = Integer.parseInt(parts[1]);

            byte[] ipBytes = InetAddress.getByName(ipAddress).getAddress();
            byte[] subnetBytes = InetAddress.getByName(subnetIp).getAddress();

            if (ipBytes.length != subnetBytes.length) {
                return false;
            }

            int fullBytes = prefixLength / 8;
            int remainingBits = prefixLength % 8;

            // Check full bytes
            for (int i = 0; i < fullBytes; i++) {
                if (ipBytes[i] != subnetBytes[i]) {
                    return false;
                }
            }

            // Check remaining bits
            if (remainingBits > 0 && fullBytes < ipBytes.length) {
                int mask = 0xFF & (0xFF << (8 - remainingBits));
                return (ipBytes[fullBytes] & mask) == (subnetBytes[fullBytes] & mask);
            }

            return true;
        } catch (UnknownHostException | NumberFormatException e) {
            log.warn("Invalid subnet format: {}", subnet);
            return false;
        }
    }
}
