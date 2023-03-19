package project1;

import java.net.*;
import java.io.*;
import java.util.*;

public class client {

    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;

    public static void main(String[] args) throws IOException {
    	while(true) {
            try {
                sock = new Socket("localhost", 25444);
                stdin = new BufferedReader(new InputStreamReader(System.in));
            } catch (Exception e) {
                System.err.println("Nous arrivons pas à vous connecter au serveur, réessayer plus tard .");
                System.exit(1);
            }

            os = new PrintStream(sock.getOutputStream());

            try {
                switch (Integer.parseInt(selectAction())) {
                case 1:
                    os.println("1");
                    sendFile();
                    continue;
                case 2:
                	os.println("2");
                	listFile();
                	continue;
                case 3:
                    os.println("3");
                    System.out.print("Entrer le nom du fichier: ");
                    fileName = stdin.readLine();
                    os.println(fileName);
                    receiveFile(fileName);
                    continue;
                case 4:
                	sock.close();
                	System.exit(1);
                }
            } catch (Exception e) {
                System.err.println("Entrer non valide");
            }

    	}
    }

    public static String selectAction() throws IOException {
        System.out.println("1. Envoyer un fichier.");
        System.out.println("2. Liste des fichiers.");
        System.out.println("3. Recevoir un fichier.");
        System.out.println("4. Exit.");
        System.out.print("\nSelectionner un numero: ");

        return stdin.readLine();
    }

    public static void listFile() {
    	try {
    		
    	//	Path path = Paths.get("C:/Users/hp/eclipse-workspace/first/src/project");
    	
    		 File curDir = new File("C:/Users/hp/eclipse-workspace/first/src/project1");
    		 File[] filesList = curDir.listFiles();
    	        for(File f : filesList){    	          
    	            if(f.isFile()){
    	                System.out.println(f.getName());
    	            }
    	        }
    	        
    	}catch(Exception e) {
            System.err.println("Entrer non valide" + e);
        }
    }
    
    public static void sendFile() {
        try {
        	System.out.print("Donner le nom de fichier: ");
            fileName = stdin.readLine();

            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];
            if(!myFile.exists()) {
            	System.out.println("Ce fichier n'existe pas..");
            	return;
            }

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);
		
            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("Le fichier "+fileName+" est envoyer au serveur.");
        } catch (Exception e) {
            System.err.println("Exceptionnnn: "+e);
        }
    }

    public static void receiveFile(String fileName) {
        try {
            int bytesRead;
            InputStream in = sock.getInputStream();

            DataInputStream clientData = new DataInputStream(in);

            fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream(fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[1024];
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }

            output.close();
            in.close();

            System.out.println("Le fichier "+fileName+" provient du serveur.");
        } catch (IOException ex) {
        	System.out.println("Exception: "+ex);
         }
    
    }
}
