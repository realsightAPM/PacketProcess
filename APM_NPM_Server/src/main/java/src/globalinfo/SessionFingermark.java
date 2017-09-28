package src.globalinfo;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

/*
 * 该类主要用于保存设置哪些字段来标示一个会话
 */
@Component
public class SessionFingermark {
	private List<String> filedsList = new LinkedList<String>();
	
	public SessionFingermark() {
			filedsList.add(PktInfo.COLOR.getValue());
	}

	public List<String> getFiledsList() {
		return filedsList;
	}

	public void setFiledsList(List<String> filedsList) {
		this.filedsList = filedsList;
	}

}
