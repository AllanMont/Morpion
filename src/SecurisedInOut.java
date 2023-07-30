import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;

public class SecurisedInOut {

    public String getMessage(Socket socket, Key key) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, key);
        DataInputStream input = new DataInputStream(socket.getInputStream());
        byte[] messageEncode = new byte[1000];
        int length = input.read(messageEncode);
        byte[] messageDecode = desCipher.doFinal(messageEncode, 0, length);

        return new String(messageDecode);
    }

    public void sendMessage(Socket socket, Key key, String message) throws IOException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        Cipher desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedMessage = desCipher.doFinal(message.getBytes());
        out.write(encryptedMessage);
    }
}