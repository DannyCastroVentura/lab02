package pacote;

import javax.sound.sampled.Port;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServidorS extends Thread {

    //problema é que ele fica a mexer no mesmo em vez de mexer na variavel da outra classe
    private static Payload objetoPayload = new Payload();
    private final static int PORT = 4243;
    private static ServerSocket serverSocket;
    private final Socket socket;


    static ArrayList<String> listaDeIps = new ArrayList<>();

    public ServidorS(Socket socket) {
        this.socket = socket;
    }

    public static void adicionarServidor()
    {
        objetoPayload.addNumeroDoServidor();
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object obj = null;
            obj = in.readObject();
            //se o servidor perguntar quantos servidores existem, esse numero total é retornado
            if(((Payload) obj).getData().contentEquals("numero"))
            {
                Payload numeroDoServidor = new Payload(Integer.toString(objetoPayload.getNumeroDoServidor()));
                out.writeObject(numeroDoServidor);
                Payload ListaDeIps = new Payload();
                ((Payload) ListaDeIps).setListaDeIps(listaDeIps);
                out.writeObject(ListaDeIps);
            }else{
                //chamar o metodo adicionar servidor
                String novoIp = ((Payload) obj).getData();
                adicionarServidor();
                listaDeIps.add(novoIp);
                Payload numeroDoServidor = new Payload(Integer.toString(objetoPayload.getNumeroDoServidor()));
                out.writeObject(numeroDoServidor);

                System.out.println("Novo servidor membro adquirido!");
                System.out.println("Numero de servidores atuais: " + objetoPayload.getNumeroDoServidor());

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        try {

            adicionarServidor();
            InetAddress localhost = InetAddress.getLocalHost();
            listaDeIps.add((localhost.getHostAddress()).trim());
            serverSocket = new ServerSocket(PORT, 1, InetAddress.getLocalHost());
            //serverSocket = new ServerSocket(PORT);
            System.out.println("[started]");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("[connection] " +
                        socket.getInetAddress().getHostName() +
                        "@" +
                        socket.getInetAddress().getHostAddress() +
                        " " +
                        socket.getLocalPort() +
                        ":" +
                        socket.getPort());
                new ServidorS(socket).start();
            } catch (IOException ioe) {
                if (serverSocket.isClosed()) {
                    System.out.println("[terminated]");
                } else {
                    ioe.printStackTrace();
                }
            }
        }
    }


}
