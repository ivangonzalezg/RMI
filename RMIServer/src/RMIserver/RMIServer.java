package RMIserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RMIServer implements RMIServerInterface {

    public boolean seguirBuscando;

    public static void main(String[] args) {
        try {
            RMIServer obj = new RMIServer();
            RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(obj, 0);
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("RMIServerInterface", stub);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String crackearArchivo(char Inital, char Final, File inputFile, File outputFile,String checkSum) throws RemoteException {
        this.seguirBuscando = true;
        String key = null;
        try {
            key = DPUCrypter.CrackFile(Inital, Final, inputFile, outputFile, checkSum, this);
        } catch (IOException ex) {
            Logger.getLogger(RMIServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.seguirBuscando = false;
        return key;
    }

    @Override
    public void DetenerCracker() throws RemoteException {
        this.seguirBuscando = false;
    }

}
