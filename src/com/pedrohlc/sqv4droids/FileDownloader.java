/**
    Copyright (C) 2012 Pedro Henrique Lara Campos, Felipe Rodrigues Varjão

    This file is part of SQV.

    SQV is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    SQV is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with SQV.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pedrohlc.sqv4droids;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {
	protected static final long dwlBufferLength = 32;
	protected static final String dwlSuffix = Main.dwlSuffix;
	
	private String location;
	private String destination;
	private FileOutputStream fos;
	private ReadableByteChannel rbc;
	private HttpURLConnection httpConn;
	private boolean finished = false;
	private long actual;
	private long size;
	
	public FileDownloader(String llocation, String ldestination){
		this.location = llocation;
		this.destination = ldestination;
	}
	
	public void start() throws MalformedURLException, IOException{
		finished = false;
		URL url = new URL(location);
		httpConn = (HttpURLConnection) url.openConnection();
		size = httpConn.getContentLength();
		InputStream rbs = httpConn.getInputStream();
		rbc = Channels.newChannel(rbs);
		fos = new FileOutputStream(destination + FileDownloader.dwlSuffix);
		if(size <= 0)
			size = Long.MAX_VALUE;
		actual = 0;
	}
	
	public boolean tick() throws IOException{
		long result = fos.getChannel().transferFrom(rbc, actual, dwlBufferLength);
		actual += result;
		if (result == 0){
			size = actual;
			finish();
			return true;
		}else
			return false;
	}
	
	public void finish() throws IOException{
		if(finished)
			return;
		else
			finished = true;
		fos.close();
	    rbc.close();
	    httpConn.disconnect();
	    Main.moveFileForced((destination + FileDownloader.dwlSuffix), destination);
	}
	
	public Long getSize(){ return this.size; }
	public Long getPosition(){ return this.actual; }
	public HttpURLConnection getConnection(){ return this.httpConn; }
	
	public static void instantDownloadFile(String location, String destination) throws MalformedURLException, IOException{
		/*URL url = new URL(location);
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		String tmpDestination = destination + FileDownloader.dwlSuffix;
	    FileOutputStream fos = new FileOutputStream(tmpDestination);
	    long result = 1;
	    while(result != 0){
	    	result = fos.getChannel().transferFrom(rbc, 0, 1024);
	    }
	    fos.close();
	    rbc.close();
	    Main.moveFileForced(tmpDestination, destination);*/
		FileDownloader fd = new FileDownloader(location, destination);
		fd.start();
		while(!fd.tick());
	}
}
