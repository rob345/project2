/**
* Web worker: an object of this class executes in its own new thread
* to receive and respond to a single HTTP request. After the constructor
* the object executes on its "run" method, and leaves when it is done.
*
* One WebWorker object is only responsible for one client connection.
* This code uses Java threads to parallelize the handling of clients:
* each WebWorker runs in its own thread. This means that you can essentially
* just think about what is happening on one client at a time, ignoring
* the fact that the entirety of the webserver execution might be handling
* other clients, too.
*
* This WebWorker class (i.e., an object of this class) is where all the
* client interaction is done. The "run()" method is the beginning -- think
* of it as the "main()" for a client interaction. It does three things in
* a row, invoking three methods in this class: it reads the incoming HTTP
* request; it writes out an HTTP header to begin its response, and then it
* writes out some HTML content for the response content. HTTP requests and
* responses are just lines of text (in a very particular format).
*
**/

import java.net.Socket;
import java.lang.Runnable;
import java.awt.Desktop;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class WebWorker implements Runnable
{

private Socket socket;
String filething = ""; 
byte[] j = null; 

/**
* Constructor: must have a valid open socket
**/
public WebWorker(Socket s)
{
   socket = s;
}

/**
* Worker thread starting point. Each worker handles just one HTTP
* request and then returns, which destroys the thread. This method
* assumes that whoever created the worker created it with a valid
* open socket object.
**/
public void run()
{
   System.err.println("Handling connection...");
   
   try {
      
      InputStream  is = socket.getInputStream();
      OutputStream os = socket.getOutputStream();
      
      String contentc = readHTTPRequest(is);
       
      writeHTTPHeader(os,filething);
      writeContent(os, contentc, j);
      os.flush();
      socket.close();
   } catch (Exception e) {
      System.err.println("Output error: "+e);
   }
   System.err.println("Done handling connection.");
   return;
}

/////////////////////////////////////////////////////////////////////////////////////////////////

private boolean fileread(String html){
    File file =new File(html);
    if (file.exists()){
        System.err.println("File is found");
        return true;
    }
       
    System.err.println("File not found");
    return false;
       
}

/////////////////////////////////////////////////////////////////////////////////////////////

private String readhtml(String html) {
    String content = "";
    String date = "<cs371date>";
    String sid = "<cs371server>";   
    LocalDate localDate = LocalDate.now(); 
   
   
   
   
    try {
        BufferedReader h= new BufferedReader(new FileReader(html));
        String line;
        while((line=h.readLine())!=null){
            if (line.contains(date)){
            	content += "Today's date: "+ DateTimeFormatter.ofPattern("MM/dd/yyy").format(localDate); 
            	 System.err.println(DateTimeFormatter.ofPattern("MM/dd/yyy").format(localDate));
            	
            }
              
            if (line.contains(sid)){
            	content += "My Server";
                System.err.println("My Server");
            }
            System.err.println(line);
            
            
           
            
            content += line;
           
        }
           
       
       
       
       
       
       
       
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
   
    return content;
   
   
}//end read html


////////////////////////////////////////////////////////////////////////////

 void readpic(String html) {
   //byte content = 0; 
    
   
   
   
   
    
        FileInputStream file;
		try {
			file = new FileInputStream(html);
		
        File picf= new File(html);
        
        //byte[] a = null; 
        j = new byte[(int)picf.length()];
        file.read(j);
        
      
        
        
        
        file.close(); 
      
        /*
        FileOutputStream fob = null; 
        File f;
        f = new File(html);
        fob = new FileOutputStream(f); 
        j = html.getBytes(); 
        fob.write(j);
        fob.flush(); 
        fob.close(); 
        		
       if (fob!=null)
    	   fob.close();
              
*/
         //   System.err.println(line);
            
            
           
		}
           // content += line;
        catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
      
          /* 
		try {
			FileOutputStream fout = new FileOutputStream(html);
			j = html.getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
           
		*/
       
       
       
       
       
       
    
   
   
   
    
   
   
}//end read pic



/////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////


/**
* Read the HTTP request header.
**/
private String readHTTPRequest(InputStream is)
{
   String line;
   String contentb = "";
   
   BufferedReader r = new BufferedReader(new InputStreamReader(is));
  
  
   try {
    line = r.readLine();
    String y = line;
    String[] arr = y.split(" ");
    
    String html = "."+arr[1];
    
    
  if (html.contains(".html")){
    System.err.println(html);
        filething = "text/html";
        fileread(html);
        
        contentb = readhtml(html);
        }//end if
        
        else if (html.contains(".gif")){
             filething = "image/gif";
             readpic(html);
             }//end else if
             
         else if (html.contains(".png")){
             filething = "image/png";
             readpic(html);
             }//end else if
             
          else if (html.contains(".jpeg")){
             filething = "image/jpeg";
            readpic(html);
             }//end else if


    
    
    
} catch (Exception e) {
    // TODO Auto-generated catch block
    System.err.println("Request error: "+e);
   
}
  

 
   while (true) {
      try {
         while (!r.ready())
             Thread.sleep(1);
         line = r.readLine();
         
        
         System.err.println("Request line: ("+line+")");
       
         if (line.length()==0) break;
      } catch (Exception e) {
         System.err.println("Request error: "+e);
         break;
      }  
   }
 
   

   return contentb;
}



//////////////////////////////////////////////////////////////////////////////////

/**
* Write the HTTP header lines to the client network connection.
* @param os is the OutputStream object to write to
* @param contentType is the string MIME content type (e.g. "text/html")
**/
private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
{
   Date d = new Date();
   DateFormat df = DateFormat.getDateTimeInstance();
   df.setTimeZone(TimeZone.getTimeZone("GMT"));
   os.write("HTTP/1.1 200 OK\n".getBytes());
   os.write("Date: ".getBytes());
   os.write((df.format(d)).getBytes());
   os.write("\n".getBytes());
   os.write("Server: Jon's very own server\n".getBytes());
   //os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
   //os.write("Content-Length: 438\n".getBytes());
   os.write("Connection: close\n".getBytes());
   os.write("Content-Type: ".getBytes());
   os.write(contentType.getBytes());
   os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
   return;
}

/**
* Write the data content to the client network connection. This MUST
* be done after the HTTP header has been written out.
* @param os is the OutputStream object to write to
**/
private void writeContent(OutputStream os, String s, byte[] b) throws Exception
{
 
   os.write("<html><head></head><body>\n".getBytes());
   os.write("<h3>My web server works!</h3>\n".getBytes());
  
   
   os.write("</body></html>\n".getBytes());
   if(filething.equals("text/html"))
   os.write(s.getBytes());
   else
   os.write(b); 

}


} // end class