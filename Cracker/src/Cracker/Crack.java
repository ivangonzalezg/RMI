package Cracker;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import RMIserver.RMIServerInterface;
import java.io.File;
import java.io.FileInputStream;

public class Crack extends Thread {

    public RMIServerInterface stub;
    public CrackerUI cracker;
    public String checkSum;
    private final File inputFile;
    private final File outputFile;

    public Crack(String ip, CrackerUI cracker, File inputFile, File outputFile, String checkSum) {
        this.checkSum = checkSum;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.cracker = cracker;
        try {
            Registry registry = LocateRegistry.getRegistry(ip, 1099);
            stub = (RMIServerInterface) registry.lookup("RMIServerInterface");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            String response = stub.crackearArchivo('h', 'h', inputBytes, checkSum);
            if (!"NOT FOUND".equals(response)) {
                cracker.DetenerLosDemasNodos(response);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
