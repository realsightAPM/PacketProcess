package src.globalinfo;

import java.util.LinkedList;
import java.util.List;

/*
 * 该类主要用于保存设置哪些字段来标示一个会话
 */
public class SessionFingermark {
	private List<String> filedsList = new LinkedList<String>();

	public SessionFingermark() {
		String[] sss = { "source", "color", "destination", "time" };
		for (String str : sss) {
			filedsList.add(str);
		}
	}

	public List<String> getFiledsList() {
		return filedsList;
	}

	public void setFiledsList(List<String> filedsList) {
		this.filedsList = filedsList;
	}

}
