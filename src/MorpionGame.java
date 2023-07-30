import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.*;

public class MorpionGame {
    private final SecurisedInOut securisedInOut = new SecurisedInOut();
    private Map<String, String> tableau = new HashMap<>();
    private int id;
    private static int numberOfGames = 0;
    private String currentPlayer;
    private Socket client1,client2;
    private KeyPair keyClient1RSA, keyClient2RSA;
    private Key keyClient1DES, keyClient2DES;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public MorpionGame(Socket client1, Socket client2) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        this.client1 = client1;
        this.client2 = client2;
        this.keyClient1RSA = this.generateRSAKey();
        this.keyClient2RSA = this.generateRSAKey();
        this.keyClient1DES = this.sendRSAKey(client1,keyClient1RSA);
        this.keyClient2DES = this.sendRSAKey(client2,keyClient2RSA);
        numberOfGames++;
        id = numberOfGames;
        tableau.put("A1","-");
        tableau.put("A2","-");
        tableau.put("A3","-");
        tableau.put("B1","-");
        tableau.put("B2","-");
        tableau.put("B3","-");
        tableau.put("C1","-");
        tableau.put("C2","-");
        tableau.put("C3","-");
    }

    public void gameLoop() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        securisedInOut.sendMessage(client1, keyClient1DES, "\nTu est le joueur X\n" + showGame() + "\nC'est à toi de jouer");
        securisedInOut.sendMessage(client2, keyClient2DES, "\nTu est le joueur O\n" + showGame() + "\nC'est à ton adversaire de jouer");
        while(!currentPlayerWin() && gameIsNotDraw()){
            currentPlayer = "1";
            while (!jouerUnCoup(securisedInOut.getMessage(client1, keyClient1DES),"X")){
                securisedInOut.sendMessage(client1, keyClient1DES, "Ce coup n'est pas valide !");
            }
            if(!currentPlayerWin() && gameIsNotDraw()){
                securisedInOut.sendMessage(client1, keyClient1DES, showGame() + "\nC'est à ton adversaire de jouer");
                securisedInOut.sendMessage(client2, keyClient2DES, showGame() + "\nC'est à toi de jouer");
                currentPlayer = "2";
                while (!jouerUnCoup(securisedInOut.getMessage(client2, keyClient2DES),"O")){
                    securisedInOut.sendMessage(client2, keyClient2DES, "Ce coup n'est pas valide !");
                }
                if(!currentPlayerWin() && gameIsNotDraw()){
                    securisedInOut.sendMessage(client1, keyClient1DES, showGame() + "\nC'est à toi de jouer");
                    securisedInOut.sendMessage(client2, keyClient2DES, showGame() + "\nC'est à ton adversaire de jouer");
                }
            }
        }
        if(gameIsNotDraw()){
            securisedInOut.sendMessage(client1, keyClient1DES, showGame() + "\nLe joueur " + currentPlayer + " gagne !\nFin de la partie");
            securisedInOut.sendMessage(client2, keyClient2DES, showGame() + "\nLe joueur " + currentPlayer + " gagne !\nFin de la partie");
            System.out.println("PARTIE "+id+"Le joueur "+currentPlayer+" gagne la partie !");
        }
        else{
            securisedInOut.sendMessage(client1, keyClient1DES, showGame() + "\nEgalité !\nFin de la partie");
            securisedInOut.sendMessage(client2, keyClient2DES, showGame() + "\nEgalité !\nFin de la partie");
            System.out.println("PARTIE "+id+"Personne ne gagne !");
            System.out.println("PARTIE "+id+"Egalité !");
        }
        numberOfGames--;
    }

    private boolean gameIsNotDraw() {
        boolean gameIsNotDraw = true;
        if(!tableau.containsValue("-") && !currentPlayerWin()){
            gameIsNotDraw = false;
        }
        return gameIsNotDraw;
    }

    public boolean jouerUnCoup(String coordonee, String symbole){
        boolean coupValide = false;
        if(caseExiste(coordonee) && caseLibre(coordonee)){
            tableau.put(coordonee,symbole);
            coupValide = true;
        }
        return coupValide;
    }

    public String showGame(){
        String game = "   1 2 3";
        game += "\nA |"+tableau.get("A1")+"|"+tableau.get("A2")+"|"+tableau.get("A3")+"|";
        game += "\nB |"+tableau.get("B1")+"|"+tableau.get("B2")+"|"+tableau.get("B3")+"|";
        game += "\nC |"+tableau.get("C1")+"|"+tableau.get("C2")+"|"+tableau.get("C3")+"|";

        return game;
    }

    public boolean currentPlayerWin(){
        boolean gameWon = false;
        if(tableau.get("A1").equals(tableau.get("A2")) && tableau.get("A2").equals(tableau.get("A3")) && !tableau.get("A1").equals("-")){
            gameWon = true;
        }
        if(tableau.get("B1").equals(tableau.get("B2")) && tableau.get("B2").equals(tableau.get("B3")) && !tableau.get("B1").equals("-")){
            gameWon = true;
        }
        if(tableau.get("C1").equals(tableau.get("C2")) && tableau.get("C2").equals(tableau.get("C3")) && !tableau.get("C1").equals("-")){
            gameWon = true;
        }
        if(tableau.get("A1").equals(tableau.get("B1")) && tableau.get("B1").equals(tableau.get("C1")) && !tableau.get("A1").equals("-")){
            gameWon = true;
        }
        if(tableau.get("A2").equals(tableau.get("B2")) && tableau.get("B2").equals(tableau.get("C2")) && !tableau.get("A2").equals("-")){
            gameWon = true;
        }
        if(tableau.get("A3").equals(tableau.get("B3")) && tableau.get("B3").equals(tableau.get("C3")) && !tableau.get("A3").equals("-")){
            gameWon = true;
        }
        if(tableau.get("A1").equals(tableau.get("B2")) && tableau.get("B2").equals(tableau.get("C3")) && !tableau.get("A1").equals("-")){
            gameWon = true;
        }
        if(tableau.get("A3").equals(tableau.get("B2")) && tableau.get("B2").equals(tableau.get("C1")) && !tableau.get("A3").equals("-")){
            gameWon = true;
        }
        return gameWon;
    }

    private boolean caseLibre(String coordonee){
        return tableau.get(coordonee).equals("-");
    }

    private boolean caseExiste(String coordonee){
        return tableau.containsKey(coordonee);
    }

    public static int getNumberOfGames(){
        return numberOfGames;
    }

    public int getId() {
        return id;
    }

    private KeyPair generateRSAKey() {
        KeyPair keypair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            keypair = keyGen.genKeyPair();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return keypair;
    }

    public Key sendRSAKey(Socket socket,KeyPair rsaKey) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        ObjectOutputStream outputClient = new ObjectOutputStream(socket.getOutputStream());
        DataInputStream inputServeur = new DataInputStream(socket.getInputStream());
        outputClient.writeObject(rsaKey.getPublic());
        outputClient.flush();
        System.out.println("Envoie de la clé RSA "+rsaKey.getPublic());

        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        byte[] encryptedKey = (byte[]) in.readObject();
        System.out.println("Reception de la clé DES"+Arrays.toString(encryptedKey)+"\n");

        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaKey.getPrivate());
        byte[] decryptedKey = rsaCipher.doFinal(encryptedKey);

        Key desKey = new SecretKeySpec(decryptedKey, 0, 8, "DES");
        System.out.println("Decryptage de la clé DES "+desKey+"\n");
        return desKey;
    }
}
