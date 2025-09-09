import TransformerFunction from "./common/TransformerFunction";
import { ArgType } from "./common/ArgType";
import FunctionContext from "./common/FunctionContext";
import { add } from "date-fns/add";

class TransformerFunctionCidrTest extends TransformerFunction {
  constructor() {
    super({
      argsSet: [{ name: "cidr", type: ArgType.String }],
    });
  }

  private static PREDEFINED: Record<string, { ipv4: string[]; ipv6: string[] }> = {
    loopback: {
      ipv4: ["127.0.0.0/8"],
      ipv6: ["::1/128"],
    },
    linklocal: {
      ipv4: ["169.254.0.0/16"], // RFC 3927 (2.1)
      ipv6: ["fe80::/10"], // RFC 2462
    },
    multicast: {
      ipv4: ["224.0.0.0/4"], // RFC 3171 (Class D)
      ipv6: ["ff00::/8"], // RFC 2373
    },
    unspecified: {
      ipv4: ["0.0.0.0/8"],
      ipv6: ["::0/128"],
    },
    private: {
      ipv4: ["10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16"], // RFC 1918
      ipv6: ["fe80::/10", "fc00::/7"],
    },
    reserved: {
      ipv4: [
        // RFCs 5735, 5737, 2544, 1700
        "192.0.0.0/24",
        "192.0.2.0/24",
        "192.88.99.0/24",
        "198.18.0.0/15",
        "198.51.100.0/24",
        "203.0.113.0/24",
        "240.0.0.0/4", // (Class E)
      ],
      ipv6: [
        "2001::/23", // RFC 3849
        "2001:db8::/32", // RFC 2928
      ],
    },
  };

  private static toLong(ipV4: string): number | null {
    const octets = ipV4.split(".");
    return octets.length != 4
      ? null
      : ((parseInt(octets[0], 10) << 24) +
          (parseInt(octets[1], 10) << 16) +
          (parseInt(octets[2], 10) << 8) +
          parseInt(octets[3], 10)) >>>
          0;
  }

  private static testV4(ipV4Address: string, cidrSubnet: string): boolean {
    try {
      let address: string;
      let netmask: number;
      if (cidrSubnet.includes("/")) {
        const cidr = cidrSubnet.split("/");
        if (cidr.length !== 2) return false;
        address = cidr[0];
        netmask = parseInt(cidr[1]);
      } else {
        address = cidrSubnet;
        netmask = 32;
      }
      if (netmask < 0 || netmask > 32) return false;

      const l = TransformerFunctionCidrTest.toLong(address);
      if (l === null) {
        return false;
      }
      const t = TransformerFunctionCidrTest.toLong(ipV4Address);
      if (t === null) {
        return false;
      }
      return netmask === 0 ? true : l >> (32 - netmask) === t >> (32 - netmask);
    } catch (error) {
      return false;
    }
  }

  private static toBigInt(ipV6: string): bigint {
    // 1. Handle the '::' expansion for compressed zeros.
    const parts = ipV6.split("::");
    if (parts.length > 2) {
      throw new Error(`Invalid IPv6 address: multiple '::' segments found.`);
    }

    const leftPart = parts[0] ? parts[0].split(":") : [];
    const rightPart = parts.length === 2 && parts[1] ? parts[1].split(":") : [];

    // If the left part from a split is just an empty string (from '::...')
    if (leftPart.length === 1 && leftPart[0] === "") {
      leftPart.shift();
    }
    // If the right part from a split is just an empty string (from '...::')
    if (rightPart.length === 1 && rightPart[0] === "") {
      rightPart.pop();
    }

    const numZeroBlocks = 8 - (leftPart.length + rightPart.length);
    if (numZeroBlocks < 0) {
      throw new Error(`Invalid IPv6 address: too many segments.`);
    }

    const zeroBlocks = Array(numZeroBlocks).fill("0");

    // 2. Reconstruct the full 8-block address array.
    const fullAddressBlocks = [...leftPart, ...zeroBlocks, ...rightPart];

    // 3. Convert the 8 blocks of 16-bit hex strings to a single 128-bit BigInt.
    let result = 0n;
    for (const block of fullAddressBlocks) {
      if (!/^[0-9a-fA-F]{1,4}$/.test(block)) {
        throw new Error(`Invalid IPv6 segment`);
      }
      const value = BigInt(parseInt(block, 16));
      result = (result << 16n) | value;
    }

    return result;
  }

  private static testV6(ipV6Address: string, cidrSubnet: string): boolean {
    try {
      const [networkStr, prefixStr] = cidrSubnet.split("/", 2);
      if (!networkStr || !prefixStr) return false;

      const prefix = parseInt(prefixStr, 10);
      if (isNaN(prefix) || prefix < 0 || prefix > 128) return false;
      if (prefix === 0) return true;

      const addressBigInt = TransformerFunctionCidrTest.toBigInt(ipV6Address);
      const networkBigInt = TransformerFunctionCidrTest.toBigInt(networkStr);

      // Create a 128-bit mask by shifting -1 (all ones) left.
      const mask = ((1n << BigInt(prefix)) - 1n) << BigInt(128 - prefix);

      return (addressBigInt & mask) === (networkBigInt & mask);
    } catch (error) {
      return false;
    }
  }

  override async apply(context: FunctionContext): Promise<any> {
    const ipAddress = await context.getString(null);
    if (ipAddress == null) {
      return false;
    }
    let ipsString = await context.getString("cidr");
    if (ipsString == null) {
      return false;
    }
    ipsString = ipsString.trim().toLowerCase();
    const isV6 = ipAddress.includes(":");
    const ips = !TransformerFunctionCidrTest.PREDEFINED[ipsString]
      ? ipsString.split(",").map(ip => ip.trim())
      : isV6
        ? TransformerFunctionCidrTest.PREDEFINED[ipsString].ipv6
        : TransformerFunctionCidrTest.PREDEFINED[ipsString].ipv4;

    return ips.some(
      isV6
        ? cidr => TransformerFunctionCidrTest.testV6(ipAddress, cidr)
        : cidr => TransformerFunctionCidrTest.testV4(ipAddress, cidr),
    );
  }
}

export default TransformerFunctionCidrTest;
