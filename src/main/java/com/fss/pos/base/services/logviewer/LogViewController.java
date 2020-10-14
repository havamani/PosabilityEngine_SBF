package com.fss.pos.base.services.logviewer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@RestController
//@Api(value="abc", description="For logging purpose")
public class LogViewController {

	static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public void download(HttpServletRequest request,
			HttpServletResponse response) {
		process(true, request, response);
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public void view(HttpServletRequest request, HttpServletResponse response) {
		process(false, request, response);
	}

	private void process(boolean download, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			String fileType = request.getParameter("file");
			if (fileType == null) {
				return;
			}
			String loggerName = "";
			String appenderName = "";
			String filename="";

			if (fileType.equals("out")) {
				appenderName = "OUTPUT";
				loggerName = "com.src.output";
				filename="Output";
			} else if (fileType.equals("err")) {
				appenderName = "ERROR";
				loggerName = "com.src.error";
				filename="Error";
			} else if (fileType.equals("fssc")) {
				appenderName = "FSSCONNECT";
				loggerName = "com.src.fssc";
				filename="FssConnect";
			} else if (fileType.equals("debug")) {
				appenderName = "DEBUG";
				loggerName = "com.src.debug";
				filename="Debug";
			} else {
				return;
			}

			
			
			//FileAppender file=new FileAppender();
			
			File f = new File(filename);

			if (download) {
				response.setContentType("application/force-download");
				response.setContentLength((int) f.length());
				response.setHeader("Content-Transfer-Encoding", "binary");
				response.setHeader("Content-Disposition",
						"attachment; filename=\"" + f.getName());
			}
			FileInputStream fis = new FileInputStream(f);
			OutputStream out = response.getOutputStream();
			IOUtils.copy(fis, out);
			out.flush();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
