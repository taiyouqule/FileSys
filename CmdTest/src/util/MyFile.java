package util;

import java.io.Serializable;
import java.util.ArrayList;

public class MyFile implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	private String name; // 保存文件名字

	public MyFile() {

	}

	public MyFile(String name) {
		this.name = name;
	}

	public MyFile(String name, ArrayList<MyDiskBlock> blocklist) {
		this(name);
		this.blocklist = blocklist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private ArrayList<MyDiskBlock> blocklist = new ArrayList<MyDiskBlock>();

	public ArrayList<MyDiskBlock> getBlocklist() {
		return blocklist;
	}

	public void setBlocklist(ArrayList<MyDiskBlock> blocklist) {
		this.blocklist = blocklist;
	}

	public void clearBlocklist() {
		this.blocklist.clear();
	}

	public void remove(){
		for(MyDiskBlock one:blocklist){
			MyDisk.getInstance().deleteUsed(one.getId());
		}
		
	}
	
	/**
	 * 拷贝一个文件
	 * @param file
	 * @return
	 */
	public static MyFile copy(MyFile file){
		ArrayList<MyDiskBlock> newlist=new ArrayList<MyDiskBlock>();
		for(MyDiskBlock one:file.getBlocklist()){
			newlist.add(one.copy());
		}
		return new MyFile(file.getName(), newlist);
	}
	/**
	 * 一个文件较大，添加磁盘块以保存该文件内容
	 * 
	 * @param a
	 *            要添加到文件磁盘列表的磁盘块号
	 */
	public void addBlock(MyDiskBlock a) {
		this.blocklist.add(a);
	}
	
	public int getSize(){
		return blocklist.size()*MyDiskBlock.maxSize;
	}
	
}
