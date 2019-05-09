package RMIserver;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServerInterface extends Remote{

    public String crackearArchivo(char Inital, char Final, File inputFile,String checkSum) throws RemoteException;

    public void DetenerCracker() throws RemoteException;
}