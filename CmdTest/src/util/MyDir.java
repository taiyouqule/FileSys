package util;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class MyDir implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 目录名 */
	private String name;

	/** 父目录 */
	private MyDir fatherDir;
	/** 该目录下目录列表 */
	private Hashtable<String, MyDir> dirlist = new Hashtable<String, MyDir>();
	/** 该目录下文件列表 */
	private Hashtable<String, MyFile> filelist = new Hashtable<String, MyFile>();

	public MyDir(String name, Hashtable<String, MyDir> dirlist, Hashtable<String, MyFile> filelist) {
		this.name = name;
		this.dirlist = dirlist;
		this.filelist = filelist;
	}
	// /** 记录该目录所占用的新的磁盘块序号*/
	// private ArrayList<Integer> newusedblock=new ArrayList<Integer>();

	public Hashtable<String, MyDir> getDirlist() {
		return dirlist;
	}

	public Hashtable<String, MyFile> getFilelist() {
		return filelist;
	}

	public static Object cloneObject(Object obj) {
		Object objx = null;
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(obj);
			ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			ObjectInputStream in = new ObjectInputStream(byteIn);
			objx = in.readObject();

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("深度拷贝失败");
		}
		return objx;

	}

	public MyDir paste(MyDir save) {
		MyDir temp = pasteRecursion(this, save);
		getDirlist().put(save.getName(), temp);
		return temp;
	}

	private MyDir pasteRecursion(MyDir father, MyDir now) {
		if (now.isEmpty()) {
			return new MyDir(now.getName());
		} else {
			Hashtable<String, MyFile> files = now.getFilelist();
			Hashtable<String, MyFile> newFiles = new Hashtable<String, MyFile>();
			for (Entry<String, MyFile> oneFile : files.entrySet()) {
				String filename = oneFile.getKey();
				MyFile file = oneFile.getValue();

				ArrayList<MyDiskBlock> blocklist = file.getBlocklist();
				ArrayList<MyDiskBlock> newblocks = new ArrayList<MyDiskBlock>();
				for (MyDiskBlock oneBlock : blocklist) {
					MyDiskBlock tempsave = oneBlock.copy();
					newblocks.add(tempsave); // 拷贝磁盘块
				}
				MyFile newFile = new MyFile(filename, newblocks); // 重建新文件
				newFiles.put(filename, newFile);
			}
			Hashtable<String, MyDir> dirs = now.getDirlist();
			Hashtable<String, MyDir> newDirs = new Hashtable<String, MyDir>();
			for (Entry<String, MyDir> onedir : dirs.entrySet()) {
				MyDir kid = onedir.getValue();
				newDirs.put(onedir.getKey(), pasteRecursion(now, kid));
			}
			MyDir realNew = new MyDir(now.getName(), newDirs, newFiles);
			for (Entry<String, MyDir> onedir : newDirs.entrySet()) {
				onedir.getValue().setFatherDir(realNew); // 设置父目录
			}
			now.setFatherDir(father);
			return realNew;
		}
	}

	public boolean isEmpty() {
		if (this.dirlist.isEmpty() && this.filelist.isEmpty()) {
			return true;
		} else
			return false;
	}

	/**
	 * 判断能否粘贴该目录
	 * 
	 * @param a
	 * @return
	 */
	public boolean canPasteDir(MyDir a) {
		return !dirlist.containsKey(a.getName());
	}

	/**
	 * 判断能否粘贴该文件
	 * 
	 * @param a
	 * @return
	 */
	public boolean canPasteFile(MyFile a) {
		return !filelist.containsKey(a.getName());
	}

	public MyDir() {

	}

	public MyDir(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MyDir getFatherDir() {
		return fatherDir;
	}

	public void setFatherDir(MyDir fatherDir) {
		this.fatherDir = fatherDir;

	}

	/**
	 * 递归获得目录大小
	 * 
	 * @return
	 */
	public static int getSize(MyDir now) {
		if (now.isEmpty()) {
			return 0;
		} else {
			int sum = 0;
			Hashtable<String, MyFile> files = now.getFilelist();
			for (Entry<String, MyFile> oneFile : files.entrySet()) {
				MyFile file = oneFile.getValue();
				sum += file.getSize();
			}
			Hashtable<String, MyDir> dirs = now.getDirlist();
			for (Entry<String, MyDir> onedir : dirs.entrySet()) {
				MyDir kid = onedir.getValue();
				sum += getSize(kid);
			}
			return sum;
		}
	}

	/**
	 * 
	 * @param path 指定路径
	 * @param now  此文件夹下所有需要保存的列表，包括文件列表和目录列表
	 */
	public static void write(String path, MyDir now) {

		if (now.isEmpty()) {
			return;
		} else {
			Hashtable<String, MyFile> files = now.getFilelist();
			for (Entry<String, MyFile> oneFile : files.entrySet()) {
				MyFile file = oneFile.getValue();
				File f = new File(path + "/" + now.getName(), file.getName());
				if (!f.exists()) {
					try {
						f.createNewFile();
						StringBuffer sb=new StringBuffer();
						for(MyDiskBlock block: file.getBlocklist()){
							sb.append(block.getContent());
						}
						BufferedWriter bw=new BufferedWriter(new FileWriter(f));
						bw.write(sb.toString());
						bw.flush();
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			Hashtable<String, MyDir> dirs = now.getDirlist();
			for (Entry<String, MyDir> onedir : dirs.entrySet()) {
				MyDir kid = onedir.getValue();
				String tempPath = path + "/" + now.getName()+"/"+ kid.getName();
				File f = new File(tempPath);
				f.mkdirs();
				write(tempPath, kid);
			}
		}
	}

	public static void remove(MyDir now) {
		if (now.isEmpty()) {
			return;
		} else {
			Hashtable<String, MyFile> files = now.getFilelist();
			for (Entry<String, MyFile> oneFile : files.entrySet()) {
				MyFile file = oneFile.getValue();
				file.remove();
			}
			files.clear();
			Hashtable<String, MyDir> dirs = now.getDirlist();
			for (Entry<String, MyDir> onedir : dirs.entrySet()) {
				MyDir kid = onedir.getValue();
				remove(kid);
			}
			dirs.clear();
		}
	}

	/**
	 * 显示目录下所有文件和目录
	 * 
	 */
	public void ls() {
		int count = 0;
		Iterator<String> a = filelist.keySet().iterator();
		while (a.hasNext()) {
			MyFile inst = filelist.get(a.next());
			System.out.print(inst.getName() + "(文件)---大小:" + inst.getSize() + "     ");
			count++;
		}
		Iterator<String> b = dirlist.keySet().iterator();
		while (b.hasNext()) {
			MyDir inst = dirlist.get(b.next());
			System.out.print(inst.getName() + "(目录)---大小:" + getSize(inst) + "     ");
			count++;
		}
		if (count == 0)
			System.out.println("对不起，当前目录下并无文件");
		else
			System.out.println();
	}

	/**
	 * 跳转cd
	 * 
	 */
	public MyDir cd(String name) {

		return dirlist.get(name);
	}

	public MyDir cdReturn() {
		return fatherDir;
	}

	/**
	 * 根据目录名得到该目录
	 * 
	 * @param oldname
	 *            目录名，为String型
	 * 
	 */
	public MyDir getDir(String oldname) {
		return dirlist.get(oldname);
	}

	/**
	 * 添加目录
	 * 
	 * @param a
	 *            一个MyDir的实例
	 */
	public void addDir(MyDir a) {
		if (dirlist.containsKey(a.getName()))
			System.out.println("已经存在同名目录，操作失败");
		else
			dirlist.put(a.getName(), a);
	}

	/**
	 * 删除目录
	 * 
	 * @param dirname
	 *            String
	 */
	public void deleteDir(String dirname) {
		dirlist.remove(dirname);
	}

	/**
	 * 根据文件名得到该文件
	 * 
	 * @param filename
	 *            文件名，为String型
	 * 
	 */
	public MyFile getFile(String filename) {
		return filelist.get(filename);
	}

	/**
	 * 增加一个文件
	 * 
	 * @param filename
	 *            文件名，为String型
	 * 
	 */
	public void addFile(MyFile a) {
		if (filelist.containsKey(a.getName()))
			System.out.println("对不起，该目录下已经存在同名文件，操作失败");
		else
			filelist.put(a.getName(), a);
	}

	public void addFile(String filename, StringBuffer realcontent) {

		int point = 0;
		ArrayList<MyDiskBlock> newFileBlocklist = new ArrayList<MyDiskBlock>();

		ArrayList<Integer> thenw = new ArrayList<Integer>();
		int blocksize = MyDiskBlock.maxSize;
		int sizecount = 0;
		if (realcontent.length() > blocksize) {
			for (int i = 0; i < realcontent.length() - blocksize; i = i + blocksize) {
				StringBuffer op = new StringBuffer(realcontent.substring(i, i + blocksize));
				MyDiskBlock newblock = new MyDiskBlock(op);
				MyDisk.getInstance().addUsed(newblock);
				thenw.add(newblock.getId());
				newFileBlocklist.add(newblock);
				point = i;
				sizecount++;
			}
			StringBuffer rest = new StringBuffer(realcontent.substring(point + blocksize, realcontent.length()));
			sizecount++;
			MyDiskBlock ano = new MyDiskBlock(rest);
			MyDisk.getInstance().addUsed(ano); // 修改后的内容保存到磁盘
			thenw.add(ano.getId());
			newFileBlocklist.add(ano); // 保存到文件
		} else {
			StringBuffer shortone = new StringBuffer(realcontent.substring(0, realcontent.length()));
			MyDiskBlock one = new MyDiskBlock(shortone);
			sizecount++;
			MyDisk.getInstance().addUsed(one);
			thenw.add(one.getId());
			newFileBlocklist.add(one);
		}
		MyFile file = new MyFile(filename, newFileBlocklist);
		this.getFilelist().put(filename, file);
	}

	/**
	 * 删除文件
	 * 
	 * @param filename
	 *            String
	 */
	public void deleteFile(String filename) {
		filelist.remove(filename);
	}

}
