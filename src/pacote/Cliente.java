package pacote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Cliente {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Socket socket = new Socket("192.168.1.104", 4242);
            //Socket socket = new Socket("localhost", 4242);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object ligacao = in.readObject();
            if(ligacao instanceof String)
            {
                System.out.println("[received] " + ligacao);
            }


            String escolha = "";
            while((!escolha.contentEquals("Q")) && (!escolha.contentEquals("T")))
            {
                Payload payload = new Payload("MostraOMenu");
                out.writeObject(payload);
                System.out.println("[received] " + ((Payload) in.readObject()).getData());
                escolha = sc.nextLine();
                if((!escolha.contentEquals("Q")) && (!escolha.contentEquals("T")))
                {
                    if(escolha.contentEquals("L"))
                    {
                        payload =  new Payload(escolha);
                        out.writeObject(payload);
                        System.out.println("[sent] " + payload.getData());
                    }else{
                        payload =  new Payload(escolha);
                        out.writeObject(payload);
                        System.out.println("[sent] " + payload.getData());
                        System.out.println("[received] " + ((Payload) in.readObject()).getData());
                    }

                }else{
                    out.writeObject(escolha);
                    System.out.println("[sent] " + escolha);
                }
            }
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ocurreu um erro. \n");
            e.printStackTrace();
        }
        sc.close();
    }
}