import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Serveur extends Thread {

    public static ServerSocket serveur;
    public static Socket waitingClient1;
    public static Socket waitingClient2;

    public Serveur() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        serveur = new ServerSocket(1500);
        while(true){
            //System.out.println("Il y a actuellement "+MorpionGame.getNumberOfGames()+" parties en cours.");
            waitingClient1 = serveur.accept();
            DataOutputStream outputClient1 = new DataOutputStream(waitingClient1.getOutputStream());
            System.out.println("Un joueur attend une partie");
            outputClient1.writeUTF("Attente d'un autre joueur...");
            outputClient1.flush();
            waitingClient2 = serveur.accept();
            DataOutputStream outputClient2 = new DataOutputStream(waitingClient2.getOutputStream());
            outputClient2.writeUTF("Lancement de la partie avec le joueur 1");
            outputClient2.flush();
            Serveur thread = new Serveur();
            thread.start();
        }
    }

    public void run() {
        try {
            Socket client1 = waitingClient1;
            Socket client2 = waitingClient2;

            MorpionGame game = new MorpionGame(client1,client2);
            System.out.println("Lancement de la partie "+game.getId());
            game.gameLoop();
        }
        catch (IOException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException |
               NoSuchAlgorithmException | BadPaddingException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}