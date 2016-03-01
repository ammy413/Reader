package com.reader.ScanSD;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanSD
{
	
	String FILE_TYPE = ".txt";
	private ArrayList<File> list;//��̬���� <����> <>��Ϊ��������� fileΪһ����
	
	/*���췽��*/
	public ScanSD(String fileType)
	{
		FILE_TYPE = fileType;//�ļ������͸ı�Ϊ����
		list = new ArrayList<File>();//new�Ǹ���̬����
	}
	/*���췽��2*/
	public ScanSD()
	{
		list = new ArrayList<File>();
	}
	/*������ΪgetMapData ����ΪArrayList���� ����ֵΪArrayList��*/
	public ArrayList<Map<String, Object>> getMapData(ArrayList<File> list) 
	{
		ArrayList<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();//�ȶ���Ҫ���ص�ֵ
		HashMap<String, Object> item;//����һ����ϣ��
		
		int i = 0;
		String path;
		String name;
		for (i = 0; i < list.size(); i++)//�������ļ�����
		{
			item = new HashMap<String, Object>();
			path = list.get(i).toString();//path������ļ�·��
			name = path.substring(path.lastIndexOf("/") + 1, path.length());//name������ļ��ļ���
			item.put("ItemName", name);
			item.put("ItemPath", path);//�����߷���item��ϣ����
			mData.add(item);//�����ӳ�����ӵ�Ҫ���ص�������
		}
		return mData;//���ص���һ������ ��һ��������·�� �ڶ������ļ���
	}
	/*����ͬ�� ������������int�Ŀ�ʼ�ͽ��� ���ڿ���getMapData�ķ�Χ �÷�Χ��Сʱʹ�ø÷�Χ ���������Χʱ ����ȫ��*/
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
		ArrayList<String> mData = new ArrayList<String>();//�ȶ���Ҫ���ص�ֵ
		
		int i = 0;
		String path;
		String name;
		for (i = 0; i < list.size(); i++)//�������ļ�����
		{
			path = list.get(i).toString();//path������ļ�·��
			name = path.substring(path.lastIndexOf("/") + 1, path.length());//name������ļ��ļ���
			mData.add(name);//�����ӳ�����ӵ�Ҫ���ص�������
		}
		return mData;
	}
	
}
