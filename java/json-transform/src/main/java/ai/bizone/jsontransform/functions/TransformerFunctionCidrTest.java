package ai.bizone.jsontransform.functions;

import ai.bizone.jsontransform.functions.common.*;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformerFunctionCidrTest extends TransformerFunction {
    public TransformerFunctionCidrTest() {
        super(FunctionDescription.of(
                Map.of(
                    "cidr", ArgumentType.of(ArgType.String).position(0)
                )
        ));
    }

    private record IpRanges(List<String> ipv4, List<String> ipv6) {}
    private static final Map<String, IpRanges> PREDEFINED;

    static {
        PREDEFINED = Map.of(
            "loopback", new IpRanges(
                List.of("127.0.0.0/8"), // RFC 3330
                List.of("::1/128") // RFC 2373
            ),
            "linklocal", new IpRanges(
                List.of("169.254.0.0/16"), // RFC 3927 (2.1)
                List.of("fe80::/10") // RFC 2462
            ),
            "multicast", new IpRanges(
                List.of("224.0.0.0/4"), // RFC 3171 (Class D)
                List.of("ff00::/8") // RFC 2373
            ),
            "unspecified", new IpRanges(
                List.of("0.0.0.0/8"),
                List.of("::0/128")
            ),
            "private", new IpRanges(
                List.of("10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16"), // RFC 1918
                List.of("fe80::/10", "fc00::/7")
            ),
            "reserved", new IpRanges(
                Arrays.asList( // RFCs 5735, 5737, 2544, 1700
                    "192.0.0.0/24",
                    "192.0.2.0/24",
                    "192.88.99.0/24",
                    "198.18.0.0/15",
                    "198.51.100.0/24",
                    "203.0.113.0/24",
                    "240.0.0.0/4" // (Class E)
                ),
                Arrays.asList(
                    "2001::/23", // RFC 3849
                    "2001:db8::/32" // RFC 2928
                )
            )
        );
    }

    /**
     * Converts an IPv4 address string to its 32-bit unsigned integer representation, stored in a Long.
     */
    private static Long toLong(String ipV4) {
        String[] octets = ipV4.split("\\.");
        if (octets.length != 4) {
            return null;
        }
        try {
            long result = 0L;
            result |= Long.parseLong(octets[0]) << 24;
            result |= Long.parseLong(octets[1]) << 16;
            result |= Long.parseLong(octets[2]) << 8;
            result |= Long.parseLong(octets[3]);
            return result & 0xFFFFFFFFL; // Mask to get the unsigned 32-bit value
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Tests if an IPv4 address is within a given CIDR subnet.
     */
    private static boolean testV4(String ipV4Address, String cidrSubnet) {
        try {
            String address;
            int netmask;

            if (cidrSubnet.contains("/")) {
                String[] cidr = cidrSubnet.split("/");
                if (cidr.length != 2) return false;
                address = cidr[0];
                try {
                    netmask = Integer.parseInt(cidr[1]);
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                address = cidrSubnet;
                netmask = 32;
            }

            if (netmask < 0 || netmask > 32) return false;

            Long networkLong = toLong(address);
            if (networkLong == null) return false;

            Long ipLong = toLong(ipV4Address);
            if (ipLong == null) return false;

            if (netmask == 0) return true;

            // A right shift isolates the network portion of the address for comparison.
            return (networkLong >> (32 - netmask)) == (ipLong >> (32 - netmask));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Converts an IPv6 address string to its 128-bit BigInteger representation.
     */
    private static BigInteger toBigInt(String ipV6) {
        String[] parts = ipV6.split("::", -1);
        if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid IPv6 address: multiple '::' segments found");
        }

        List<String> left = parts[0].isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(parts[0].split(":")));
        List<String> right = (parts.length < 2 || parts[1].isEmpty()) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(parts[1].split(":")));

        int numZeroBlocks = 8 - (left.size() + right.size());
        if (numZeroBlocks < 0) {
            throw new IllegalArgumentException("Invalid IPv6 address: too many segments");
        }

        var zeroBlocks = Collections.nCopies(numZeroBlocks, "0");

        var fullAddressBlocks = Stream.concat(left.stream(), Stream.concat(zeroBlocks.stream(), right.stream())).toList();

        var result = BigInteger.ZERO;
        for (String block : fullAddressBlocks) {
            if (!block.matches("^[0-9a-fA-F]{1,4}$")) {
                throw new IllegalArgumentException("Invalid IPv6 segment");
            }
            result = result.shiftLeft(16).or(new BigInteger(block, 16));
        }
        return result;
    }

    /**
     * Tests if an IPv6 address is within a given CIDR subnet.
     */
    private static boolean testV6(String ipV6Address, String cidrSubnet) {
        try {
            String[] parts = cidrSubnet.split("/", 2);
            if (parts.length != 2) return false;

            String networkStr = parts[0];
            int prefix = Integer.parseInt(parts[1]);
            if (prefix < 0 || prefix > 128) return false;
            if (prefix == 0) return true;

            BigInteger addressBigInt = toBigInt(ipV6Address);
            BigInteger networkBigInt = toBigInt(networkStr);

            // Create a 128-bit mask by shifting -1 (all ones) left.
            BigInteger mask = new BigInteger("-1").shiftLeft(128 - prefix);

            return addressBigInt.and(mask).equals(networkBigInt.and(mask));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object apply(FunctionContext context) {
        var ipAddress = context.getString(null);
        if (ipAddress == null || ipAddress.isEmpty()) {
            return false;
        }

        var cidrInput = context.getString("cidr");
        if (cidrInput == null || cidrInput.isEmpty()) {
            return false;
        }

        cidrInput = cidrInput.trim().toLowerCase();
        boolean isV6 = ipAddress.contains(":");

        List<String> cidrs;
        if (PREDEFINED.containsKey(cidrInput)) {
            IpRanges ranges = PREDEFINED.get(cidrInput);
            cidrs = isV6 ? ranges.ipv6 : ranges.ipv4;
        } else {
            // Split by comma and trim whitespace from each resulting string
            cidrs = Arrays.stream(cidrInput.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        if (isV6) {
            return cidrs.stream().anyMatch(cidr -> testV6(ipAddress, cidr));
        } else {
            return cidrs.stream().anyMatch(cidr -> testV4(ipAddress, cidr));
        }
    }
}
