import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

import util.AllUser;
import util.MyDir;
import util.MyDisk;
import util.MyDiskBlock;
import util.MyFile;

public class MainTest {
	public static String writePath="/Users/zhouliangjun/Downloads";    //�����·��
	public static void main(String[] args) throws IOException {
		System.out.println("��ӭ�����������ļ�ϵͳ,�����������û���");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String username;
		while ((username = br.readLine().trim()).equals(""))
			System.out.println("�û���������������������");
		MyDir nowdir = new MyDir(username);
		System.out.println("��ӭ����" + username
				+ "   �����Դ����µ�Ŀ¼���ļ����������������help�鿴����ָ��");
		MyDir dirsave = null;
		MyFile filesave = null;
		while (true) {
			System.out.print(username+"/");
			Stack<String> brid=new Stack<String>();
			
			MyDir k = new MyDir();
			k = nowdir;
			while (nowdir.getFatherDir() != null) // ѭ�����¸�Ŀ¼���̿�(���)�ʹ�С��ֱ����Ŀ¼
			{

				brid.push(nowdir.getName()+"/");
				nowdir = nowdir.getFatherDir();
			}
			nowdir = k;
			while(!brid.empty())
           System.out.print(brid.pop());
			
			String s = br.readLine().trim();

			

			if (s.equals("ls")) // --->Ŀ¼��ʾ,1
				nowdir.ls();
			else if (s.startsWith("cd") && !s.equals("cd ..")) { // --->��ת,1
				String real = s.substring(3, s.length());
				MyDir a = nowdir.cd(real);
				if (a != null) {
					MyDir b = nowdir;
					nowdir = a;
					nowdir.setFatherDir(b);
				} else
					System.out.println("�������Ŀ¼����������������");

			} else if (s.equals("cd ..")) { // --->����,1
				if (nowdir.cdReturn() != null)
					nowdir = nowdir.cdReturn();
				else
					System.out.println("�Ѿ��˵���Ŀ¼");

			} else if (s.startsWith("vim") && s.length() > 4) { // --->�����ļ�,1
				String real = s.substring(4, s.length());
				nowdir.addFile(new MyFile(real));

			} else if (s.startsWith("rmfile") && s.length() > 7) { // --->ɾ���ļ���1
				String real = s.substring(7, s.length());
				MyFile a = nowdir.getFile(real);
				if (a != null) {
					a.remove();
					nowdir.getFilelist().remove(real);  

					System.out.println("ɾ���ɹ�");
				} else
					System.out.println("�Բ��𣬸��ļ�������");
			} else if (s.startsWith("rnmfile") && s.length() > 8) { // --->�ļ�������
				String real = s.substring(8, s.length());
				MyFile a = nowdir.getFile(real);
				if (a != null) {
					System.out.println("�������µ��ļ���");
					String newname = br.readLine().trim();
					if (nowdir.canPasteFile(new MyFile(newname))) { // �ж��Ƿ����ͬ���ļ�
						if (!newname.equals("")) {
							nowdir.deleteFile(real);
							MyFile thnew = a;
							thnew.setName(newname);
							nowdir.addFile(thnew);
							System.out.println("�ļ��������ɹ�!!!");
						}
					} else
						System.out.println("�Բ����Ѿ�����ͬ���ļ�");

				} else
					System.out.println("�Բ��𣬸��ļ�������");
			} else if (s.equals("open")) { // --->�ļ��༭,1
				System.out.println("�������ļ�����");
				String filename = br.readLine();
				MyFile a = nowdir.getFile(filename);
				if (a != null) {
					System.out.println("��༭");
					ArrayList<MyDiskBlock> list = a.getBlocklist();

					StringBuffer all = new StringBuffer();
					for (MyDiskBlock one : list) {
						all.append(one.getContent());
					}
					System.out.println(all);     //չʾ�ļ�����
					nowdir.deleteFile(a.getName());   //�Ӹ��ļ�����ɾ��
					a.remove();         //�Ӵ������Ƴ�

					StringBuffer realcontent = new StringBuffer(br.readLine());
					nowdir.addFile(filename,realcontent);   //�����ļ������ļ����ݴ����ļ�
					System.out.println("�༭�ɹ�������");
				} else
					System.out.println("�Բ����ļ�����������");

			} else if (s.startsWith("vi") && s.length() > 3) { // ��ʾ�ļ�����,1
				String filename = s.substring(3, s.length());
				MyFile a = nowdir.getFile(filename);
				if (a != null) {
					ArrayList<MyDiskBlock> list = a.getBlocklist();

					StringBuffer all = new StringBuffer();
					for (MyDiskBlock one : list) {
						all.append(one.getContent());
					}

					System.out.println(all);
				} else
					System.out.println("�ļ�������������");

			} else if (s.startsWith("write") && s.length() > 6) { // --->Ŀ¼д��Ӳ��
				String real = s.substring(6, s.length());
				MyDir a = nowdir.getDir(real);
				if (a != null) {
					File f = new File(writePath,a.getName());
					f.mkdirs();
					MyDir.write(writePath, a);
					System.out.println("�ɹ�д��Ӳ��");
				} else
					System.out.println("д��Ӳ��ʧ��");
			}else if (s.startsWith("cpfile") && s.length() > 7) { // --->�ļ�����,1
				String real = s.substring(7, s.length());
				MyFile a = nowdir.getFile(real);
				if (a != null) {
					filesave=new MyFile();
					filesave = a;
					System.out.println("�����ļ������а�ɹ�");
				} else
					System.out.println("�Բ����ļ�����������");
			} else if (s.equals("ptfile")) { // --->�ļ�ճ��,1
				if (filesave != null) {
					ArrayList<MyDiskBlock> fileblocklist = filesave
							.getBlocklist();
					if (nowdir.canPasteFile(filesave)) { // �ж�Ŀ¼���Ƿ���ͬ���ļ�
						MyFile file=MyFile.copy(filesave);   //�����ļ�
						nowdir.getFilelist().put(file.getName(), file);
					} else
						System.out.println("��Ŀ¼���Ѿ���ͬ���ļ�������ճ��");
				}

				else
					System.out.println("���а���û���ļ�");
			}

			else if (s.startsWith("rnmdir") && s.length() > 7) { // --->Ŀ¼������,1
				String real = s.substring(7, s.length());
				MyDir old = nowdir.getDir(real);
				if (old != null) {
					System.out.println("�������µ�Ŀ¼��");
					String newname = br.readLine().trim();
					if (nowdir.canPasteDir(new MyDir(newname))) { // ȷ�ϸ�Ŀ¼����ͬ��Ŀ¼
						if (newname != "") {
							nowdir.deleteDir(real);
							MyDir thnew = old;
							thnew.setName(newname);
							nowdir.addDir(thnew);
							System.out.println("Ŀ¼�������ɹ�!!!");
						} else {
							System.out.println("�ļ���δ����");
						}
					} else
						System.out.println("�Ѿ�����ͬ��Ŀ¼");

				} else
					System.out.println("�Բ��𣬲����ڸ�Ŀ¼");

			} else if (s.startsWith("rmdir") && s.length() > 6) { // ɾ��Ŀ¼��1
				String real = s.substring(6, s.length());
				MyDir old = nowdir.getDir(real);
				if (old != null) {
					MyDir.remove(old);  
					nowdir.getDirlist().remove(real);  //�Ӹ��б���ɾ��
					System.out.println("Ŀ¼ɾ���ɹ�!");
				} else
					System.out.println("�Բ��𣬲����ڸ�Ŀ¼");
			}

			else if (s.startsWith("mkdir") && s.length() > 6) { // ����Ŀ¼��1
				String dirname = s.substring(6, s.length());
				MyDir dir = new MyDir(dirname);
				nowdir.addDir(dir);

			} else if (s.startsWith("cpdir") && s.length() > 6) { // ����Ŀ¼
				String real = s.substring(6, s.length());
				MyDir a = nowdir.getDir(real);
				if (a != null) {
					dirsave=(MyDir)MyDir.cloneObject(a);
					System.out.println("Ŀ¼�ɹ����Ƶ����а�");
				} else
					System.out.println("�Բ��𣬲����ڸ�Ŀ¼");
			} else if (s.equals("ptdir")) { // ճ��Ŀ¼
				if (dirsave != null) {
					if (nowdir.canPasteDir(dirsave)) { // �����ͬ��Ŀ¼��������ӣ�
						MyDir save = new MyDir();
						save = nowdir;
						nowdir.paste(dirsave);
						nowdir = save;
						System.out.println("ճ��Ŀ¼�ɹ���");
					} else
						System.out.println("Ŀ¼���Ѿ���ͬ��Ŀ¼���޷�ճ��");

				} else
					System.out.println("���а���û��Ŀ¼");
			} else if (s.equals("showdisk")) { // ��ʾ����ʹ�������1
				MyDisk.getInstance().showUsed();
			} else if (s.equals("help")) { // ��ʾ������1
				System.out
						.println("-------------------------����Ϊ��ʾ����ת����------------------------------");
				System.out
						.println("ls   ��ʾ�ļ�Ŀ¼             cd [dirname]:   ��ת��ָ��Ŀ¼                cd ..��ת���ϲ�Ŀ¼        ");
				System.out
						.println("---------------------------����ΪĿ¼����--------------------------------");
				System.out
						.println("mkdir [dirname]:����Ŀ¼       ptdir [dirname]:ճ��Ŀ¼       cpdir [dirname]:����Ŀ¼ ");
				System.out
						.println("rnmdir [dirname]:������Ŀ¼            rmdir [dirname]:ɾ��Ŀ¼");
				System.out
				.println("write [dirname]:����Ŀ¼�����µ������ļ�������Ӳ���� ");
				
				System.out
						.println("---------------------------����Ϊ�ļ�����--------------------------------");
				System.out
						.println("vim [filename]:�����ļ�       ptfile [filename]:ճ���ļ�       cpfile [filename]:�����ļ� ");
				System.out
						.println("rnmfile [filename]:�������ļ�            rmfile [filename]:ɾ���ļ�       vi [filename]:��ʾ�ļ�����");
				System.out.println("open ���ļ�     ֮��������ȷ���ļ������ܴ򿪽��б༭��");
				System.out
						.println("---------------------------����Ϊ���̲���--------------------------------");
				System.out.println("showdisk  :��ʾ����ʹ�����");
				System.out
						.println("---------------------------����Ϊ�˳���ϵͳ����----------------------------");
				System.out.println("exit");

				System.out
						.println("-----------------------------------------------------------------------------");
			} else if (s.equals("exit")) {
				System.out.println("���Ѿ��˳�ϵͳ");
				System.exit(0);
			} else if (s.equals("cls")) {
				for (int i = 0; i <= 30; i++) {
					System.out.println();
				}
			} else if (s.equals("ls?"))
				System.out.println("����ls,������ʾ��ǰĿ¼�����е�Ŀ¼�ļ�");
			else if (s.equals("cd?"))
				System.out.println("���� cd [dirname]:   ��ת��ָ��Ŀ¼");
			else if (s.equals("cd ..?"))
				System.out.println("����cd ..��ת���ϲ�Ŀ¼ ");
			else if (s.equals("mkdir?"))
				System.out.println("����mkdir [dirname]:����Ŀ¼ ");
			else if (s.equals("cpdir?"))
				System.out.println("����cpdir [dirname]:����Ŀ¼ ");
			else if (s.equals("ptdir?"))
				System.out.println("���� ptdir [dirname]:ճ��Ŀ¼ ");
			else if (s.equals("rnmdir?"))
				System.out.println("����rnmdir [dirname]:������Ŀ¼ ");
			else if (s.equals("rmdir?"))
				System.out.println("���� rmdir [dirname]:ɾ��Ŀ¼ ");

			else if (s.equals("ptfile?"))
				System.out.println("����ptfile [filename]:ճ���ļ�");
			else if (s.equals("cpfile?"))
				System.out.println("���� cpfile [filename]:�����ļ�  ");
			else if (s.equals("rnmfile?"))
				System.out.println("����rnmfile [filename]:�������ļ� ");
			else if (s.equals("rmfile?"))
				System.out.println("���� rmfile [filename]:ɾ���ļ�");
			else if (s.equals("open?"))
				System.out.println("����open ���ļ�     ֮��������ȷ���ļ������ܴ򿪽��б༭��");
			else if (s.equals("showdisk?"))
				System.out.println("����showdisk  :��ʾ����ʹ����� ");
			else if (s.equals("exit?"))
				System.out.println("����exit  :�˳���ϵͳ ");
			else if(s.equals("logout")){
				MyDir thisone = new MyDir();
				thisone = nowdir;
				while (nowdir.getFatherDir() != null) // ѭ�����¸�Ŀ¼���̿�(���)�ʹ�С��ֱ����Ŀ¼
				{

					brid.push(nowdir.getName()+"/");
					nowdir = nowdir.getFatherDir();
				}
               AllUser.getInstance().addUser(thisone);
               System.out.println("���Ѿ�ע���������Խ�������ѡ�񣺢�����exit�뿪�ļ�ϵͳ    ������login+�Ѿ����ڵ������û��˺� ���е�½  ������add+�µ��û���  ��������û�����¼");
               String in=br.readLine().trim();
               if(in.equals("exit"))  System.exit(0);
               else if (in.startsWith("login") && in.length() > 6) { //��½ԭ�е��˺�
   				String anouser = in.substring(6, in.length());
   				if(AllUser.getInstance().whetherExist(anouser)){
   					nowdir=AllUser.getInstance().getUserDir(anouser);
   	   				System.out.println("��ӭ����" + anouser
   	  						+ "   �����Դ����µ�Ŀ¼���ļ����������������help�鿴����ָ��");
   	   				username=anouser;
   				}else System.out.println("�����ڸ��û�");
   					
               }
               else if (in.startsWith("add") && in.length() > 4) { //�������˺Ų���¼
      				String newuser = in.substring(4, in.length());
      				if(!AllUser.getInstance().whetherExist(newuser)){
      					nowdir= new MyDir(newuser);
          				System.out.println("��ӭ����" + newuser
          						+ "   �����Դ����µ�Ŀ¼���ļ����������������help�鿴����ָ��");
          					AllUser.getInstance().addUser(nowdir);
          					username=newuser;
      				}
      				else System.out.println("�Ѿ�����ͬ���û�");
      					
                  }
               else {
            	   System.out.println("�޸�ָ�����������");
               }
            	   
			}
			else
				System.out.println("�޸�ָ�����������");
			
		}

	}

}
