package pacote;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


public class ServerMembro {
    private static ServerSocket serverSocket;
    public static void main(String[] args) {
        try {
            Socket socket1 = new Socket("192.168.1.106", 4243);
            //Socket socket1 = new Socket("localhost", 4243);
            ObjectOutputStream out = new ObjectOutputStream(socket1.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket1.getInputStream());


            Enumeration<NetworkInterface> nets = null;
            nets = NetworkInterface.getNetworkInterfaces();

            Payload payload;
            for (NetworkInterface netint : Collections.list(nets))
            {
                if(netint.getName().contentEquals("eth0"))
                {
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if(inetAddress.toString().contains("/192")){
                            payload = new Payload(inetAddress.toString());
                            out.writeObject(payload);
                        }

                    }
                }
            }



            int numeroDoServ = Integer.parseInt(((Payload) in.readObject()).getData());

            System.out.println("O numero do servidor é " + numeroDoServ);
            final int port = (4243 + numeroDoServ);
            System.out.println("A porta do servidor é " + port);
            ArrayList<String> lista = new ArrayList<>();
            socket1.close();
            try {
                //serverSocket = new ServerSocket(port);
                nets = null;
                nets = NetworkInterface.getNetworkInterfaces();
                for (NetworkInterface netint : Collections.list(nets))
                {
                    if(netint.getName().contentEquals("eth0"))
                    {
                        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                            if(inetAddress.toString().contains("/192")){

                                serverSocket = new ServerSocket(port, 1, inetAddress);

                                System.out.println("O IP do servidor é " + inetAddress);
                            }

                        }
                    }
                }
                System.out.println("[started]");

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    System.out.println("[connection] " +
                            socket.getInetAddress().getHostName() +
                            "@" +
                            socket.getInetAddress().getHostAddress() +
                            " " +
                            socket.getLocalPort() +
                            ":" +
                            socket.getPort());

                    Object obj = in.readObject();
                    if(obj instanceof Payload){
                        String[] escolha = ((Payload) obj).getData().split(" ", 3);
                        switch (escolha[0]){
                            case "R":
                                int verificarSeExiste = 0;
                                for(String l: lista) {
                                    String [] arr = l.split(" ", 2);
                                    if(arr[0].contentEquals(escolha[1])){
                                        verificarSeExiste++;
                                    }
                                }
                                if(verificarSeExiste == 0)
                                {
                                    lista.add(escolha[1] + " " + escolha[2]);
                                    payload = new Payload("Chave e texto registado com sucesso!");
                                    out.writeObject(payload);
                                    System.out.println("Chave e texto registado com sucesso!");
                                }else{
                                    payload = new Payload("Chave existente.");
                                    out.writeObject(payload);
                                }
                                break;
                            case "C":
                                verificarSeExiste = 0;
                                for(String l: lista) {
                                    String [] arr = l.split(" ", 2);
                                    if(arr[0].contentEquals(escolha[1])){
                                        payload = new Payload(arr[1]);
                                        out.writeObject(payload);
                                        System.out.println("Chave encontrada com sucesso!");
                                        verificarSeExiste++;
                                    }
                                }
                                if(verificarSeExiste == 0)
                                {
                                    System.out.println("Chave não encontrada!");
                                    payload = new Payload("Chave inexistente.");
                                    out.writeObject(payload);
                                }
                                break;
                            case "D":
                                int posicao = 0;
                                verificarSeExiste = 0;
                                for(String l: lista) {
                                    String [] arr = l.split(" ", 2);
                                    if(arr[0].contentEquals(escolha[1])){
                                        posicao = lista.indexOf(arr[0] + " " + arr[1]);
                                        verificarSeExiste++;
                                    }
                                }
                                if(verificarSeExiste == 0)
                                {
                                    System.out.println("Chave não encontrada!");
                                    payload = new Payload("Chave inexistente.");
                                    out.writeObject(payload);
                                }else{
                                    lista.remove(posicao);
                                    payload = new Payload("Item eliminado com sucesso!");
                                    out.writeObject(payload);
                                    System.out.println("Item eliminado com sucesso!");
                                }
                                break;
                            case "L":
                                if(lista.size() == 0)
                                {
                                    System.out.println("Sem itens.");
                                }else{
                                    for(String l : lista) {
                                        System.out.println(l);
                                    }
                                }
                                break;
                        }
                    }





                } catch (IOException ioe) {
                    if (serverSocket.isClosed()) {
                        System.out.println("[terminated]");
                    } else {
                        ioe.printStackTrace();
                    }
                }
            }



        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}