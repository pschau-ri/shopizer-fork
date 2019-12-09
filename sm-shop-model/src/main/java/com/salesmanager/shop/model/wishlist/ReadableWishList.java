package com.salesmanager.shop.model.wishlist;

import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartItem;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Compatible with v1
 * @author c.samson
 *
 */
public class ReadableWishList extends ShoppingCartEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String code;
	private BigDecimal subtotal;


	private String displaySubTotal;
	private BigDecimal total;
	private String displayTotal;
	private int quantity;
	
	List<ReadableWishListtem> products = new ArrayList<ReadableWishListtem>();

	private Long customer;



	public Long getCustomer() {
		return customer;
	}



	public void setCustomer(Long customer) {
		this.customer = customer;
	}





	public List<ReadableWishListtem> getProducts() {
		return products;
	}



	public void setProducts(List<ReadableWishListtem> products) {
		this.products = products;
	}



	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}
	
	public BigDecimal getSubtotal() {
		return subtotal;
	}



	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}



	public String getDisplaySubTotal() {
		return displaySubTotal;
	}



	public void setDisplaySubTotal(String displaySubTotal) {
		this.displaySubTotal = displaySubTotal;
	}



	public BigDecimal getTotal() {
		return total;
	}



	public void setTotal(BigDecimal total) {
		this.total = total;
	}



	public String getDisplayTotal() {
		return displayTotal;
	}



	public void setDisplayTotal(String displayTotal) {
		this.displayTotal = displayTotal;
	}



	public int getQuantity() {
		return quantity;
	}



	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}




}
