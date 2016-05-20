package util;

import java.io.Serializable;


public class MyDiskBlock implements Serializable{

	public static int maxSize=10;  //���̵�ҳ���10�ֽ�(һ�ֽڴ���StringBuffer�е�1λ)
	private static final long serialVersionUID = 1L;
       private int id;               //��¼���̿�����ڴ����ϵ�λ��
       private StringBuffer content=new StringBuffer();

	public MyDiskBlock() {

	}
	public MyDiskBlock(int id,StringBuffer sb){
		this.id=id;
		this.content=sb;
		
		
	}
       public MyDiskBlock(StringBuffer s){
    
    	   this.content=s;
       }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public  int getSize() {
		return content.length();
	}
	
	public StringBuffer getContent() {
		return content;
	}
	public void setContent(StringBuffer content) {
		this.content = content;
	}
	
	public boolean canAdd(){
		if(content.length()>maxSize) return false;
		else return true;
	}
	
	public MyDiskBlock copy(){
		 int nowpoint=MyDisk.getInstance().nowpoint;
		StringBuffer tempcontent=new StringBuffer(content);
		MyDiskBlock temp=new MyDiskBlock(nowpoint,tempcontent);
		MyDisk.getInstance().getUsedlist().put(nowpoint,temp);
		MyDisk.getInstance().restNum--;
		MyDisk.getInstance().nowpoint++;
		return temp;
	}
	public void delete(){
		MyDisk.getInstance().remove(id);
	}
}
