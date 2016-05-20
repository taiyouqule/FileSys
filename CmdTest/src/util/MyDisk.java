package util;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

public class MyDisk implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static MyDisk a = new MyDisk();
	private Hashtable<Integer,MyDiskBlock> usedlist;
	public int restNum = 100;
	
	/**  ��ǰ���̿�ָ�룬ֻ���������Է����̿��ͻ     */
	 int nowpoint=0;        

	private MyDisk() {
		this.usedlist = new Hashtable<Integer,MyDiskBlock>();
	}

	/**
	 * ���õ���ģʽ�õ���ǰ����ʵ��
	 * 
	 * @return
	 */
	public static synchronized MyDisk getInstance() {
		return a;
	}

	

	public Hashtable<Integer, MyDiskBlock> getUsedlist() {
		return usedlist;
	}

	public void setUsedlist(Hashtable<Integer, MyDiskBlock> usedlist) {
		this.usedlist = usedlist;
	}

	public int getRestNum() {
		return restNum;
	}

	/**
	 * 
	 * @param restNum
	 */
	public void setRestNum(int restNum) {
		this.restNum = restNum;
	}

	
	/**
	 * ��ĳһ�����̿��ϵ����ݱ��浽������
	 * @param a
	 *   ��Ҫ���浽�����ϵ�ĳһ���ſ��
	 */
	public void addUsed(MyDiskBlock a){
		a.setId(nowpoint);
		this.usedlist.put(nowpoint,a);
		this.restNum--;
		this.nowpoint++;
	}
	
	/**
	 * ���ƶ��̿��ϵ����ݴӴſ���ɾ��
	 * @param a
	 * �̿��
	 */
	public void deleteUsed(int a){
		this.usedlist.remove(a);
		this.restNum++;
	}
	
  /**
   * ��ӡ�Ѿ�ʹ�ù��Ĵ��̿��
   */
	public void showUsed() {
		System.out.println("--------------����Ϊ�Ѿ�ʹ�õĴ��̿��--------------");
		Iterator<Integer> it=usedlist.keySet().iterator();
		while(it.hasNext()) {
			System.out.print(usedlist.get(it.next()).getId() + "     ");
		}
		if (usedlist.size() > 0)
			System.out.println();
	}
	public void remove(int a){
		usedlist.remove(a);
	}

}
