package org.asechs.wheelwego.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.asechs.wheelwego.model.FoodTruckService;
import org.asechs.wheelwego.model.MypageService;
import org.asechs.wheelwego.model.vo.BookingVO;
import org.asechs.wheelwego.model.vo.ListVO;
import org.asechs.wheelwego.model.vo.MemberVO;
import org.asechs.wheelwego.model.vo.ReviewVO;
import org.asechs.wheelwego.model.vo.TruckVO;
import org.asechs.wheelwego.model.vo.WishlistVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
/**
 * 본인 이름
 *  수정 날짜 (수정 완료)
 * 대제목[마이페이지/푸드트럭/멤버/게시판/예약] - 소제목
 * ------------------------------------------------------
 * 코드설명
 * 
 * EX)
	박다혜
	 2017.06.21 (수정완료) / (수정중)
 	마이페이지 - 마이트럭설정
	---------------------------------
	~~~~~
  */
@Controller
public class FoodTruckController {
	@Resource(name="foodTruckServiceImpl")
	private FoodTruckService foodTruckService;
	@Resource(name="mypageServiceImpl")
	private MypageService mypageService; 

/*	*//** 	  
		정현지
		2017.06.21 (수정완료)
	 	푸드트럭 - 푸드트럭명으로 검색하기
	 	기능설명 : 푸드트럭명 검색(searchFoodtruckName)리스트를 TruckVO 객체로 받아온다
	 			return 값은 푸드트럭 검색 리스트 페이지로 보낸다
	  *//*
	@RequestMapping("searchFoodTruckList.do")
	public ModelAndView searchFoodTruckList(String searchFoodtruckName){
		List<TruckVO> searchList = foodTruckService.searchFoodTruckList(searchFoodtruckName);
		return new ModelAndView("foodtruck/foodtruck_location_select_list.tiles", "pagingList", searchList);
	}*/
	
	
	/**
	 * 박다혜
	 * 2017.06.21 (수정 중)
	 * 푸드트럭 - 푸드트럭 명으로 검색하기
	 * ------------------------------------------------------------------
	 * filteringOption이 null이라면 최신순 필터링을 적용한다.
	 * filteringOption과 현재 페이지 번호, 검색 조건에 해당하는 푸드트럭 리스트를 반환받고
	 * modelAndView 객체에 푸드트럭 리스트를 설정한다.
	 *  또한 페이징시를 대비해 검색조건과 filteringOption도 함께 설정한다.
	 * 
	 * 
	 * @param name
	 * @param pageNo
	 * @param latitude
	 * @param longitude
	 * @param request
	 * @param option
	 * @return
	 */
	@RequestMapping("searchFoodTruckByName.do")
	public ModelAndView searchFoodTruckByName(String name, String pageNo, String latitude, String longitude,HttpServletRequest request,String filteringOption) {
		if(filteringOption==null)
			filteringOption="ByDate";
		ModelAndView modelAndView = new ModelAndView("foodtruck/foodtruck_location_select_list.tiles");		
		ListVO listVO =foodTruckService.filtering(filteringOption, name, pageNo, latitude, longitude,null);
		modelAndView.addObject("pagingList", listVO);
		modelAndView.addObject("name", name);
		modelAndView.addObject("filteringOption", filteringOption);		
		
		HttpSession session=request.getSession(false);
		if(session != null){
			MemberVO memberVO=(MemberVO)session.getAttribute("memberVO");
			if(memberVO != null){
				modelAndView.addObject("heartWishlist",mypageService.heartWishList( memberVO.getId()));
			}
		}
		modelAndView.addObject("GPSflag", "false");	
		return modelAndView;
	}
	/**
	 * 황윤상 GPS 기반 푸드트럭수동검색
	 * @param name
	 * @return
	 */
	@RequestMapping("searchFoodTruckByGPS.do")
	public ModelAndView searchFoodTruckByGPS(String latitude, String longitude, String pageNo,String option,HttpServletRequest request) {
		if(option==null)
			option="ByDate";
		TruckVO gpsInfo = new TruckVO();
		
		gpsInfo.setLatitude(Double.parseDouble(latitude));
		gpsInfo.setLongitude(Double.parseDouble(longitude));
		
		ModelAndView modelAndView = new ModelAndView("foodtruck/foodtruck_location_select_list.tiles");
		//ListVO listVO1 = foodTruckService.getFoodTruckListByGPS(pageNo, gpsInfo);
		//System.out.println(listVO1);
		ListVO listVO =foodTruckService.filtering(option,null, pageNo, latitude, longitude,gpsInfo);
		HttpSession session=request.getSession(false);
		String id=null;
		List<WishlistVO> heartWishList=null;
		if(session != null){
			MemberVO memberVO=(MemberVO)session.getAttribute("memberVO");
			if(memberVO != null){
				id = memberVO.getId();
				heartWishList = mypageService.heartWishList(id);
				modelAndView.addObject("heartWishlist",heartWishList);
			}
		}
		modelAndView.addObject("pagingList", listVO);
		modelAndView.addObject("gpsInfo", gpsInfo);
		modelAndView.addObject("option", option);	
		modelAndView.addObject("flag", "true");	
		return modelAndView;
	}
	/** 	  
	정현지
	2017.06.21 (수정완료)
 	푸드트럭 - 푸드트럭 상세보기 
 	기능설명 : 1. 푸드트럭 번호(foodtruckNo)로 푸드트럭 상세정보를 TruckVO 객체로 받아온다
 			2. 푸드트럭 번호로 작성된 리뷰 리스트를 받아온다(리뷰 리스트 pagingBean 적용)
 			-> return 값은 푸드트럭 detail 페이지로 보낸다
  */
	   @RequestMapping("foodtruck/foodTruckAndMenuDetail.do")
	   public ModelAndView foodTruckAndMenuDetail(String foodtruckNo,String reviewPageNo, String latitude, String longitude, HttpServletRequest request){
	      TruckVO truckDetail = foodTruckService.foodTruckAndMenuDetail(foodtruckNo);
	      String bookingPossible = "no";
	      List<String> foodtruckNumberList = foodTruckService.getFoodtruckNumberList(new TruckVO(Double.parseDouble(latitude), Double.parseDouble(longitude)));
	      for (int i = 0; i < foodtruckNumberList.size(); i++)
	      {
	         if (foodtruckNumberList.get(i).equals(truckDetail.getFoodtruckNumber()))
	         {
	            bookingPossible = "ok";
	            break;
	         }            
	      }
	      ModelAndView mv= new ModelAndView();
	      mv.setViewName("foodtruck/foodtruck_detail.tiles");
	      HttpSession session=request.getSession(false);
	      String id=null;
	      if(session != null){
	         MemberVO memberVO=(MemberVO)session.getAttribute("memberVO");
	         if(memberVO != null){
	            id = memberVO.getId();
	            int wishlistFlag=mypageService.getWishListFlag(id, foodtruckNo);
	            mv.addObject("wishlistFlag",wishlistFlag);
	         }
	      }
	      mv.addObject("truckDetailInfo", truckDetail);
	      ListVO reviewList = foodTruckService.getReviewListByTruckNumber(reviewPageNo, foodtruckNo);
	      mv.addObject("reviewlist", reviewList);
	      mv.addObject("bookingPossible", bookingPossible);
	      return mv;
	   }
	   /** 	  
		정현지
		2017.06.21 (수정완료)
	 	푸드트럭 - 리뷰 작성
	 	기능설명 : 평점(grade), 리뷰 내용, 작성일자, 작성자(customerId), 리뷰를 작성한 푸드트럭 번호를 
	 			ajax 통신한 뒤, 통신이 성공하면 다시 푸드트럭 디테일 페이지로 이동한다(reloading)
	  */
	@RequestMapping(value = "afterLogin_foodtruck/registerReview.do", method = RequestMethod.POST)
	@ResponseBody
	public String registerReview(ReviewVO reviewVO){
		foodTruckService.registerReview(reviewVO); // 푸드 트럭 등록
		return "foodtruck/foodtruck_detail.tiles";
	}

