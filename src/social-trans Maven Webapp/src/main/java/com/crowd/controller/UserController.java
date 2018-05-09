package com.crowd.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.crowd.bean.AcceptTask;
import com.crowd.bean.Message;
import com.crowd.bean.ReceiveTask;
import com.crowd.bean.Role;
import com.crowd.bean.User;
import com.crowd.service.AcceptTaskService;
import com.crowd.service.MessageService;
import com.crowd.service.ReceiveTaskService;
import com.crowd.service.RoleService;
import com.crowd.service.UserService;
@Controller  
@RequestMapping(value = "user")  
public class UserController {
	
	    @Autowired  
	    private UserService userService;  
	    @Autowired
	    private RoleService roleService;
	    @Autowired
		private MessageService messageService;
	    @Autowired
		private ReceiveTaskService retaskService;
		@Autowired
		private AcceptTaskService acceptTaskService;

//	    @RequestMapping(value="query", method=RequestMethod.POST)
//	public ModelAndView selectUserById(@RequestParam("userId") String userId) {
//		User user = new User();
//		user = userService.selectUserById(userId);
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.addObject("user", user);
//		modelAndView.setViewName("result");
//		return modelAndView;
//	}
//	    
//	    @RequestMapping(value="query",method=RequestMethod.GET)
//	    public ModelAndView selectUserById1(User user){
//	    	ModelAndView mv =new ModelAndView();
//	    	mv.setViewName("regist");
//	    	return mv;
//	    }
	    //用户注册
	    @RequestMapping(value="register",method=RequestMethod.GET)
	    public ModelAndView AddUser1(User user)throws Exception{
	    	ModelAndView mv =new ModelAndView();
	    	mv.setViewName("register");
	    	return mv;
	    }
	    @RequestMapping(value="register",  method=RequestMethod.POST)
	    public String AddUser(User user)throws Exception{
		// 随机产生一个GUID
		ModelAndView mv = new ModelAndView();
		  boolean isExit = userService.verifyAccount(user.getAccount());
		System.out.println(isExit);
		if (isExit) {	
			if(user.getAccount()==""||user.getAccount().equals("")){
				mv.setViewName("register");
				mv.addObject("Accounterror", "用户ID不能为空！");
			}

			else if(user.getPassword()==""||user.getPassword().equals("")){
				mv.setViewName("register");
				mv.addObject("Passworderror", "用户密码不能为空！");
			}
			else{
			String userId = UUID.randomUUID().toString();
			user.setUsername(user.getAccount());
			user.setUserId(userId);
			int UserSuccess = userService.insertUser(user);
			User user1 =userService.selectUserById(userId);
			int RoleSuccess = roleService.addUserRole(userId,roleService.getRoleIdByRolename(user1.getRole()));
			if (UserSuccess > 0 && RoleSuccess > 0) {
				mv.addObject("User", user1);
				return "login";
			} else {
				mv.setViewName("register");
				mv.addObject("error", "注册失败");
				}
			}
		} else {
			mv.addObject("error", "用户ID已经注册！");
			return "register";
		}
		return "register";
	}
	    //更新用户信息
	    @RequestMapping(value="updateUser",method=RequestMethod.GET)
	    public String UpdateUser1(HttpSession session,Model model)throws Exception{
	    	String account=(String) session.getAttribute("account");
	    	if (account != null && account != "") {
	    	System.out.println(account);
	    	String userId = userService.getUserIdByAccount(account);
	    	System.out.println(userId);
	    	User user = userService.selectUserById(userId);
	    	System.out.println(user);
	    	String hobby = user.getHobby();
	    	System.out.println(hobby);
	    	model.addAttribute("user", user);
	    	return "updateUser";
	    	}else {
				model.addAttribute("error", "请登陆后查看");
				return "error";
			}
	    }
	    
