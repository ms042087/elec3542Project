import java.io.*;
import java.net.*;
import java.security.PublicKey;
import java.io.Serializable;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter ;

public class SecretServer {

	// To determine whether the condition is OK
	public static String determineState(String s) {
		String result = "";
		int heartBeat = Integer.parseInt(s);
		System.out.println("HeartBeat = "+heartBeat);
		if(heartBeat<60||heartBeat>100)
			result = "Not OK";
		else
			result = "OK";

		return result;
	}
	// write the data in the csv file
	public static void writeToFile(int data) throws IOException{
		FileWriter  pw = new FileWriter ("test.csv",true);
        StringBuilder sb = new StringBuilder();
        sb.append(data);
        sb.append('\n');
        pw.write(sb.toString());
        pw.close();
	}
	public static void main(String[] args) throws Exception {



		// Create server socket listening on port
		int port = 3333;
		ServerSocket serverSocket = new ServerSocket(port);
		boolean connected = false;

		// Declare client socket
		Socket clientSocket;

		while (true) { // Provide service continuously
			clientSocket = serverSocket.accept();

			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());


			// when receive client's public key, create server's public key and send to client
			PublicKey clientPub = (PublicKey) in.readObject();

        	DH server = new DH();
        	PublicKey serverPub = server.getPubKey();
        	out.writeObject(serverPub);
			out.flush();
			// compute the secret key
			byte[] serverShared = server.getSharedKey(clientPub, 128);
      	  	SecretKey serverSecret = new SecretKeySpec(serverShared, "AES");

			AESEncryption encryptEngine = new AESEncryption();
			connected = true;
			if(connected==true){
				while(true){
					SealedObject cipherObject = (SealedObject) in.readObject();
					String decryptedText = (String) encryptEngine.decrypt(cipherObject, serverSecret);
					writeToFile(Integer.parseInt(decryptedText));
					String result = determineState(decryptedText);
					cipherObject = encryptEngine.encrypt(result, serverSecret);
					out.writeObject(cipherObject);
					out.flush();
				}
			}

			out.close();
			in.close();
			clientSocket.close();
		}
	}

}