	@RequestMapping(value = "afterLogin_foodtruck/registerBookMark.do", method = RequestMethod.POST)
	@ResponseBody
	public String registerBookMark(String id, String foodtruckNumber){
		String result = null;
		WishlistVO wishlistVO = new WishlistVO(foodtruckNumber, id);
		int count = foodTruckService.getBookMarkCount(wishlistVO);

		if(count != 0){
			result = "off";
			mypageService.deleteWishList(wishlistVO);
		}else{
			foodTruckService.registerBookMark(wishlistVO);
			result = "on";
		}
		return result;
}

	@RequestMapping("afterLogin_foodtruck/getBookMarkCount.do")
	@ResponseBody
	public String getBookMarkCount(WishlistVO wishlistVO){
		String result = null;
		int count = foodTruckService.getBookMarkCount(wishlistVO);
		if(count != 0){
			result = "off";			
		}else{
			result = "on";
		}
		System.out.println(result);
		return result;
	}

	/** 	  
	정현지
	2017.06.21 (수정완료)
 	예약 - 주문 후 주문 내역 확인
 	기능설명 : 주문한 메뉴를 BookingVO 객체로 받아오고(bvo), session으로부터 id를 받아와 포인트 내역을 받아온다(myPoint)
 			-> 주문 메뉴 / 포인트 내역을 주문 내역 페이지에서 확인할 수 있다
  */
	@RequestMapping(value = "afterLogin_foodtruck/foodtruck_booking_confirm.do", method = RequestMethod.POST)
	public ModelAndView foodtruck_booking_confirm(BookingVO bvo,HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("foodtruck/foodtruck_booking_confirm.tiles");
		MemberVO memberVO=(MemberVO)request.getSession(false).getAttribute("memberVO");
		mv.addObject("myPoint", mypageService.getMyPoint(memberVO.getId()));   
		mv.addObject("bvo",bvo);
		return mv;
	}
	/**
	 * 다혜 : 메뉴 예약하기
	 * @param bookingVO
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="afterLogin_foodtruck/bookingMenu.do")
	public String bookingMenu(BookingVO bookingVO,HttpServletRequest request,String resultPoint,String resultTotalAmount){
		foodTruckService.bookingMenu(bookingVO);
		String bookingNumber=bookingVO.getBookingNumber();
		mypageService.calPoint(resultPoint, resultTotalAmount, Integer.parseInt(bookingNumber));
		request.getSession(false).setAttribute("bookingNumber", bookingNumber);
		return "redirect:../foodtruck/foodtruck_booking_confirm_result.do";
	}

	@RequestMapping("afterLogin_foodtruck/getRecentlyBookingNumberBySellerId.do")
	@ResponseBody
	public Object getRecentlybookingNumberBySellerId(String id){
		int bookingNumber=foodTruckService.getRecentlyBookingNumberBySellerId(id);
		return bookingNumber;
	}
	@RequestMapping("afterLogin_foodtruck/getPreviousBookingNumberBySellerId.do")
	@ResponseBody
	public Object getPreviousbookingNumberBySellerId(String id){
		int bookingNumber=foodTruckService.getPreviousBookingNumberBySellerId(id);
		return bookingNumber;
	}
	@RequestMapping("afterLogin_foodtruck/getBookingStateBybookingNumber.do")
	@ResponseBody
	public String getBookingStateBybookingNumber(String bookingNumber,HttpServletRequest request){
		String state=foodTruckService.getBookingStateBybookingNumber(bookingNumber);
		if(state.equals("조리완료")){
			request.getSession(false).removeAttribute("bookingNumber");
			return "ok";
		}
		else
			return "fail";
	}
	   @RequestMapping("afterLogin_foodtruck/checkBooking.do")
	   @ResponseBody
	   public String checkBooking(HttpServletRequest request) {
	      System.out.println("실행됨");
	      HttpSession session = request.getSession();
	      MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");      
	      int count = mypageService.checkBookingState(memberVO.getId());
	      System.out.println(count);
	      return (count==0) ? "ok":"no";
	   }
	   
		@RequestMapping("afterLogin_foodtruck/getPreviousBookingNumberByCustomerId.do")
		@ResponseBody
		public String getPreviousBookingNumberByCustomerId(String id){
			String bookingNumber=foodTruckService.getPreviousBookingNumberByCustomerId(id);
			return bookingNumber;
		}
		
}
