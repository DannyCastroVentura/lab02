package pacote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServidorC extends Thread {

    private static Payload objetoPayload = new Payload();
    private final static int PORT = 4242;
    private static ServerSocket serverSocket;
    private final Socket socket;

    ArrayList<String> lista = new ArrayList<>();

    static ArrayList<String> listaDeIps = new ArrayList<>();


    public ServidorC(Socket socket) {
        this.socket = socket;
    }


    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object obj;
            Object ligacao = "Ligação estabelecida!";

            out.writeObject(ligacao);

            while (true) {
                obj = in.readObject();

                if (obj instanceof String) {
                    if (obj.equals("Q")) {
                        break;
                    }

                } else if (obj instanceof Payload) {
                    if(((Payload) obj).getData().contentEquals("MostraOMenu"))
                    {
                        ((Payload) obj).setData("Menu: R-Registar\tC-Consultar\tD-Eliminar\tL-Cada Servidor Listar o que tem\tQ-Terminar");
                        out.writeObject(obj);
                    }else{
                        String[] textoSeparado = ((Payload) obj).getData().split(" ", 3);
                        int ServidorEscolhido = 0;
                        String ipEscolhido = "";
                        if(!textoSeparado[0].contentEquals("L")){
                            int valorDaChave = 0;
                            // Creating array of string length
                            char[] ch = new char[textoSeparado[1].length()];

                            // Copy character by character into array
                            //calcular o valor da string
                            for (int i = 0; i < textoSeparado[1].length(); i++) {
                                ch[i] = textoSeparado[1].charAt(i);
                                valorDaChave = valorDaChave + ch[i];
                            }

                            ServidorEscolhido = (valorDaChave % objetoPayload.getNumeroDoServidor()) + 1;

                            System.out.println("Numero de servidores: " + objetoPayload.getNumeroDoServidor());
                            System.out.println("Servidor Escolhido: " + ServidorEscolhido);


                            ipEscolhido = listaDeIps.get(ServidorEscolhido-1);

                            System.out.println("Ip do servidor escolhido: "+ ipEscolhido);

                        }




                        switch (textoSeparado[0]){
                            case "R":
                                if(ServidorEscolhido==1)
                                {
                                    int verificarSeExiste = 0;
                                    for(String l: lista) {
                                        String [] arr = l.split(" ", 2);
                                        if(arr[0].contentEquals(textoSeparado[1])){
                                            verificarSeExiste++;
                                        }
                                    }
                                    if(verificarSeExiste == 0)
                                    {
                                        lista.add(textoSeparado[1] + " " + textoSeparado[2]);
                                        objetoPayload.setData("Chave não existente!");
                                    }else{
                                        objetoPayload.setData("Chave existente.");
                                    }

                                }else{
                                    int finalServidorEscolhido = ServidorEscolhido;
                                    String finalIpEscolhido3 = ipEscolhido;
                                    Thread registar = new Thread(() -> {
                                        try {
                                            Socket socket = new Socket(finalIpEscolhido3, 4243 + finalServidorEscolhido);
                                            ObjectOutputStream out1 = new ObjectOutputStream(socket.getOutputStream());
                                            ObjectInputStream in1 = new ObjectInputStream(socket.getInputStream());
                                            Payload payload = new Payload("R " + textoSeparado[1] + " " + textoSeparado[2]);
                                            out1.writeObject(payload);
                                            String partilha = ((Payload) in1.readObject()).getData();
                                            System.out.println("[received] " + partilha);
                                            objetoPayload.setData(partilha);
                                        } catch (IOException | ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    registar.start();
                                    registar.join();
                                }
                                System.out.println(objetoPayload.getData());
                                if(!objetoPayload.getData().contentEquals("Chave existente."))
                                {
                                    System.out.println("Um cliente registou.");
                                    ((Payload) obj).setData("Item registado com sucesso.");
                                    out.writeObject(obj);
                                }else{
                                    System.out.println("Chave existente.");
                                    ((Payload) obj).setData("Chave existente.");
                                    out.writeObject(obj);
                                }

                                break;
                            case "C":
                                if(ServidorEscolhido == 1)
                                {
                                    int verificarSeExiste = 0;
                                    for(String l: lista) {
                                        String [] arr = l.split(" ", 2);
                                        if(arr[0].contentEquals(textoSeparado[1])){
                                            objetoPayload.setData(arr[1]);
                                            System.out.println("Chave encontrada com sucesso!");
                                            verificarSeExiste++;
                                        }
                                    }
                                    if(verificarSeExiste == 0)
                                    {
                                        objetoPayload.setData("Chave inexistente.");
                                    }
                                }else{
                                    int finalServidorEscolhido1 = ServidorEscolhido;
                                    String finalIpEscolhido2 = ipEscolhido;
                                    Thread busca = new Thread(() -> {
                                        try {
                                            Socket socket = new Socket(finalIpEscolhido2, 4243 + finalServidorEscolhido1);
                                            ObjectOutputStream out1 = new ObjectOutputStream(socket.getOutputStream());
                                            ObjectInputStream in1 = new ObjectInputStream(socket.getInputStream());
                                            Payload payload = new Payload("C " + textoSeparado[1]);
                                            out1.writeObject(payload);
                                            String partilha = ((Payload) in1.readObject()).getData();
                                            System.out.println("[received] " + partilha);
                                            objetoPayload.setData(partilha);
                                        } catch (IOException | ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    busca.start();
                                    busca.join();
                                }
                                if(!objetoPayload.getData().contentEquals("Chave inexistente."))
                                {
                                    System.out.println("Um cliente consultou.");
                                    ((Payload) obj).setData(objetoPayload.getData());
                                    out.writeObject(obj);
                                }else{
                                    System.out.println("Chave não encontrada!");
                                    ((Payload) obj).setData("Chave inexistente.");
                                    out.writeObject(obj);
                                }

                                break;
                            case "D":
                                if(ServidorEscolhido == 1)
                                {
                                    int verificarSeExiste = 0, posicao = 0;
                                    for(String l: lista) {
                                        String [] arr = l.split(" ", 2);
                                        if(arr[0].contentEquals(textoSeparado[1])){
                                            System.out.println("Chave encontrada com sucesso!");
                                            posicao = lista.indexOf(arr[0] + " " + arr[1]);

                                            verificarSeExiste++;
                                        }
                                    }
                                    if(verificarSeExiste == 0)
                                    {
                                        objetoPayload.setData("Chave inexistente.");
                                    }else{
                                        lista.remove(posicao);
                                    }

                                }else{
                                    int finalServidorEscolhido2 = ServidorEscolhido;
                                    String finalIpEscolhido1 = ipEscolhido;
                                    Thread busca = new Thread(() -> {
                                        try {
                                            Socket socket = new Socket(finalIpEscolhido1, 4243 + finalServidorEscolhido2);
                                            ObjectOutputStream out1 = new ObjectOutputStream(socket.getOutputStream());
                                            ObjectInputStream in1 = new ObjectInputStream(socket.getInputStream());
                                            Payload payload = new Payload("D " + textoSeparado[1]);
                                            out1.writeObject(payload);
                                            String partilha = ((Payload) in1.readObject()).getData();
                                            System.out.println("[received] " + partilha);
                                            objetoPayload.setData(partilha);
                                        } catch (IOException | ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    busca.start();
                                    busca.join();
                                }

                                if(!objetoPayload.getData().contentEquals("Chave inexistente."))
                                {
                                    System.out.println("Um cliente eliminou.");
                                    ((Payload) obj).setData("Item removido com sucesso.");
                                    out.writeObject(obj);
                                }else{
                                    System.out.println("Chave inexistente.");
                                    ((Payload) obj).setData("Chave inexistente.");
                                    out.writeObject(obj);
                                }
                                break;

                            case "L":

                                int numeroServidores = objetoPayload.getNumeroDoServidor() + 1;

                                if(lista.size() == 0)
                                {
                                    System.out.println("Sem itens.");
                                }else{
                                    for(String l : lista) {
                                        System.out.println(l);
                                    }
                                }
                                for (int i = 2; i < numeroServidores; i++)
                                {
                                    int finalI = i;
                                    ipEscolhido = listaDeIps.get(finalI-1);
                                    String finalIpEscolhido = ipEscolhido;
                                    Thread busca = new Thread(() -> {
                                        try {
                                            Socket socket = new Socket(finalIpEscolhido, 4243 + finalI);
                                            ObjectOutputStream out1 = new ObjectOutputStream(socket.getOutputStream());
                                            ObjectInputStream in1 = new ObjectInputStream(socket.getInputStream());
                                            Payload payload = new Payload("L");
                                            out1.writeObject(payload);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    busca.start();
                                    busca.join();
                                }

                                break;

                            default:
                                System.out.println("Um cliente digitou um comando desconhecido.");
                                ((Payload) obj).setData("Comando desconhecido!");
                                out.writeObject(obj);
                                break;

                        }
                    }


                } else {
                    System.out.println("[payload] Unexpected data.");
                }



            }
            socket.close();
            out.close();
            in.close();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        try {
            Thread busca = new Thread(() -> {
                try {

                    Socket socket = new Socket("192.168.1.91", 4243);
                    //Socket socket = new Socket("localhost", 4243);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    Payload payload = new Payload("numero");
                    out.writeObject(payload);
                    String partilha = ((Payload) in.readObject()).getData();
                    System.out.println("[received] " + partilha);
                    objetoPayload.setNumeroDoServidor(Integer.parseInt(partilha));
                    listaDeIps = ((Payload) in.readObject()).getListaDeIps();
                    System.out.println("[received] " + listaDeIps);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
            busca.start();
            busca.join();
            serverSocket = new ServerSocket(PORT, 1, InetAddress.getLocalHost());
            //serverSocket = new ServerSocket(PORT);
            System.out.println("[started]");
        } catch (IOException | InterruptedException ioe) {
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
                new ServidorC(socket).start();
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
