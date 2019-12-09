package com.salesmanager.core.business.wishlist;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.wishlist.WishList;
import com.salesmanager.core.model.wishlist.WishListItem;

public interface WishListService extends SalesManagerEntityService<Long, WishList> {


	void saveOrUpdate(WishList shoppingCart) throws ServiceException;

	WishList getById(Long id, MerchantStore store) throws ServiceException;

	WishList getByCode(String code, MerchantStore store) throws ServiceException;




	/**
	 * Populates a ShoppingCartItem from a Product and attributes if any
	 * 
	 * @param product
	 * @return
	 * @throws ServiceException
	 */
	WishListItem populateWishListItem(Product product) throws ServiceException;


	/**
	 * Removes a shopping cart item
	 * @param item
	 */
	void deleteShoppingCartItem(Long id);

}