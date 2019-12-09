package com.salesmanager.core.business.wishlist;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.wishlist.WishListAttributeRepository;
import com.salesmanager.core.business.repositories.wishlist.WishListItemRepository;
import com.salesmanager.core.business.repositories.wishlist.WishListRepository;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.wishlist.WishList;
import com.salesmanager.core.model.wishlist.WishListAttributeItem;
import com.salesmanager.core.model.wishlist.WishListItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("wishListService")
public class WishListServiceImpl extends SalesManagerEntityServiceImpl<Long, WishList>
		implements WishListService {

	private WishListRepository wishListRepository;

	@Inject
	private ProductService productService;

	@Inject
	private WishListItemRepository wishListItemRepository;
	
	@Inject
	private WishListAttributeRepository wishListAttributeRepository;
	
	@Inject
	private PricingService pricingService;

	@Inject
	private ProductAttributeService productAttributeService;
	


	private static final Logger LOGGER = LoggerFactory.getLogger(
      WishListServiceImpl.class);

	@Inject
	public WishListServiceImpl(WishListRepository shoppingCartRepository) {
		super(shoppingCartRepository);
		this.wishListRepository = shoppingCartRepository;

	}


	/**
	 * Save or update a {@link ShoppingCart} for a given customer
	 */
	@Override
	public void saveOrUpdate(final WishList shoppingCart) throws ServiceException {
		if (shoppingCart.getId() == null || shoppingCart.getId().longValue() == 0) {
			super.create(shoppingCart);
		} else {
			super.update(shoppingCart);
		}
	}

	/**
	 * Get a {@link ShoppingCart} for a given id and MerchantStore. Will update
	 * the shopping cart prices and items based on the actual inventory. This
	 * method will remove the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public WishList getById(final Long id, final MerchantStore store) throws ServiceException {

		try {
			WishList shoppingCart = wishListRepository.findById(store.getId(), id);
			if (shoppingCart == null) {
				return null;
			}
			getPopulatedWishList(shoppingCart);

			if (shoppingCart.isObsolete()) {
				delete(shoppingCart);
				return null;
			} else {
				return shoppingCart;
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Get a {@link ShoppingCart} for a given id. Will update the shopping cart
	 * prices and items based on the actual inventory. This method will remove
	 * the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public WishList getById(final Long id) {

		try {
			WishList wishList = wishListRepository.findOne(id);
			if (wishList == null) {
				return null;
			}
			getPopulatedWishList(wishList);

			if (wishList.isObsolete()) {
				delete(wishList);
				return null;
			} else {
				return wishList;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Get a {@link ShoppingCart} for a given code. Will update the shopping
	 * cart prices and items based on the actual inventory. This method will
	 * remove the shopping cart if no items are attached.
	 */
	@Override
	@Transactional
	public WishList getByCode(final String code, final MerchantStore store) throws ServiceException {

		try {
			WishList shoppingCart = wishListRepository.findByCode(store.getId(), code);
			if (shoppingCart == null) {
				return null;
			}
			getPopulatedWishList(shoppingCart);

			if (shoppingCart.isObsolete()) {
				delete(shoppingCart);
				return null;
			} else {
				return shoppingCart;
			}

		} catch (javax.persistence.NoResultException nre) {
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		} catch (Exception ee) {
			throw new ServiceException(ee);
		} catch (Throwable t) {
			throw new ServiceException(t);
		}

	}


	@Transactional(noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	private WishList getPopulatedWishList(final WishList wishList) throws Exception {

		try {

			boolean cartIsObsolete = false;
			if (wishList != null) {

				Set<WishListItem> items = wishList.getLineItems();
				if (items == null || items.size() == 0) {
					wishList.setObsolete(true);
					return wishList;

				}

				// Set<ShoppingCartItem> shoppingCartItems = new
				// HashSet<ShoppingCartItem>();
				for (WishListItem item : items) {
					LOGGER.debug("Populate item " + item.getId());
					getPopulatedItem(item);
					LOGGER.debug("Obsolete item ? " + item.isObsolete());
					if (item.isObsolete()) {
						cartIsObsolete = true;
					}
				}

				// shoppingCart.setLineItems(shoppingCartItems);
				boolean refreshCart = false;
				Set<WishListItem> refreshedItems = new HashSet<WishListItem>();
				for (WishListItem item : items) {
/*					if (!item.isObsolete()) {
						refreshedItems.add(item);
					} else {
						refreshCart = true;
					}*/
					refreshedItems.add(item);
				}

				//if (refreshCart) {
					wishList.setLineItems(refreshedItems);
				    update(wishList);
				//}

				if (cartIsObsolete) {
					wishList.setObsolete(true);
				}
				return wishList;
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new ServiceException(e);
		}

		return wishList;

	}

	@Override
	public WishListItem populateWishListItem(final Product product) throws ServiceException {
		Validate.notNull(product, "Product should not be null");
		Validate.notNull(product.getMerchantStore(), "Product.merchantStore should not be null");

		WishListItem item = new WishListItem(product);

		item.setProductVirtual(product.isProductVirtual());

		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product);
		item.setItemPrice(price.getFinalPrice());
		return item;

	}

	@Transactional
	private void getPopulatedItem(final WishListItem item) throws Exception {

		Product product = null;

		Long productId = item.getProductId();
		product = productService.getById(productId);

		if (product == null) {
			item.setObsolete(true);
			return;
		}

		item.setProduct(product);

		if (product.isProductVirtual()) {
			item.setProductVirtual(true);
		}

		Set<WishListAttributeItem> cartAttributes = item.getAttributes();
		Set<ProductAttribute> productAttributes = product.getAttributes();
		List<ProductAttribute> attributesList = new ArrayList<ProductAttribute>();//attributes maintained
		List<WishListAttributeItem> removeAttributesList = new ArrayList<WishListAttributeItem>();//attributes to remove
		//DELETE ORPHEANS MANUALLY
		if ( (productAttributes != null && productAttributes.size() > 0) || (cartAttributes != null && cartAttributes.size() > 0)) {
			for (WishListAttributeItem attribute : cartAttributes) {
				long attributeId = attribute.getProductAttributeId().longValue();
				boolean existingAttribute = false;
				for (ProductAttribute productAttribute : productAttributes) {

					if (productAttribute.getId().longValue() == attributeId) {
						attribute.setProductAttribute(productAttribute);
						attributesList.add(productAttribute);
						existingAttribute = true;
						break;
					}
				}

				if(!existingAttribute) {
					removeAttributesList.add(attribute);
				}

			}
		}

		//cleanup orphean item
		if(CollectionUtils.isNotEmpty(removeAttributesList)) {
			for(WishListAttributeItem attr : removeAttributesList) {
				wishListAttributeRepository.delete(attr);
			}
		}

		//cleanup detached attributes
		if(CollectionUtils.isEmpty(attributesList)) {
			item.setAttributes(null);
		}



		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product, attributesList);
		item.setItemPrice(price.getFinalPrice());
		item.setFinalPrice(price);

		BigDecimal subTotal = item.getItemPrice().multiply(new BigDecimal(item.getQuantity().intValue()));
		item.setSubTotal(subTotal);

	}









	@Override
	@Transactional
	public void deleteShoppingCartItem(Long id) {
		wishListItemRepository.deleteById(id);
	}

}
