package com.human.springboot.PaymentController;

import java.util.ArrayList;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.human.springboot.CartController.CartDAO;
import com.human.springboot.CartController.CartDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class PaymentController {
	@Autowired
	private PaymentDAO pdao;
	@Autowired
	private CartDAO cartdao;
	
	// 결제창
	@GetMapping("/payment")
	public String dopayment(HttpServletRequest req, Model model) {
	HttpSession session = req.getSession();
	String id=(String)session.getAttribute("id");
	PaymentDTO ship=pdao.selectShip(id);
	String cut[]=ship.getAddress().split("@");
	String cut2[] = cut[1].split("\\)");
	ship.setAddress1(cut[0]);
	ship.setAddress2(cut2[1]);
	ship.setAddress3(cut2[0].replace("(", ""));
	model.addAttribute("ship",ship); 
	return "payment";
	}
	
	 @PostMapping("/paymentInsert")
	 @ResponseBody
	 public String paymentInsert(HttpServletRequest req) {
	 try {
	 String id=req.getParameter("id");	 
	 ArrayList<CartDTO> alCart = cartdao.load_cart(id);
	 String coment=req.getParameter("coment");
	 String payWay=req.getParameter("payway");
	 System.out.println(id);
	 System.out.println(coment);
	 System.out.println(payWay);
	 for(int i=0; i<alCart.size(); i++) {
		 System.out.println(alCart.get(i).getCart_num());
		 int cartNum=alCart.get(i).getCart_num();
		 int qty=alCart.get(i).getQty();
		 int bookNum=alCart.get(i).getBook_num();
		 cartdao.delete_cart(cartNum);
		 pdao.insertOrderCart(cartNum,qty,id,bookNum);
		 pdao.insertPayment(cartNum,id,coment,payWay);
      }
	} catch(Exception e) { return "fail"; }
			
     return "ok";	
	 }
}

