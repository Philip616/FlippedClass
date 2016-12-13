
package com.mcucsie.flippedclass.remind;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mcucsie.flippedclass.DataFromDatabase;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class myAsyncTask extends AsyncTask<Object, Integer, Void> {

	private ProgressDialog dialog = null;
	HttpURLConnection connection = null;
	DataOutputStream outputStream = null;
	DataInputStream inputStream = null;
	//the file path to upload
	String pathToOurFile = "";
	//the server address to process uploaded file
	//String urlServer = "http://10.0.2.2/rec.php";
	String urlServer = "";
	String lineEnd = "\r\n";
	String twoHyphens = "--";
	String boundary = "*****";

	File uploadFile;
	long totalSize; 
	private Context myContext;
	
	public myAsyncTask(Context context,String uploadUri) {
		DataFromDatabase getPath = new DataFromDatabase();
		urlServer = getPath.actionUrl;
		
		this.myContext=context;
		pathToOurFile=uploadUri;
		//pathToOurFile="/mnt/sdcard/amosdownload/100.docx";
		uploadFile = new File(pathToOurFile);
		totalSize = uploadFile.length(); // Get size of file, bytes
		

	}
	@Override
	protected void onPreExecute() {

		dialog = new ProgressDialog(myContext);
		dialog.setMessage("¤W¶Ç¤¤...");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setProgress(0);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Object... params) {

		long length = 0;
		int progress;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 256 * 1024;// 256KB

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					pathToOurFile));

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Set size of every block for post
			connection.setChunkedStreamingMode(256 * 1024);// 256KB

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ pathToOurFile + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				length += bufferSize;
				progress = (int) ((length * 100) / totalSize);
				publishProgress(progress);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);
			publishProgress(100);

			// Responses from the server (code and message)
			int serverResponseCode = connection.getResponseCode();
			String serverResponseMessage = connection.getResponseMessage();
			System.out.println(serverResponseCode+"");
			System.out.println(serverResponseMessage+"");

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
		} 
		catch (Exception ex)
		{
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {

		dialog.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		dialog.setProgress(values[0]);
	}
	

}
