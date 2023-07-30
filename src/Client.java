import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

public class Client {

    public static <SecretKey> void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecurisedInOut securisedInOut = new SecurisedInOut();
        Key key = null;
        String message;
        Socket socket = new Socket("localhost",1500);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        InputStreamReader inp = new InputStreamReader(System.in);
        BufferedReader userInput = new BufferedReader(inp);

        DataInputStream inputServeur = new DataInputStream(socket.getInputStream());

        String messageServeur = "";

        messageServeur = inputServeur.readUTF();
        System.out.println(messageServeur+"\n");

        //Reception de la clé publique RSA
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        PublicKey publicKey = (PublicKey) in.readObject();

        // Génération de la clé DES
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        Key desKey = keyGen.generateKey();

        // Encryption de la clé DES avec la clé publique RSA
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(desKey.getEncoded());

        //Envoi de la clé DES encryptée
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(encryptedKey);

        while (!messageServeur.contains("Fin de la partie")){
            if(messageServeur.contains("C'est à toi de jouer") || messageServeur.contains("Ce coup n'est pas valide !")){
                message = userInput.readLine();
                securisedInOut.sendMessage(socket,desKey,message);
            }
            messageServeur = securisedInOut.getMessage(socket,desKey);
            System.out.println(messageServeur);
        }
        outputStream.close();
        socket.close();
    }


}
