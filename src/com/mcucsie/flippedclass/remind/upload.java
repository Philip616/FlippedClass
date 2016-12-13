package com.mcucsie.flippedclass.remind;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class upload {
	static public void uploadFile(String uploadUrl ,String srcPath)
	{
		String end = "\r\n";  
		String twoHyphens = "--";
		String boundary = "******";
		
		URL url;
		try 
		{
			url = new URL(uploadUrl);;
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			// Stream size settings of each transmission, can effectively prevent the mobile phone because of insufficient memory to collapse
		      // This method is used to request the text in advance do not know the content length is enabled when no internal buffer of HTTP flow. 
		      httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
		   // Allow the input and output streams
		      httpURLConnection.setDoInput(true);
		      httpURLConnection.setDoOutput(true);
		      httpURLConnection.setUseCaches(false);
		   // Using the POST method
		      httpURLConnection.setRequestMethod("POST");
		      httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
		      httpURLConnection.setRequestProperty("Charset", "UTF-8");
		      httpURLConnection.setRequestProperty("Content-Type",
		          "multipart/form-data;boundary=" + boundary);
		      DataOutputStream dos = new DataOutputStream( httpURLConnection.getOutputStream());
		      dos.writeBytes(twoHyphens + boundary + end);
		      dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
		    		  + srcPath.substring(srcPath.lastIndexOf("/") + 1)+ "\""+ end);
		      dos.writeBytes(end);
		      
		      FileInputStream fis = new FileInputStream(srcPath);
		      byte[] buffer = new byte[8192]; // 8k
		      int count = 0;		      
		      // ¶ÁÈ¡ÎÄ¼þ
		      while ((count = fis.read(buffer)) != -1)
		      {
		        dos.write(buffer, 0, count);
		      }
		      fis.close();
		      
		      dos.writeBytes(end);
		      dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
		      dos.flush();
		      InputStream is = httpURLConnection.getInputStream();//HTTP input, to obtain the returned results
		      InputStreamReader isr = new InputStreamReader(is, "utf-8");
		      BufferedReader br = new BufferedReader(isr);
		      String result = br.readLine();
		      System.out.println(result);
		      dos.close();
		      is.close();
		      
		} 
		catch (Exception e) 
		{

			e.printStackTrace();
			System.out.println("BAD");
		}
		
	}
}
