package pacote;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;

public class exemplo {

    public static void main(String args[]) {

        try {
            Enumeration<NetworkInterface> nets = null;
            nets = NetworkInterface.getNetworkInterfaces();

             for (NetworkInterface netint : Collections.list(nets))
             {
                 if(netint.getName().contentEquals("eth0"))
                 {
                     Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                     for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                         if(inetAddress.toString().contains("/192")){
                             out.printf("InetAddress: %s\n", inetAddress);
                         }

                     }
                 }
             }

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}