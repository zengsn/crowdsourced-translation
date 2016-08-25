package com.cst.dao.user.action;

import java.util.List;

import javax.annotation.Resource;

import com.cst.dao.user.entity.User;
import com.cst.dao.user.service.UserService;
import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {
	
	@Resource
	private UserService userService;
	private List<User> userList;//�û��б�
	private User user;
	private String[] selectedRow;//ɾ����ѡ

	//��ת��ҳ�����ַ�����ʾ
	//�б�ҳ��
	public String listUI(){
		userList = userService.findObjects();
		return "listUI";
	}
	//��ת������ҳ��
	public String addUI(){
		return "addUI";
	}
	//��������
	public String add(){
		if(user != null){
			userService.save(user);
		}
		return "list";
	}
	//��ת���༭ҳ��
	public String editUI(){
		if (user != null && user.getId() != null) {
			user = userService.findObjectById(user.getId());
		}
		return "editUI";
	}
	//����༭
	public String edit(){
		if(user != null){
			userService.update(user);
		}
		return "list";
	}
	
	//ɾ��
	public String delete(){
		if(user != null && user.getId() != null){
			userService.delete(user.getId());
		}
		return "list";
	}
	//����ɾ��
	public String deleteSelected(){
		if(selectedRow != null){
			for(String id: selectedRow){
				userService.delete(id);
			}
		}
		return "list";
	}
	
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String[] getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(String[] selectedRow) {
		this.selectedRow = selectedRow;
	}
}