import java.io.*;
import java.net.*;
import java.util.*;
import java.security.PublicKey;
import java.io.Serializable;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;

public class SecretClient {

	public static void main(String[] args) throws Exception {
		// Bind the socket to the server with the appropriate port
		Socket socket = new Socket("localhost", 3333);

		// Setup I/O streams
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


		// create client's public key and send to server
        DH client = new DH();
        PublicKey clientPub = client.getPubKey();
        out.writeObject(clientPub);
		out.flush();

		// receive server's public key
		PublicKey serverPub = (PublicKey) in.readObject();

		// compute the secret key
        byte[] clientShared = client.getSharedKey(serverPub, 128);
        SecretKey clientSecret = new SecretKeySpec(clientShared, "AES");



        String dataSet[] = {"70","73","81","93","103","93","82","73","64","57","65"};
        int i =0;
        while(i<dataSet.length){
        /*
		System.out.print("Enter the current heart beat: ");
		Scanner scan = new Scanner(System.in);
		String s;
		s = scan.nextLine();
		*/
		String s = dataSet[i];
		System.out.println("Sent: "+s);
		AESEncryption encryptEngine = new AESEncryption();
		SealedObject cipherObject = encryptEngine.encrypt(s, clientSecret);

		out.writeObject(cipherObject);
		out.flush();

		SealedObject result = (SealedObject) in.readObject();
		String decryptedText = (String) encryptEngine.decrypt(result, clientSecret);


		System.out.println(decryptedText);
		Thread.sleep(1000);
		i++;
		}
		System.out.println("simulation end");
		}
	}

