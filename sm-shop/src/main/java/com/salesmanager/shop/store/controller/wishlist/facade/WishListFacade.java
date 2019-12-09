/**
 *
 */
package com.salesmanager.shop.store.controller.wishlist.facade;

import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.wishlist.WishList;
import com.salesmanager.shop.model.shoppingcart.PersistableShoppingCartItem;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import com.salesmanager.shop.model.wishlist.PersistableWishListItem;
import com.salesmanager.shop.model.wishlist.ReadableWishList;
import java.util.List;



public interface WishListFacade {

	/**
	 * Add item to shopping cart
	 * @param item
	 * @param store
	 * @param language
	 * @return
	 * @throws Exception
	 */
	ReadableWishList addToCart(PersistableWishListItem item, MerchantStore store,
      Language language) throws Exception;

	/**
	 * Removes a shopping cart item
	 * @param cartCode
	 * @param productId
	 * @param merchant
	 * @param language
	 * @return
	 * @throws Exception
	 */
	void removeWishListItem(String cartCode, Long productId, MerchantStore merchant,
      Language languag) throws Exception;

	/**
	 * Retrieves a shopping cart
	 * @param code
	 * @param store
	 * @param language
	 * @return
	 * @throws Exception
	 */
	ReadableWishList getByCode(String code, MerchantStore store, Language language) throws Exception;

	ReadableWishList modifyCart(String cartCode, PersistableWishListItem item, MerchantStore store,
			Language language) throws Exception;
}
