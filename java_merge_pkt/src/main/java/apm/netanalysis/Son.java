package apm.netanalysis;

public class Son extends Father{

	protected String test = "HUHUHUHU";
	
	public void init(){
		super.test = test;
	}
	@Override
	void add() {
		init();
		System.out.println("!!!!!!!");
	}

}
