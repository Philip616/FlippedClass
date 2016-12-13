
package com.mcucsie.flippedclass.remind;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;

/**
 * ���U����
 * 
 * @author yangxiaolong
 * @2014-5-6
 */
public class FileDownloadThread extends Thread {

	private static final String TAG = FileDownloadThread.class.getSimpleName();

	/** ��e�U���O�_���� */
	private boolean isCompleted = false;
	/** ��e�U�������� */
	private int downloadLength = 0;
	/** ���O�s���| */
	private File file;
	/** ���U�����| */
	private URL downloadUrl;
	/** ��e�U���u�{ID */
	private int threadId;
	/** �u�{�U���ƾڪ��� */
	private int blockSize;

	/**
	 * 
	 * @param url:���U���a�}
	 * @param file:���O�s���|
	 * @param blocksize:�U���ƾڪ���
	 * @param threadId:�u�{ID
	 */
	public FileDownloadThread(URL downloadUrl, File file, int blocksize,
			int threadId) {
		this.downloadUrl = downloadUrl;
		this.file = file;
		this.threadId = threadId;
		this.blockSize = blocksize;
	}

	@Override
	public void run() {

		BufferedInputStream bis = null;
		RandomAccessFile raf = null;

		try {
			URLConnection conn = downloadUrl.openConnection();
			conn.setAllowUserInteraction(true);

			int startPos = blockSize * (threadId - 1);//�}�l��m
			int endPos = blockSize * threadId - 1;//������m
			//�]�m��e�u�{�U�����_�I�B���I
			conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
			System.out.println(Thread.currentThread().getName() + "  bytes="
					+ startPos + "-" + endPos);

			byte[] buffer = new byte[1024];
			bis = new BufferedInputStream(conn.getInputStream());

			raf = new RandomAccessFile(file, "rwd");
			raf.seek(startPos);
			int len;
			while ((len = bis.read(buffer, 0, 1024)) != -1) {
				raf.write(buffer, 0, len);
				downloadLength += len;
			}
			isCompleted = true;
			Log.d(TAG, "current thread task has finished,all size:"
					+ downloadLength);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * �u�{���O�_�U������
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * �u�{�U��������
	 */
	public int getDownloadLength() {
		return downloadLength;
	}

}
