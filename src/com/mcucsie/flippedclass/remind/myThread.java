package com.mcucsie.flippedclass.remind;

import android.content.Context;

public class myThread extends Thread {

	private String uploadUrl;
	private String srcPath;
	
	public myThread(String uploadUrl, String srcPath) {
		super();
		this.uploadUrl = uploadUrl;
		this.srcPath = srcPath;
	}

	@Override
	public void run() {

		upload.uploadFile(this.uploadUrl, this.srcPath);
		super.run();
	}

}
