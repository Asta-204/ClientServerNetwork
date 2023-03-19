package project1;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
@SuppressWarnings("unused")
public class server {

    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String[] args) throws IOException {

        try {
            serverSocket = new ServerSocket(25444);
            System.out.println("Salut!! le serveur est en attente de connection.");
        } catch (Exception e) {
            System.err.println("Un autre programme utilise ce port.");
            System.exit(1);
        }
   
  
        while (true) {
            try {
                clientSocket = serverSocket.accept();
               System.out.println("Connection accepter: " + clientSocket);
               
               class ServiceClient implements Runnable {

            	    private Socket clientSocket;
            	    private BufferedReader in = null;

            	    public ServiceClient(Socket client) {
            	        this.clientSocket = client;
            	    }

            	    @Override
            	    public void run() {
            	        try {
            	            in = new BufferedReader(new InputStreamReader(
            	                    clientSocket.getInputStream()));
            	            String clientSelection;
            	            while ((clientSelection = in.readLine()) != null) {
            	                switch (clientSelection) {
            	                    case "1":
            	                        receiveFile();
            	                        continue;
            	                    case "2":
            	                        System.out.println("La liste des fichiers du serveur est envoyer au client.");
            	                    	continue;
            	                    case "3":
            	                        String outGoingFileName;
            	                        while ((outGoingFileName = in.readLine()) != null) {
            	                            sendFile(outGoingFileName);
            	                        }
            				continue;
            			    case "4":
            				System.exit(1);	

            	                       break;
            	                    default:
            	                        System.out.println("Commande incorrecte.");
            	                        break;
            	                }
            	               
            	            }

            	        } catch (IOException ex) {
            	          
            	        }
            	    }

            	    public void receiveFile() {
            	        try {
            	            int bytesRead;

            	            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());

            	            String fileName = clientData.readUTF();
            	            OutputStream output = new FileOutputStream(fileName);
            	            long size = clientData.readLong();
            	            byte[] buffer = new byte[1024];
            	            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
            	                output.write(buffer, 0, bytesRead);
            	                size -= bytesRead;
            	            }

            	            output.close();
            	            clientData.close();

            	            System.out.println("Fichier "+fileName+" recu du client.");
            	        } catch (IOException ex) {
            	            System.err.println("Erreur. Connection fermer.");
            	        }
            	    }

            	    public void sendFile(String fileName) {
            	        try {
            	           
            	            File myFile = new File(fileName);  //handle file reading
            	            byte[] mybytearray = new byte[(int) myFile.length()];

            	            FileInputStream fis = new FileInputStream(myFile);
            	            BufferedInputStream bis = new BufferedInputStream(fis);
            	            
            	            @SuppressWarnings("resource")
            				DataInputStream dis = new DataInputStream(bis);
            	            dis.readFully(mybytearray, 0, mybytearray.length);

            	           
            	            OutputStream os = clientSocket.getOutputStream();  //handle file send over socket

            	            DataOutputStream dos = new DataOutputStream(os); //Sending file name and file size to the server
            	            dos.writeUTF(myFile.getName());
            	            dos.writeLong(mybytearray.length);
            	            dos.write(mybytearray, 0, mybytearray.length);
            	            dos.flush();
            	            System.out.println("Fichier "+fileName+" est envoyer au client.");
            	        } catch (Exception e) {
            	            System.err.println("Le fichier n'existe pas!");
            	        } 
            	    }
            	}


                Thread t = new Thread(new ServiceClient(clientSocket));

                t.start();

            } catch (Exception e) {
                System.err.println("Erreur connection.");
            }
        }
    }
}
