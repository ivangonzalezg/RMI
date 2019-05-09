package RMIserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class DPUCrypter {

    public static int CryptFileUsingAES(boolean encrypt, String key, File inputFile, String checkSum) {
        try {
            if(key.length()<16){                
                key=String.format("%16s", key).replace(' ', '0');
            }
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            if (encrypt) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            } else {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            }

            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);

            byte[] outputBytes = cipher.doFinal(inputBytes);
            
            File outputFile = File.createTempFile("tempFileDecrypted", ".tmp");
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);

            inputStream.close();
            outputStream.close();
            if (checkSum != null) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    String checkSumOfDecryptedFile = DPUCrypter.checksum(outputFile, md);
                    if (!(checkSum.equals(checkSumOfDecryptedFile))) {
                        return -2;
                    }
                } catch (Exception err) {

                }
            }
            return 0;

        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | BadPaddingException
                | IllegalBlockSizeException | IOException e) {

            //e.printStackTrace();
            return -1;
        } catch (InvalidKeyException e) {

            //e.printStackTrace();
            return -2;
        }
    }

    public static String CrackFile(char Inital, char Final, File inputFile, String checkSum, RMIServer rmiServer) throws IOException {
        System.out.println("Loading...");
        char[] PL = DPUCrypter.shuffleArray(DPUCrypter.createArray(Inital, Final));
        char[] SL = DPUCrypter.shuffleArray(DPUCrypter.createArray('a', 'z'));
        char[] TL = DPUCrypter.shuffleArray(DPUCrypter.createArray('a', 'z'));
        char[] CL = DPUCrypter.shuffleArray(DPUCrypter.createArray('a', 'z'));
        char[] QL = DPUCrypter.shuffleArray(DPUCrypter.createArray('a', 'z'));
        int KeysLength = (int) Math.pow(26, 4) * (Final-Inital+1);
        System.out.println(KeysLength);
        String [] Keys = new String[KeysLength];
        int counter = 0;
        for(int A = 0; A < PL.length; A++){
            for(int B = 0; B < SL.length; B++){
                for(int C = 0; C < TL.length; C++){
                    for(int D = 0; D < CL.length; D++){
                        for(int E = 0; E < QL.length; E++){
                            Keys[counter] = PL[A]+""+SL[B]+""+TL[C]+""+CL[D]+""+QL[E];
                            counter += 1;
                        }
                    }
                }
            }
        }
        List<String> strList = Arrays.asList(Keys);
        Collections.shuffle(strList);
        Keys = strList.toArray(new String[strList.size()]);
        System.out.println("Starting...");
        long startTime = System.currentTimeMillis();
        for (int F = 0; F < Keys.length ;F++){
            String currentIndex = Keys[F];
            System.out.println("Current key " + F + ": " + currentIndex);
            int internalReturn = CryptFileUsingAES(false, currentIndex, inputFile,checkSum);
            if (internalReturn == 0) {
                Date endDate=new Date();
                long stopTime = System.currentTimeMillis();
                System.out.println("The key is: " + currentIndex+" / ended at "+endDate.toString() + ". Took " + (stopTime - startTime)/1000 + " seconds");
                return currentIndex;
            }
        }
        long stopTime = System.currentTimeMillis();
        System.out.println("NOT FOUND" + ". Took " + (stopTime - startTime)/1000 + " seconds");
        return "NOT FOUND";
    }

    public static String checksum(File filepath, MessageDigest md) throws IOException {

        // file hashing with DigestInputStream
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();

    }
    
    public static char[] shuffleArray(char[] array) throws IOException {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
        return array;
    }
    
    public static char[] createArray(char first, char last) throws IOException {
        int a= last - first + 1;
        char[] array = new char[a];
        int index = 0;
        for (char c = first; c <= last; c++) {
            array[index++] = c;
        }     
        return array;
    }

}
