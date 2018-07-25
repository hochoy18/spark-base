package com.hochoy.HadoopDNSVerifier;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CheckRemote {
    public static void main(String[] args) throws UnknownHostException {

        args = new String[1];
        args[0] = "61.147.166.76";
        if (args.length < 1) {
            System.out.println("\nUsage:\n\njava io.pivotal.HadoopDNSVerifier.CheckRemote hostname.domain.com");
            System.exit(1);
        }

        InetAddress addr = InetAddress.getByName(args[0]);
        System.out.println(
                String.format(
                        "IP:  %s \nhostname:  %s \ncanonicalName:  %s", addr.getHostAddress(), addr.getHostName(), addr.getCanonicalHostName())
        );
    }
}
