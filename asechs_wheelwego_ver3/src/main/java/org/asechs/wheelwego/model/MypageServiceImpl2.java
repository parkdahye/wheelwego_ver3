package org.asechs.wheelwego.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.asechs.wheelwego.model.vo.BookingDetailVO;
import org.asechs.wheelwego.model.vo.BookingVO;
import org.asechs.wheelwego.model.vo.FileManager;
import org.asechs.wheelwego.model.vo.FileVO;
import org.asechs.wheelwego.model.vo.FoodVO;
import org.asechs.wheelwego.model.vo.ListVO;
import org.asechs.wheelwego.model.vo.ReviewVO;
import org.asechs.wheelwego.model.vo.TruckVO;
import org.asechs.wheelwego.model.vo.WishlistVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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
@Service
public class MypageServiceImpl2 implements MypageService {
   @Resource
   private MypageDAO mypageDAO;
   @Resource
   private FoodTruckDAO foodtruckDAO;
	@Override
	public ListVO customerBookingList(String pageNo, String customerId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<BookingVO> getBookingList(int bookingNumber) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 김래현 황윤상
	   2017.06.21 수정완료
 	    마이페이지-포인트적립,사용 계산메서드
	 */
	@Override
	public void calPoint(String usePoint, String totalAmount, int bookingNumber) {
		System.out.println("service : "+usePoint);
		int _usePoint = 0;
		if (usePoint!=null &&usePoint!="")
			{
			_usePoint = Integer.parseInt(usePoint);
			minusPoint(_usePoint, bookingNumber);
			}
			addPoint(Integer.parseInt(totalAmount), bookingNumber);
	   }
	/**
	 * 김래현 황윤상
	   2017.06.21 수정완료
 	    마이페이지-포인트사용
	 */
	@Override
	public void minusPoint(int usePoint, int bookingNumber) {
		usePoint = -usePoint;
		HashMap<String, Integer> pointInfo = new HashMap<String, Integer>();
		pointInfo.put("point", usePoint);
		pointInfo.put("bookingNumber", bookingNumber);
		mypageDAO.minusPoint(pointInfo);
	   }
	/**
	 * 김래현 황윤상
	   2017.06.21 수정완료
 	    마이페이지-포인트적립
	 */
	@Override
	public void addPoint(int totalAmount, int bookingNumber) {
		HashMap<String, Object> pointInfo = new HashMap<String, Object>();
		pointInfo.put("point", Math.round(totalAmount * 0.05));
		pointInfo.put("bookingNumber", bookingNumber);
		mypageDAO.addPoint(pointInfo);
	}
	/**
	 * 김래현 황윤상
	   2017.06.21 수정완료
 	    마이페이지-보유포인트 조회
	 */
	@Override
	public int getMyPoint(String customerId) {
		return mypageDAO.getMyPoint(customerId);
	}
	
	@Override
	public List<WishlistVO> heartWishList(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 김래현 황윤상
	   2017.06.21 수정완료
 	    마이페이지-단골트럭리스트
	 */
	@Override
	public List<TruckVO> myWishList(String id) {
		return mypageDAO.myWishList(id);
	}
	/**
	 * 김래현 황윤상
	 * 2017.06.21 수정완료
	 * 마이페이지-단골트럭삭제
	 */
	@Override
	public void deleteWishList(WishlistVO wishlistVO) {
		mypageDAO.deleteWishList(wishlistVO);
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 * 마이페이지 - 마이트럭 등록
	 */
	@Override
	public void registerFoodtruck(TruckVO tvo, String uploadPath) {
	      MultipartFile truckFile=tvo.getFoodtruckFile(); 
	      String fileName=truckFile.getOriginalFilename();
	      if(fileName.equals("")==false){
	         try {
	        	 FileManager fm=new FileManager();
	            String renamedFile=fm.rename(truckFile,tvo.getFoodtruckNumber());
	            tvo.setFileVO(new FileVO(tvo.getFoodtruckName(), renamedFile));
	            mypageDAO.registerFoodtruck(tvo);  //트럭정보 등록
	            mypageDAO.saveFilePath(new FileVO(tvo.getFoodtruckNumber(), renamedFile));
	            fm.uploadFile(truckFile,uploadPath+renamedFile);
	         } catch (IOException e) {
	            e.printStackTrace();
	         } //서버에 전송
	      }else{
	         mypageDAO.registerFoodtruck(tvo);
	         mypageDAO.saveFilePath(new FileVO(tvo.getFoodtruckNumber(), "defaultTruck.jpg"));
	      }
		
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public TruckVO findtruckInfoByTruckNumber(String truckNumber) {
	       TruckVO truckVO=mypageDAO.findtruckInfoByTruckNumber(truckNumber);
	       truckVO.setAvgGrade(foodtruckDAO.findAvgGradeByTruckNumber(truckNumber));
	       truckVO.setWishlistCount(foodtruckDAO.findWishlistCountByTruckNumber(truckNumber));
	       return truckVO;
	}
	@Override
	public String findtruckNumberBySellerId(String sellerId) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public void updateMyfoodtruck(TruckVO truckVO, String uploadPath) {
	      MultipartFile truckFile=truckVO.getFoodtruckFile(); 
	      FileManager fm=new FileManager();
	      String fileName=truckFile.getOriginalFilename();
	      if(fileName.equals("")==false){
	         try {
	         String renamedFile=fm.rename(truckFile,truckVO.getFoodtruckNumber());
	         truckVO.setFileVO(new FileVO(truckVO.getFoodtruckNumber(), renamedFile));
	         mypageDAO.updateMyfoodtruck(truckVO);  //트럭정보 등록
	         mypageDAO.updateFilePath(truckVO.getFileVO()); //파일경로 등록
	            fm.uploadFile(truckFile, uploadPath+renamedFile);
	         } catch (IOException e) {
	            e.printStackTrace();
	         }
	      }else{
	         mypageDAO.updateMyfoodtruck(truckVO); 
	      }
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public List<FoodVO> showMenuList(String truckNumber) {
		return mypageDAO.showMenuList(truckNumber);
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public void updateMenu(TruckVO truckVO, String uploadPath) {
	      MultipartFile truckFile=truckVO.getFoodtruckFile(); 
	      FileManager fm=new FileManager();
	      String fileName=truckFile.getOriginalFilename();
	      if(fileName.equals("")==false){
	         try {
	         String renamedFile=fm.rename(truckFile,truckVO.getFoodtruckNumber());
	         truckVO.setFileVO(new FileVO(truckVO.getFoodtruckNumber(), renamedFile));
	         mypageDAO.updateMyfoodtruck(truckVO);  //트럭정보 등록
	         mypageDAO.updateFilePath(truckVO.getFileVO()); //파일경로 등록
	            fm.uploadFile(truckFile, uploadPath+renamedFile);
	         } catch (IOException e) {
	            e.printStackTrace();
	         }
	      }else{
	         mypageDAO.updateMyfoodtruck(truckVO); 
	      }		
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public void deleteMyTruck(String foodtruckNumber) {
		mypageDAO.deleteMyTruck(foodtruckNumber);		
	}
	@Override
	public ListVO showMyReviewList(String customerId, String reviewPageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void updateMyReview(ReviewVO reviewVO) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteMyReview(String reviewNo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ReviewVO findReviewInfoByReviewNo(String reviewNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public TruckVO getGPSInfo(String sellerId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setGPSInfo(TruckVO gpsInfo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ListVO getWishList(String pageNo, String id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getWishListFlag(String customerId, String foodtruckNumber) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListVO showMyContentByFreeList(String id, String contentPageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void updateBookingState(BookingVO bookingVO) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ListVO getBookingVO(String foodTruckNumber, String pageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<BookingDetailVO> getBookingDetailVO(BookingVO bookingVO) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void freeboardDeleteInMaypage(String contentNo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ListVO showMyContentBybusinessList(String id, String contentPageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void businessDeleteInMaypage(String contentNo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ListVO showMyContentByqnaList(String id, String contentPageNo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void qnaDeleteInMaypage(String contentNo) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int checkBookingState(String id) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public ListVO getPointListById(String id, String nowPage) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 박다혜
	 * 2017.06.21 수정중
	 */
	@Override
	public void registerMenuList(TruckVO truckVO, String uploadPath) {
		   List<FoodVO> foodList=truckVO.getFoodList();
		   String foodtruckNumber=truckVO.getFoodtruckNumber();
	      FileManager fm=new FileManager();
	      for(int i=0;i<foodList.size();i++){
	         try{
	            foodList.get(i).setFoodTruckNumber(foodtruckNumber); //트럭넘버를 세팅
	            foodList.get(i).setFileVO(new FileVO(foodtruckNumber, "defaultMenu.jpg"));
	            mypageDAO.registerMenu(foodList.get(i)); //메뉴를 등록한다.
	            MultipartFile foodFile=foodList.get(i).getMenuFile(); //메뉴사진받아와서
	            String renamedFile=fm.rename(foodFile,foodtruckNumber+"_"+foodList.get(i).getMenuId()); //파일 이름 수정
	            mypageDAO.updateMenuFilepath(new FileVO(foodList.get(i).getMenuId(), renamedFile)); //파일 경로 수정
	            fm.uploadFile(foodFile, uploadPath+renamedFile); //서버에 파일 업로드
	         }catch (Exception e) {
	            e.printStackTrace();
	         }
	      }		
	}
  
}