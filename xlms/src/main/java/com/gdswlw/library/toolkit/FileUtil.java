package com.gdswlw.library.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtil {
	
	public static byte[] getBytesByFilePath(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	public static  String readFromFile(File file) {
		String ret = "";
		try {
			InputStream inputStream = new FileInputStream(file);
			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString).append("\n");
				}
				inputStream.close();
				ret = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return ret;
	}

	/**
	 * Write binary data byte to file
	 * @param file
	 * @param binaryData
	 * @return
	 */
	public static boolean byte2File(@NonNull File file,byte[] binaryData){
		if(file == null || !file.exists()){
			return false;
		}
		try {
			OutputStream output = new FileOutputStream(file);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
			bufferedOutput.write(binaryData);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return  false;
		}
	}
	public static double getFolderSize(File file) throws Exception {
		double size = 0;
		File[] fileList = file.listFiles();
		if (fileList == null || fileList.length == 0) {
			return size;
		}
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				size = size + getFolderSize(fileList[i]);
			} else {
				size = size + fileList[i].length();
			}
		}
		return size / 1048576;
	}
	
	public static boolean deleteFileDir(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					FileUtil.deleteFileDir(files[i]);
				}
			}
			file.delete();
			return true;
		}
		return false;
	}

	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	private static final int BUFF_SIZE = 1048576; // 1M Byte

	/**
	 * Copy a single file
	 * @param oldFile
	 * @param newFile
	 * @return
	 */
	public static boolean copyFile(File oldFile, File newFile) {
		if (oldFile == null && newFile == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			int bytesum = 0;
			int byteread = 0;
			if (oldFile.exists()) {
				// 文件存在时
				InputStream inStream = new FileInputStream(oldFile); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newFile);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				fs.flush();
				fs.close();
				inStream.close();
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Copy a single file
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean copyFile(String oldPath, String newPath) {
		return copyFile(new File(oldPath), new File(newPath));
	}

	/**
	 * Copy all files under the folder to the new folder
	 * @param oldFile
	 * @param newFile
	 * @return
	 */
	@SuppressWarnings("resource")
	public static boolean copyFiles(File oldFile, File newFile) {
		{
			if (!oldFile.exists()) {
				return false;
			}
			byte[] b = new byte[(int) oldFile.length()];
			if (oldFile.isFile()) {
				try {
					FileInputStream is = new FileInputStream(oldFile);
					FileOutputStream ps = new FileOutputStream(newFile);
					is.read(b);
					ps.write(b);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else if (oldFile.isDirectory()) {
				if (!oldFile.exists())
					oldFile.mkdir();
				String[] list = oldFile.list();
				for (int i = 0; i < list.length; i++) {
					copyFiles(oldFile.getAbsolutePath() + "/" + list[i],
							newFile.getAbsolutePath() + "/" + list[i]);
				}
			}
		}
		return true;
	}

	/**
	 * Copy all files under the folder to the new folder
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean copyFiles(String oldPath, String newPath) {
		return copyFiles(new File(oldPath), new File(newPath));
	}

	/**
	 * Delete all the files under the folder
	 * @param file
	 * @return
	 */
	public static boolean delFiles(File file) {
		if (file.isFile()) {
			file.delete();
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
			}
			for (File f : childFile) {
				delFiles(f);
			}
			// file.delete();
		}
		return true;
	}

	/**
	 * Delete all the files under the folder
	 * @param Path
	 * @return
	 */
	public static boolean delFiles(String Path) {
		return delFiles(new File(Path));
	}

	/**
	 * Get the list of all files in a format under the folder
	 * @param file
	 * @param suffixName
	 * @return
	 */
	public static List<String> getSimpleFileList(File file, String suffixName) {
		List<String> list = new ArrayList<String>();
		String path = "";
		if (!file.exists()) {
			return null;
		}
		// 创建fileArray名字的数组
		File[] fileArray = file.listFiles();
		// 如果传进来一个以文件作为对象的allList 返回0
		if (null == fileArray) {
			return null;
		}
		// 偏历目录下的文件
		for (int i = 0; i < fileArray.length; i++) {
			// 如果是个目录
			if (fileArray[i].isDirectory()) {
				// 递归调用
				list.addAll(getSimpleFileList(fileArray[i].getAbsoluteFile(),
						suffixName));

			} else if (fileArray[i].isFile()) {
				// 如果是以“”结尾的文件
				if (suffixName == null
						|| fileArray[i].getName().endsWith(suffixName)) {
					// 展示文件
					path = fileArray[i].getAbsolutePath();
					Log.e("@@@@@", path);
					list.add(path);
				}
			}
		}
		return list;
	}

	/**
	 * Get the list of all files in a format under the folder
	 * @param path
	 * @param suffixName
	 * @return
	 */
	public static List<String> getSimpleFileList(String path, String suffixName) {
		return getSimpleFileList(new File(path), suffixName);
	}

	/**
	 * Generate the file according to the byte array
	 * @param bfile
	 * @param filePath
	 * @param fileName
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {
				// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Copy the data from the assets folder to the sd card
	 * @param context
	 * @param assetsNamee
	 * @param strOutFilePath
	 * @throws IOException
	 */
	public static void copyDataToSD(Context context, String assetsNamee,
			String strOutFilePath) throws IOException {
		InputStream myInput;
		OutputStream myOutput = new FileOutputStream(strOutFilePath + "/"
				+ assetsNamee);
		myInput = context.getAssets().open(assetsNamee);
		byte[] buffer = new byte[1024];
		int length = myInput.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = myInput.read(buffer);
		}
		myOutput.flush();
		myInput.close();
		myOutput.close();
	}

	/**
	 * Get the size of the folder
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	/**
	 * There are files in the folder
	 * @param file
	 * @return
	 */
	public static boolean havefile(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					havefile(files[i]);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Read text file
	 * @param strFilePath
	 * @return
	 * @throws IOException
	 */
	public static String ReadTxtFile(String strFilePath) throws IOException {
		String path = strFilePath;
		String content = ""; // 文件内容字符串
		// 打开文件
		File file = new File(path);
		// 如果path是传递过来的参数，可以做一个非目录的判断
		if (!file.isDirectory()) {
			InputStream instream = new FileInputStream(file);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;
				// 分行读取
				while ((line = buffreader.readLine()) != null) {
					content += line;
				}
				instream.close();
			}
		}
		return content;
	}

	/**
	 * Unzip the file to the specified directory
	 * @param zipFileName
	 * @param targetBaseDirName
	 * @throws IOException
	 */
	public static void upzipFile(String zipFileName, String targetBaseDirName)
			throws IOException {
		if (!targetBaseDirName.endsWith(File.separator)) {
			targetBaseDirName += File.separator;
		}

		// 根据ZIP文件创建ZipFile对象
		ZipFile myZipFile = new ZipFile(zipFileName);
		ZipEntry entry = null;
		String entryName = null;
		String targetFileName = null;
		byte[] buffer = new byte[4096];
		int bytes_read;
		// 获取ZIP文件里所有的entry
		Enumeration<?> entrys = myZipFile.entries();
		// 遍历所有entry
		while (entrys.hasMoreElements()) {
			entry = (ZipEntry) entrys.nextElement();
			// 获得entry的名字
			entryName = entry.getName();
			targetFileName = targetBaseDirName + entryName;
			if (entry.isDirectory()) {
				// 如果entry是一个目录，则创建目录
				new File(targetFileName).mkdirs();
				continue;
			} else {
				// 如果entry是一个文件，则创建父目录
				new File(targetFileName).getParentFile().mkdirs();
			}
			// 否则创建文件
			File targetFile = new File(targetFileName);
			// System.out.println("创建文件：" + targetFile.getAbsolutePath());
			// 打开文件输出流
			FileOutputStream os = new FileOutputStream(targetFile);
			// 从ZipFile对象中打开entry的输入流
			InputStream is = myZipFile.getInputStream(entry);
			while ((bytes_read = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytes_read);
			}
			// 关闭流
			os.close();
			is.close();
			myZipFile.close();
		}
	}

	/**
	 * Unzip the file to the specified directory
	 * @param resFile
	 * @param zipout
	 * @param rootpath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void zipFile(File resFile, ZipOutputStream zipout,
			String rootpath) throws FileNotFoundException, IOException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "UTF-8");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

	/**
	 * Compress the file of the target folder to the specified zip file
	 * @param sourceFilePath
	 * @param zipFilePath
	 * @param fileName
	 * @return Is compression successful?
	 * @throws IOException
	 */
	public static boolean fileToZip(String sourceFilePath, String zipFilePath,
			String fileName) throws IOException {
		boolean flag = false;
		File sourceFile = new File(sourceFilePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		if (sourceFile.exists() == false) {
		} else {
			File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
			File[] sourceFiles = sourceFile.listFiles();
			if (null == sourceFiles || sourceFiles.length < 1) {
			} else {
				fos = new FileOutputStream(zipFile);
				zos = new ZipOutputStream(new BufferedOutputStream(fos));
				byte[] bufs = new byte[1024 * 10];
				for (int i = 0; i < sourceFiles.length; i++) {
					// 创建ZIP实体,并添加进压缩包
					// if(sourceFiles[i].getName().contains(".p12")||sourceFiles[i].getName().contains(".truststore")){
					ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
					zos.putNextEntry(zipEntry);
					// 读取待压缩的文件并写进压缩包里
					fis = new FileInputStream(sourceFiles[i]);
					bis = new BufferedInputStream(fis, 1024 * 10);
					int read = 0;
					while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
						zos.write(bufs, 0, read);
					}
					bis.close();
					fis.close();
					// }
				}
				flag = true;
			}
			zos.close();
			fos.close();
		}
		return flag;
	}

	/**
	 * Write the string to the file
	 * @param filePath
	 * @param strContent
	 */
	public static void writeStr2File(String filePath,String strContent) {  
        FileOutputStream fileOutputStream=null;  
        BufferedOutputStream bufferedOutputStream=null;  
        try{  
            fileOutputStream=new FileOutputStream(filePath);  
            bufferedOutputStream =new BufferedOutputStream(fileOutputStream);  
            bufferedOutputStream.write(strContent.getBytes());  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }finally{  
            try {  
                bufferedOutputStream.flush();  
                fileOutputStream.close();  
                bufferedOutputStream.close();  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
    }

	/**
	 * 转换文件大小
	 * @param fileS
	 * @return
	 *
	 */
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 获取图片base64内容
	 * @param image 图片bitmap数据
	 * @param quality 压缩率 = 100 - quality 100代表不压缩
	 * @return
	 */
	public static String getImageBase64Content(Bitmap image, int quality){
		if(image == null){
			return "";
		}
		if(quality < 1 || quality > 100){
			quality = 100;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//读取图片到ByteArrayOutputStream
		image.compress(Bitmap.CompressFormat.JPEG, quality, baos); //参数如果为100那么就不压缩
		return Base64.encodeToString(baos.toByteArray(),Base64.NO_WRAP);
	}


	/**
	 * 序列化数据
	 * @param fileDir 文件保存目录
	 * @param filename  文件名
	 * @param data 数据
	 * @return
	 */
	public static void serilizenData(File fileDir,String filename,Object data) {
		if(fileDir == null || filename == null || data == null){
			return ;
		}
		ObjectOutputStream objectOutputStream = null;
		if(! fileDir.exists()){
			fileDir.mkdir();
		}
		File saveFile =  new File(fileDir.getPath()+"/"+filename);
		try {
			if(! saveFile.exists()){
				saveFile.createNewFile();
			}
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(saveFile));
			objectOutputStream.writeObject(data);
			objectOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 获取序列化数据
	 * @param filePath
	 * @return
	 */
	public static Object getSerilizeData(String filePath) {
		Object bean = null;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(
					new FileInputStream(
							new File(filePath)
					)
			);
			bean = objectInputStream.readObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bean;
	}


	/**
	 * 获取目录下的所有文件
	 * @param path
	 * @return
	 */
	public static File[] listDirectoryFiles(String path){
		File file = new File(path);
		if(file !=  null && file.isDirectory()){
			return file.listFiles();
		}
		return null;
	}

}
