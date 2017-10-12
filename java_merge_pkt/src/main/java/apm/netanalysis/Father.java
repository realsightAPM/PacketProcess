package apm.netanalysis;

public abstract class Father {
String test ="%%%%%";
public void set(String newtest){
	this.test = newtest;
}
public void print(){
	add();
	System.out.println(test);
}
abstract void add();
}
