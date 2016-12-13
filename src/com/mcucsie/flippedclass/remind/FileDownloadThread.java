
package com.mcucsie.flippedclass.remind;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;

/**
 * ゅン更摸
 * 
 * @author yangxiaolong
 * @2014-5-6
 */
public class FileDownloadThread extends Thread {

	private static final String TAG = FileDownloadThread.class.getSimpleName();

	/** 讽玡更琌ЧΘ */
	private boolean isCompleted = false;
	/** 讽玡更ゅン */
	private int downloadLength = 0;
	/** ゅン玂隔畖 */
	private File file;
	/** ゅン更隔畖 */
	private URL downloadUrl;
	/** 讽玡更絬祘ID */
	private int threadId;
	/** 絬祘更计沮 */
	private int blockSize;

	/**
	 * 
	 * @param url:ゅン更
	 * @param file:ゅン玂隔畖
	 * @param blocksize:更计沮
	 * @param threadId:絬祘ID
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

			int startPos = blockSize * (threadId - 1);//秨﹍竚
			int endPos = blockSize * threadId - 1;//挡竚
			//砞竚讽玡絬祘更癬翴沧翴
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
	 * 絬祘ゅン琌更Ч拨
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * 絬祘更ゅン
	 */
	public int getDownloadLength() {
		return downloadLength;
	}

}