	    @RequestMapping(value="updateUser", method=RequestMethod.POST)
	    public String UpdateUser(@ModelAttribute("user")User user, Model model,HttpSession session/*,@RequestParam(value = "headImage", required = false) MultipartFile headImage*/) throws Exception{
		// 验证密码
		System.out.println(user);
		
			String account = (String) session.getAttribute("account");
			String userId = userService.getUserIdByAccount(account);
			user.setUserId(userId);
			int isSuccess = userService.UpdateUserByUserId(user);
			if (isSuccess > 0) {
				model.addAttribute("user", "user");
			} else {
				model.addAttribute("error", "更新失败");
			}
		
		return "redirect:toIndex";
	}
	//查询用户
//	@RequestMapping("selectUser")
//	public ModelAndView SelectUser(String account)throws Exception{
//		ModelAndView mv=new ModelAndView();
//		if(account.equals("")||account==""){
//			mv.addObject("error", "查询ID不能为空");
//			mv.setViewName("redirect:index");
//		}
//		else{
//			String userId = userService.getUserIdByAccount(account);
//			User user = userService.selectUserById(userId);
//			if(user==null){
//				mv.addObject("error", "查询ID不存在！");
//				mv.setViewName("redirect:/index");
//			}else{
//				mv.addObject("user", user);
//				mv.setViewName("Userindex");
//			}
//		}
//		return mv;
//	}
	@RequestMapping("selectUser")
	public String SelectUser(String account,Model model)throws Exception{
		
		if(account.equals("")||account==""){
			model.addAttribute("error", "查询ID不能为空");
			return "redirect:/task/toIndex";
		}
		else{
			String userId = userService.getUserIdByAccount(account);
			User user = userService.selectUserById(userId);
			if(user==null){
				model.addAttribute("error", "查询用户不存在");
				return "redirect:/task/toIndex";
			}else{
				model.addAttribute("user",user);
				return "Userindex";
			}
		}
	}
	//用户主页
	@RequestMapping("userData")
	public ModelAndView UserData(HttpSession session){
		ModelAndView mv= new ModelAndView();
		String account= (String) session.getAttribute("account");
		String userId = userService.getUserIdByAccount(account);
		User user = userService.selectUserById(userId);
		mv.addObject("user", user);
		mv.setViewName("Userindex");
		return mv;
	}
	//用户登录
	 @RequestMapping(value="login",method=RequestMethod.GET)
	    public String login1(User user,HttpServletRequest request,HttpServletResponse response)throws Exception{
	    
	    	return "login";
//		 	HttpSession session = request.getSession(); 
//		 	session.setAttribute("account", "dam");
//			Cookie cookieAccount = new Cookie("accountCookie","dam");
//			cookieAccount.setMaxAge(3600); //Cookie保存时间
//			cookieAccount.setPath(request.getContextPath());
//			response.addCookie(cookieAccount);
//			return "redirect:/task/toIndex";
	    }
	@RequestMapping(value="login",method=RequestMethod.POST)
	public String Login(String account,String password,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		System.out.println(account);
		boolean isSuccess = userService.Userlogin(account, password);
		if(isSuccess){
		Cookie cookieAccount = new Cookie("accountCookie",account);
		session.setAttribute("account", account);
		cookieAccount.setMaxAge(3600); //Cookie保存时间
		cookieAccount.setPath(request.getContextPath());
		//创建用户密码Cookie对象
		Cookie cookiePassword = new Cookie("password",password);
		cookiePassword.setMaxAge(3600); //Cookie保存时间
		cookiePassword.setPath(request.getContextPath());
		//添加到客户端
		response.addCookie(cookieAccount);
		response.addCookie(cookiePassword);
		session.setAttribute("account", account);
		String userId = userService.getUserIdByAccount(account);
		User user = userService.selectUserById(userId);
		session.setAttribute("userId", userId);
		session.setAttribute("role", user.getRole());
		session.setAttribute("username",user.getUsername());	
		System.out.println(userId);
		List<Message> messageList = messageService.selectMessageByUserId(userId);
		List<Message> mList = new ArrayList<>();
		Iterator<Message> Lists = messageList.iterator();
		while(Lists.hasNext()){
			Message message = Lists.next();
			if(message.getState()==0){
				mList.add(message);
			}
		}
		System.out.println(mList);
		session.setAttribute("messageNum", mList.size());
		//创建用户密码Cookie对象
		System.out.println(isSuccess);
		
			return  "redirect:/task/toIndex";
		}else{
			return "login";
		}
		
	}
	//用户主页
		@RequestMapping("toIndex")
		public ModelAndView userIndex(HttpSession session,Model model,HttpServletRequest request){
			ModelAndView mv= new ModelAndView();
			String account= (String) session.getAttribute("account");
			String userId = userService.getUserIdByAccount(account);
			User user = userService.selectUserById(userId);
			mv.addObject("user", user);
			List<ReceiveTask> unfinishList = new ArrayList<>();
			List<ReceiveTask> finishList = new ArrayList<>();
			List<ReceiveTask> uploadList = new ArrayList<>();
			List<ReceiveTask> exitList = new ArrayList<>();
			List<ReceiveTask> pushList = new ArrayList<>();
			List<ReceiveTask> reList = new ArrayList<>();
			List<String> taskIdList = acceptTaskService
					.selectTaskIdByuserId(userId);
			if (taskIdList.size() != 0) {
				Iterator<String> ac = taskIdList.iterator();
				// hasNext是取值取的是当前值.他的运算过程是判断下个是否有值如果有继续.
				 
				while (ac.hasNext()) {
					String taskId = ac.next().toString();
					System.out.println(taskId);
					AcceptTask acd = acceptTaskService.selectStateByUTID(userId,
							taskId);
					ReceiveTask receiveTask = retaskService.selectTaskByTaskId(taskId);
					if (acd.getIsSubmit() != 1) {
						unfinishList.add(receiveTask);
					}else {
						finishList.add(receiveTask);
					}
					
				}
			}
			List<ReceiveTask> lists = retaskService
					.selectReceiveTaskByuserId(userId);
			if (lists != null && lists.size() > 0) {
				Iterator<ReceiveTask> ac = lists.iterator();
				System.out.println(ac);
				while (ac.hasNext()) {
					ReceiveTask reTask = ac.next();
					if (reTask.getIsChild() == 0&&reTask.getState()==0) {
						uploadList.add(reTask);
					}else if(reTask.getIsChild() == 0&&reTask.getState()==1){
						exitList.add(reTask);
					}
				}
			}else {
				model.addAttribute("uploadmsg", "您暂时未发布任何任务");
			}
			if (unfinishList.size() == 0) {
				model.addAttribute("unfinishmsg", "您暂时未接收任何任务!");
			}
			if (finishList.size() == 0) {
				model.addAttribute("finishmsg", "您暂时未提交任何任务!");
			}
			List<ReceiveTask> nList = retaskService.getNewsReceiveTaskList();
			List<ReceiveTask> newList = new ArrayList<>();
//			model.addAttribute("textList", text);
			
			
			if(nList != null && nList.size() > 0) {
				for(int i = 0;i<nList.size();i++) {
					ReceiveTask re = nList.get(i);
					if (re.getIsChild() == 0) {
						newList.add(re);
					}
					if(newList.size()>4)
						break;
				}
			}
			if (lists != null && lists.size() > 0 && account != null && account != "") {
				Iterator<ReceiveTask> ac = lists.iterator();
				System.out.println(ac);
				while (ac.hasNext()) {
					ReceiveTask reTask = ac.next();
					if (reTask.getState() == 0) {
						if (reTask.getIsChild() == 0) {
							reList.add(reTask);
						}
					}
				}
				String historyTrans = user.getHistoryTrans();
				String hobby = user.getHobby();
				Iterator<ReceiveTask> re = reList.iterator();
				if (historyTrans != null || hobby != null) {
					while (re.hasNext()) {
						ReceiveTask retask = re.next();
						String parent = retask.getParentId();
						String describe = retask.getDescription();
						if (historyTrans != null && pushList.size() < 5&&(parent==null||parent=="")) {
							if (historyTrans.contains(describe) || hobby.contains(describe)) {
								pushList.add(retask);
							}
						} else if (hobby != null && hobby.contains(describe)
								&& pushList.size() < 5&&(parent==null||parent=="")) {
							pushList.add(retask);
						}
					}
					model.addAttribute("pushList", pushList);
				} else {
					model.addAttribute("pushList", newList);
				}

			} else {
				model.addAttribute("pushList", newList);
			}
			String subError = (String) request.getParameter("subError");
			System.out.println(subError);
			model.addAttribute("subError", subError);
			model.addAttribute("newList", newList);
			mv.addObject("uploadList", uploadList);
			mv.addObject("exitList", exitList);
			mv.addObject("unfinishList", unfinishList);
			mv.addObject("finishList", finishList);
			mv.setViewName("userToIndex");
			return mv;
		}
	
	
	
}
