package com.reader.ScanSD;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanSD
{
	
	String FILE_TYPE = ".txt";
	private ArrayList<File> list;//动态数组 <泛型> <>内为数组的类型 file为一个类
	
	/*构造方法*/
	public ScanSD(String fileType)
	{
		FILE_TYPE = fileType;//文件名类型改变为参量
		list = new ArrayList<File>();//new那个动态数组
	}
	/*构造方法2*/
	public ScanSD()
	{
		list = new ArrayList<File>();
	}
	/*方法名为getMapData 参数为ArrayList类型 返回值为ArrayList类*/
	public ArrayList<Map<String, Object>> getMapData(ArrayList<File> list) 
	{
		ArrayList<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();//先定义要返回的值
		HashMap<String, Object> item;//定义一个哈希表
		
		int i = 0;
		String path;
		String name;
		for (i = 0; i < list.size(); i++)//遍历该文件数组
		{
			item = new HashMap<String, Object>();
			path = list.get(i).toString();//path储存该文件路径
			name = path.substring(path.lastIndexOf("/") + 1, path.length());//name储存该文件文件名
			item.put("ItemName", name);
			item.put("ItemPath", path);//将两者放入item哈希表中
			mData.add(item);//将这个映射对添加到要返回的数组中
		}
		return mData;//返回的是一个数组 第一个参数是路径 第二个是文件名
	}
	/*基本同上 参数多了两个int的开始和结束 用于控制getMapData的范围 该范围较小时使用该范围 超出输入表范围时 遍历全表*/
	public ArrayList<Map<String, Object>> getMapData(ArrayList<File> list, int start, int end) 
	{
		ArrayList<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();
		HashMap<String, Object> item;
		
		int i = 0;
		String path;
		String name;
		int tmp;
		if (end > list.size()) 
		{
			tmp = list.size();
		} else 
		{
			tmp = end;
		}
		
		for (i = start; i < tmp; i++) 
		{
			item = new HashMap<String, Object>();
			path = list.get(i).toString();
			name = path.substring(path.lastIndexOf("/") + 1, path.length());
			item.put("ItemName", name);
			item.put("ItemPath", path);
			mData.add(item);
		}
		return mData;
	}
	
	protected void getAllFiles(File root, String fileType) 
	{
		if (fileType == null) {
			fileType = FILE_TYPE;
		}
		File files[] = root.listFiles();
		if (files != null) {
			for (File f: files) {
				
				if (f.isDirectory()) {
					getAllFiles(f, fileType);
				} else {
					if (f.getName().indexOf(fileType) > 0) {
						this.list.add(f);
					}
				}
			}
		}
	}
	
	public ArrayList<File> getFileList() 
	{
		getAllFiles(new File("/sdcard"), null);
		return list;
	}
	
	public ArrayList<File> getFileList(String fileType) 
	{
		getAllFiles(new File("/sdcard"), fileType);
		return list;
	}
	
	public void setGetFileType(String fileType) 
	{
		FILE_TYPE = fileType;
	}
	
	public ArrayList<String> sgetFileList()
	{
		getAllFiles(new File("/sdcard"), null);
		ArrayList<String> mData = new ArrayList<String>();//先定义要返回的值
		
		int i = 0;
		String path;
		String name;
		for (i = 0; i < list.size(); i++)//遍历该文件数组
		{
			path = list.get(i).toString();//path储存该文件路径
			name = path.substring(path.lastIndexOf("/") + 1, path.length());//name储存该文件文件名
			mData.add(name);//将这个映射对添加到要返回的数组中
		}
		return mData;
	}
	
}
