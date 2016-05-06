/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication5;

import com.sun.mail.pop3.POP3Folder;
import com.sun.mail.pop3.POP3Store;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Scanner;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;

public class POP3Client {
  
    private String host;// = "pop.mail.ru";
    private String provider = "pop3s";
    private String user;// = "qbqq2016";
    private String pass;
    int port;// = 995;// для SSL!!!!!!!!!!!!
    private Scanner in = new Scanner(System.in);
    
  public POP3Client(){
      
      Scanner in = new Scanner(System.in);

      System.out.println("Server: ");
      this.host = in.nextLine();
      System.out.println("Port: ");
      this.port = Integer.parseInt(in.nextLine());
      System.out.println("User: ");
      this.user = in.nextLine();
      System.out.println("Pass: ");
      this.pass = in.nextLine();
      
  }
  public static void main(String[] args) throws Exception {
    POP3Client c = new POP3Client();
    c.run();
  }
  
  public void run() throws NoSuchProviderException, MessagingException, IOException{
      Properties props = new Properties();
      Session session = Session.getDefaultInstance(props, null);
      POP3Store store = (POP3Store) session.getStore(this.provider);
      store.connect(this.host, this.port, this.user, this.pass);
      System.out.println("Connected!");
      
      POP3Folder inbox = (POP3Folder) store.getFolder("INBOX");
      if (inbox == null) {
        System.out.println("Error, No INBOX");
        System.exit(1);
      }
      inbox.open(Folder.READ_WRITE);
      Message[] messages = inbox.getMessages();
    
      while(true){
          System.out.printf("CMD:  ");
          String comand = in.nextLine();
          if(comand.equals("STAT")||comand.equals("stat")){
              //System.out.println("It's STAT cmd");
              System.out.printf("В ящике на данный момент %d писем\n", messages.length);
          }
          else if(comand.equals("LIST")||comand.equals("list")){
              //System.out.println("It's LIST cmd");
              for(int i=0; i<messages.length; i++){
                  System.out.printf("Сообщение №%d, дата: %s\n", i+1, messages[i].getSentDate());
                  System.out.println("----------------------------------------------------------");
              }
          }
          else if(comand.equals("RETR")||comand.equals("retr")){
              //System.out.println("It's RETR cmd");
              System.out.printf("Номер письма (в ящике %d писем): ", messages.length);
              int num = Integer.parseInt(in.nextLine());
              System.out.println("******************************************************************\n");
              try{
                  System.out.println(messages[num-1].getContent());
              }
              catch(Exception e){
                  System.out.println(e);
                  System.out.println("Неправильный номер!");
              }
              System.out.println("******************************************************************");
          }
          else if(comand.equals("DELE")||comand.equals("dele")){
              //System.out.println("It's DELE cmd");
              System.out.printf("Номер письма (в ящике %d писем): ", messages.length);
              int num = Integer.parseInt(in.nextLine());
              try{
                  messages[num-1].setFlag(Flags.Flag.DELETED, true);
              }
              catch(Exception e){
                  System.out.println(e);
                  System.out.println("Неправильный номер!");
              }
              System.out.printf("Сообщение номер %d помечено на удаление.", num); 
          }
          else if(comand.equals("TOP")||comand.equals("top")){
              //System.out.println("It's TOP cmd");
              //получить первые 5 строк
              int i=0;
              System.out.printf("Номер письма (в ящике %d писем): ", messages.length);
              int num = Integer.parseInt(in.nextLine());
              try{
                    String msg = messages[num-1].getContent().toString();
                    
                    System.out.println("*********************************************************\n");
                    System.out.println(msg.substring(0, msg.indexOf("\n")));
                    while(i<4){
                        // на случай, если строк в сообщении меньше 5
                        try{
                            msg = msg.substring(msg.indexOf("\n")+1, msg.length());
                            System.out.println(msg.substring(0, msg.indexOf("\n")));
                        }
                        catch(Exception e){
                            break; 
                        }
                        i++;
                    }
                    System.out.println("\n*********************************************************\n");
              }
              catch(Exception e){
                  System.out.println(e);
                  System.out.println("Неправильный номер!");
              }
          }
          else if(comand.equals("UIDL")||comand.equals("uidl")){
              //System.out.println("It's UIDL cmd");
              System.out.printf("Номер письма (в ящике %d писем): ", messages.length);
              int num = Integer.parseInt(in.nextLine());
              try{
                  System.out.println(inbox.getUID(messages[num-1]));
              }
              catch(Exception e){
                  System.out.println(e);
                  System.out.println("Неправильный номер!");
              }
          }
          else if(comand.equals("RSET")||comand.equals("rset")){
              //System.out.println("It's RSET cmd");
              for(int i=0; i<messages.length; i++){
                  messages[i].setFlag(Flags.Flag.DELETED, false);
              }
              System.out.println("Все метки на удаление сняты!");
          }
          else if(comand.equals("QUIT")||comand.equals("quit")){
              //System.out.println("It's QUIT cmd");
              inbox.close(true); //закрытие хранилища с применением флагов
              store.close();
              break;
          }
          else {
              System.out.println("UNKNOWN COMAND");
          }  
      }
  }
}

