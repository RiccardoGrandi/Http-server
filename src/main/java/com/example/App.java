package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
            System.out.println("server avviato");

            while (true) {
                Socket s = serverSocket.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                String line;
                line = in.readLine();
                String filePath = line.split(" ")[1];

                do {
                    line = in.readLine();
                    System.out.println(line);
                } while (!line.isEmpty());

                File file  = new File("htdocs" + filePath);
                if (file.exists()) {
                    sendBinaryFile(out, file);
                } else {
                    String msg = "File non trovato";
                    out.writeBytes("HTTP/1.1 404 Not Found\n");
                    out.writeBytes("Content-Length: " + msg.length() + "\n");
                    out.writeBytes("Server: Java HTTP Server from Grandi: 1.0" + "\n");
                    out.writeBytes("Date: " + new Date() + "\n");
                    out.writeBytes("Content-Type: text/plain; charset=utf-8\n");
                    out.writeBytes("\n");
                    out.writeBytes(msg);
                }

                s.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore durante l'instanza del server");
            System.exit(1);
        }  
    }
    
    private static void sendBinaryFile(DataOutputStream out, File file) throws IOException{
        out.writeBytes("HTTP/1.1 200 OK" + "\n");
        out.writeBytes("Content-Length: " + file.length() + "\n");
        out.writeBytes("Server: Java HTTP Server from Grandi: 1.0" + "\n");
        out.writeBytes("Date: " + new Date() + "\n");
        out.writeBytes("Content-Type: " + getContentType(file) + "charset=utf-8\n");
        out.writeBytes("\n");

        InputStream input = new FileInputStream(file);
        byte[] buf = new byte[8192];
        int n;
        while ((n = input.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        input.close();
    }

    private static String getContentType(File f) {
       String [] s = f.getName().split("\\.");
       String ext = s[s.length-1];
        switch (ext) {
            case "html":
                return "text/html;";
           
            case "png":
                return "image/png;";

            case "css":
                return "text/html;";

            case "js":
                return "application/js;";

            case "ico":
                return "favicon/ico;";

            default:
                return "";
        }
    }
}