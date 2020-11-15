package pacote;

import java.io.Serializable;
import java.util.ArrayList;

class Payload implements Serializable {
    private String data;
    private int numeroDoServidor = 0;
    private ArrayList<String> listaDeIps = new ArrayList<>();

    private boolean executando = false;

    //mostrar Os Diferentes IPs associados
    public ArrayList<String> getListaDeIps() {
        return listaDeIps;
    }

    public void setListaDeIps(ArrayList<String> listaDeIps) {
        this.listaDeIps = listaDeIps;
    }



    //payload numeroDoServidor
    synchronized public void addNumeroDoServidor() {
        while(executando)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executando = true;
        numeroDoServidor++;
        executando = false;
        notifyAll();

    }
    synchronized public void setNumeroDoServidor(int numero) {
        while(executando)
        {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executando = true;
        numeroDoServidor = numero;
        executando = false;
        notifyAll();

    }

    synchronized public int getNumeroDoServidor() {
        return numeroDoServidor;
    }

    //payload data

    public Payload(){

    }
    public Payload(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



}


