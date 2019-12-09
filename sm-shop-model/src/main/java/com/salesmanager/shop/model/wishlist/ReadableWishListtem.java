package com.salesmanager.shop.model.wishlist;

import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * compatible with v1 version
 * @author c.samson
 *
 */
public class ReadableWishListtem extends ReadableProduct implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal subTotal;
	private String displaySubTotal;
	private List<ReadableWishListAttribute> cartItemattributes = new ArrayList<ReadableWishListAttribute>();


	public BigDecimal getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	public String getDisplaySubTotal() {
		return displaySubTotal;
	}
	public void setDisplaySubTotal(String displaySubTotal) {
		this.displaySubTotal = displaySubTotal;
	}
	public List<ReadableWishListAttribute> getCartItemattributes() {
		return cartItemattributes;
	}
	public void setCartItemattributes(List<ReadableWishListAttribute> cartItemattributes) {
		this.cartItemattributes = cartItemattributes;
	}
	
	

}
