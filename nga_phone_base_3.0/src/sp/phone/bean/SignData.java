package sp.phone.bean;

import java.util.List;
import java.util.Map;

public class SignData {
	private String SignResult;
	private int Uid;//ǩ��ID
	private int Continued;//����ǩ������
	private int Sum;//�ܼ�ǩ��ʱ��
	private String Last_time;//�ϴ�ǩ��ʱ��
	private int Successrows;
	private int Availablerows;
	private int Totalrows;
	private boolean is_json_error,is_json_signsuccess;
	private boolean today_alreadysign;
	
	
	List<MissionDetialData> EntryList;

	public void set__today_alreadysign(boolean today_alreadysign){
		this.today_alreadysign=today_alreadysign;
	}
	public boolean get__today_alreadysign(){
		return today_alreadysign;
	}
	
	public void set__is_json_error(boolean is_json_error){
		this.is_json_error=is_json_error;
	}
	public boolean get__is_json_error(){
		return is_json_error;
	}

	public void set__is_json_signsuccess(boolean is_json_signsuccess){
		this.is_json_signsuccess=is_json_signsuccess;
	}
	public boolean get__is_json_signsuccess(){
		return is_json_signsuccess;
	}

	public void setEntryList(List<MissionDetialData> EntryList) {
		this.EntryList = EntryList;
	}
	
	public List<MissionDetialData> getEntryList() {
		return EntryList;
	}
	
	public void set__SignResult(String SignResult) {
		this.SignResult = SignResult;
	}
	public String get__SignResult() {
		return SignResult;
	}
	
	public void set__Successrows(int Successrows) {
		this.Successrows = Successrows;
	}
	public int get__Successrows() {
		return Successrows;
	}

	public void set__Totalrows(int Totalrows) {
		this.Totalrows = Totalrows;
	}
	public int get__Totalrows() {
		return Totalrows;
	}
	
	public void set__Availablerows(int Availablerows) {
		this.Availablerows = Availablerows;
	}
	public int get__Availablerows() {
		return Availablerows;
	}

	public void set__Last_time(String Last_time) {
		this.Last_time = Last_time;
	}
	public String get__Last_time() {
		return Last_time;
	}

	public void set__Uid(int Uid) {
		this.Uid = Uid;
	}
	public int get__Uid() {
		return Uid;
	}
	
	public void set__Continued(int Continued) {
		this.Continued = Continued;
	}
	public int get__Continued() {
		return Continued;
	}
	
	public void set__Sum(int Sum) {
		this.Sum = Sum;
	}
	public int get__Sum() {
		return Sum;
	}
	
}
