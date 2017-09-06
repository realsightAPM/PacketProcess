package apm.netanalysis.info;

import java.util.LinkedList;
import java.util.List;

/*
 * 该类主要用于保存设置哪些字段来标示一个会话
 */
public class SessionFingermark {
   private List<String> filedsList = new LinkedList<String>();
   
   /*
    * 改写成读取配置文件
    */
   public void init(){
	   filedsList.add("source");
	   filedsList.add("destination");
   }
}
