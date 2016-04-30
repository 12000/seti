package httpserver_v1;

import java.io.*;
import java.net.*;

class HttpServer extends Thread
{
    private Socket s;
    
        public HttpServer(Socket s)
    {
        this.s = s;
        start();
    }

        public void run()
    {
        try
        {
            // входной поток данных
            InputStream is = this.s.getInputStream();
            // выходной поток - к браузеру
            OutputStream os = this.s.getOutputStream();

            byte buffer[] = new byte[65536];
            int r = is.read(buffer);
            System.out.printf("r = %d", r);
            
            // dannye zaprosa
            //String request = new String();
            //request = buf.toString();
            String request = new String(buffer, 0, r);
            System.out.printf("\n------------------------------------------------\n");
            System.out.println("Request messsge:");
            System.out.println(request);
            
            //---выделяем путь--------------------------------------------------
            String URI;
            int a = request.indexOf("\n\n", 0);
            System.out.println(a);
            if(a < 0){ 
                a = request.indexOf("\r\n\r\n", 0);
                System.out.println(a);
            }
            if(a > 0){ 
                request = request.substring(0, a);
                System.out.println(a);
            }
            a = request.indexOf("GET ", 0)+"GET ".length();
            if(a < "GET ".length()){ 
                System.out.println("Ошибка - нeправильный запрос");
                //посылаем сообщение о неправильном запросе
                String answer = "HTTP/1.1 400 Bad Request\n";
                os.write(answer.getBytes());
                s.close();
                return;
            }
            
            int e = request.indexOf(" ", a);
            if(e < 0) e = " ".length();
                URI = (request.substring(a, e)).trim();
                      
            System.out.println("URI0:");
            System.out.println(URI);
            
            URI = URI.substring(1); // удаление слеша
            System.out.println("URI1:");
            System.out.println(URI);
            
            // строка - нормальный путь
            String way = ".\\";
            char b;
            for(int i = 0; i < URI.length(); i++)
            {
                b = URI.charAt(i);
                if(b == '/')
                    way = way + "\\";
                else
                    way = way + b;
            }
            System.out.println(" ");
            System.out.println("Путь до файла");
            System.out.println(way);
            
            // открывается файл
            File f = new File(way);
            boolean access = !f.exists(); // проверка на доступность
            if(access)
            {
                System.out.println("Файл не найден");
                String answer = "HTTP/1.1 404 Not Found\n";
                os.write(answer.getBytes());
                s.close();
                return;
            }
            else{
                if(f.isDirectory()){
                    if(way.lastIndexOf("\\") == way.length()-1)
                        way = way + "first.html";
                
                    f = new File(way);
                    access = !f.exists();
                }
            }
            System.out.println("access");
            System.out.println(access);
            
            System.out.println("Last way");
            System.out.println(way);

            //ответ
            String mime = "text/html";
            //для разных типов файла
            int f_type = way.lastIndexOf(".");
            if(r > 0)
            {
                String s_type = way.substring(f_type);
                if(s_type.equalsIgnoreCase(".html"))
                    mime = "text/html";
                else if(s_type.equalsIgnoreCase(".htm"))
                    mime = "text/html";
                else if(s_type.equalsIgnoreCase(".gif"))
                    mime = "image/gif";
                else if(s_type.equalsIgnoreCase(".jpg"))
                    mime = "image/jpeg";
                else if(s_type.equalsIgnoreCase(".jpeg"))
                    mime = "image/jpeg";
                else if(s_type.equalsIgnoreCase(".bmp"))
                    mime = "image/x-xbitmap";
                
                System.out.println(s_type);
            }
            String answer = "HTTP/1.1 200 OK\n";
            
            /*entity-header  = Allow                   ; 
                         | Content-Base             ; 
                         | Content-Encoding         ; 
                         | Content-Language         ; 
                         | Content-Length           ; 
                         | Content-Location         ; 
                         | Content-MD5              ; 
                         | Content-Range            ; 
                         | Content-Type             ; 
                         | ETag                     ; 
                         | Expires                  ; 
                         | Last-Modified            ; 
                         | extension-header*/
            
            // в entity-header
            answer += "Content-Language: en\n";
            answer += "Content-Length: " + f.length() + "\n";
            answer += "Content-Type: " + mime + "\n\n";

            System.out.println("answer:");
            System.out.println(answer);
                        
            os.write(answer.getBytes());
            
            FileInputStream fis = new FileInputStream(way);
            int size = 1;
            while(size > 0)
            {
                size = fis.read(buffer);
                System.out.printf("В поток size=%d\n", size);
                if(size > 0){ 
                    os.write(buffer, 0, size);
                    System.out.println("Вывод в поток...");
                }
            }
            fis.close();          
            
            s.close();
                        
        }
        catch(Exception e)
        {e.printStackTrace();} // вывод исключений
    }
               
        public static void main(String args[])
    {
        try
        {
            int port = 8080;
            InetAddress addr = InetAddress.getByName("127.0.0.1");
            ServerSocket server = new ServerSocket(port, 0, addr);
            System.out.println("Server started!");

            // прослушка порта
            while(true)
            {
                HttpServer serv = new HttpServer(server.accept());
                //serv.run();
            }
        }
        catch(Exception e)
        {System.out.println("ОШИБКА : "+e);}  
    } 
}
