package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

public class App 
{
    public static void main( String[] args )
    {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket client = serverSocket.accept();
                BufferedReader inDalClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String in = "";
                String percorso = "";
                String ext = "";
                do {
                    in = inDalClient.readLine();
                    System.out.println(in);
                    if (percorso.isEmpty() && (!in.isEmpty() || !in.equals(null))) {
                        percorso = in.split(" ")[1];
                        if (!percorso.startsWith("/")) {
                            percorso = "/" + percorso;
                        }
                        percorso = "htdocs" + percorso;
                        ext = percorso.split("\\.")[1];
                        percorso = percorso.split("\\.")[0];
                    }
                    if (in.isEmpty() || in.equals(null)) {
                        break;
                    }
                } while (true);
                sendFile(client, percorso + "." + ext, ext);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore durante l'instanza del server");
            System.exit(1);
        }  
    }
    
    private static void sendFile(Socket s, String file, String extFile) {
        try {
            try {
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                File myObj = new File(file);
                FileInputStream in = new FileInputStream(file);

                out.writeBytes("HTTP/1.1 200 OK" + "\n");
                System.out.println(myObj.length());
                out.writeBytes("Content-Length: " + myObj.length() + "\n");
                out.writeBytes("Server: Java HTTP Server from Grandi: 1.0" + "\n");
                out.writeBytes("Date: " + new Date(0) + "\n");
                out.writeBytes("Content-Type: " + getContentType(extFile) + "\n");

                out.writeBytes("\n");

                byte[] buf = new byte[8192];
                int n;

                while ((n = in.read(buf)) != -1) {
                    out.write(buf, 0, n);
                }

                out.close();
                in.close();

            } catch (Exception e) {
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeBytes("HTTP/1.1 404 Not found" + "\n");
            }
        } catch (Exception e) {
            System.out.println("errore");
        }
    }

    private static String getContentType(String ext) {
        String type = "";

        switch (ext) {
            case "html":
                type = "text/html;" + "charset=utf-8";
            break;
           
            case "jpg":
            case "png":
            case "jpeg":
            case "webp":
                type = "image/" + ext;
            break;
            
            case "css":
                type = "text/html";
            break;

            case "js":
                type = "application/js";
            break;

            case "ico":
                type = "favicon/ico";
            break;

            default:
                type = "text/plain";
            break;
        }

        return type;
    }
}
