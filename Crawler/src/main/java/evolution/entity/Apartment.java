package evolution.entity;

import lombok.Data;

@Data
public class Apartment {
	private Double price;
	private Double area;
	private Double pricePerArea;
	private String url;
	private String phone;
	private String leaseType;// 租赁方式
	private String rentType;// 租金押付
	private String apartmentType;// 房型
	private Integer apartmentScore;
	private String address;
}
