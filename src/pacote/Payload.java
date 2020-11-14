package pacote;

import java.io.Serializable;

class Payload implements Serializable {
    private String data;
    private int numeroDoServidor = 1;

    private boolean executando = false;

    //payload numeroDoServidor
    synchronized public void addNumeroDoServidor() {
        while(executando == true)
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
        while(executando == true)
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


