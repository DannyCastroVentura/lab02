package pacote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorS extends Thread {

    //problema é que ele fica a mexer no mesmo em vez de mexer na variavel da outra classe
    private static Payload objetoPayload = new Payload();
    private final static int PORT = 4243;
    private static ServerSocket serverSocket;
    private final Socket socket;

    public ServidorS(Socket socket) {
        this.socket = socket;
    }

    public void adicionarServidor()
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
            if(((Payload) obj).getData().contentEquals("numero"))
            {
                Payload numeroDoServidor = new Payload(Integer.toString(objetoPayload.getNumeroDoServidor()));
                out.writeObject(numeroDoServidor);
            }else{
                //chamar o metodo adicionar servidor
                adicionarServidor();
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
            serverSocket = new ServerSocket(PORT);
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
