/**
 *
 */
package com.salesmanager.shop.store.controller.wishlist.facade;

import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.ProductService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.ProductPriceUtils;
import com.salesmanager.core.business.wishlist.WishListService;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.wishlist.WishList;
import com.salesmanager.core.model.wishlist.WishListItem;
import com.salesmanager.shop.model.wishlist.PersistableWishListItem;
import com.salesmanager.shop.model.wishlist.ReadableWishList;
import com.salesmanager.shop.populator.wishlist.ReadableWishListPopulator;
import com.salesmanager.shop.store.api.exception.ResourceNotFoundException;
import com.salesmanager.shop.utils.ImageFilePath;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Umesh Awasthi
 * @version 1.0
 * @since 1.0
 */
@Service( value = "wishListFacade" )
public class WishListFacadeImpl
    implements WishListFacade
{

    
    private static final Logger LOG = LoggerFactory.getLogger(
        WishListFacadeImpl.class);

    @Inject
    private WishListService wishListService;

    @Inject
    ShoppingCartCalculationService shoppingCartCalculationService;

    @Inject
    private ProductPriceUtils productPriceUtils;

    @Inject
    private ProductService productService;

    @Inject
    private PricingService pricingService;

    @Inject
    private ProductAttributeService productAttributeService;
    
	@Inject
	@Qualifier("img")
	private ImageFilePath imageUtils;

    
    //used for api
	private com.salesmanager.core.model.wishlist.WishListItem createCartItem(WishList wishList,
			 PersistableWishListItem shoppingCartItem, MerchantStore store) throws Exception {

		Product product = productService.getById(shoppingCartItem.getProduct());

		if (product == null) {
			throw new Exception("Item with id " + shoppingCartItem.getProduct() + " does not exist");
		}

		if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new Exception("Item with id " + shoppingCartItem.getProduct() + " does not belong to merchant "
					+ store.getId());
		}

		com.salesmanager.core.model.wishlist.WishListItem item = wishListService
				.populateWishListItem(product);

		item.setQuantity(shoppingCartItem.getQuantity());
		item.setWishList(wishList);
		
		//set attributes
		List<com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute> attributes = shoppingCartItem.getAttributes();
		if (!CollectionUtils.isEmpty(attributes)) {
			for(com.salesmanager.shop.model.catalog.product.attribute.ProductAttribute attribute : attributes) {
				
				ProductAttribute productAttribute = productAttributeService.getById(attribute.getId());
				
				if (productAttribute != null
						&& productAttribute.getProduct().getId().longValue() == product.getId().longValue()) {
					
					com.salesmanager.core.model.wishlist.WishListAttributeItem attributeItem = new com.salesmanager.core.model.wishlist.WishListAttributeItem(
							item, productAttribute);

					item.addAttributes(attributeItem);
				}				
			}
		}

		return item;

	}





    private WishListItem getEntryToUpdate( final long entryId,
                                                                                                 final WishList cartModel )
    {
        if ( CollectionUtils.isNotEmpty( cartModel.getLineItems() ) )
        {
            for ( WishListItem shoppingCartItem : cartModel.getLineItems() )
            {
                if ( shoppingCartItem.getId().longValue() == entryId )
                {
                    LOG.info( "Found line item  for given entry id: " + entryId );
                    return shoppingCartItem;

                }
            }
        }
        LOG.info( "Unable to find any entry for given Id: " + entryId );
        return null;
    }





    private WishList getWishListModel( final String cartId,final MerchantStore store )
    {
        if ( StringUtils.isNotBlank( cartId ) )
        {
           try
            {
                return wishListService.getByCode( cartId, store );
            }
            catch ( ServiceException e )
            {
                LOG.error( "unable to find any cart asscoiated with this Id: " + cartId );
                LOG.error( "error while fetching cart model...", e );
                return null;
            }
            catch( NoResultException nre) {
           	//nothing
            }

        }
        return null;
    }



	
	@Override
	public ReadableWishList addToCart(PersistableWishListItem item, MerchantStore store,
			Language language) throws Exception {
		
		Validate.notNull(item,"PersistableShoppingCartItem cannot be null");
		
		//if cart does not exist create a new one

		WishList cartModel = new WishList();
		cartModel.setMerchantStore(store);
		cartModel.setWishListCode(uniqueShoppingCartCode());


		return readableShoppingCart(cartModel,item,store,language);
	}
	

	@Override
	public void removeWishListItem(String cartCode, Long productId,
	      MerchantStore merchant, Language language) throws Exception {
	    Validate.notNull(cartCode, "Shopping cart code must not be null");
	    Validate.notNull(productId, "product id must not be null");
	    Validate.notNull(merchant, "MerchantStore must not be null");
	    
	  
	    //get cart
	    WishList wishList = getWishListModel(cartCode, merchant);
	    
	    if(wishList == null) {
	      throw new ResourceNotFoundException("wishList code [ " + cartCode + " ] not found");
	    }
	    
	    Set<WishListItem> items = new HashSet<WishListItem>();
	    WishListItem itemToDelete = null;
	    for ( WishListItem shoppingCartItem : wishList.getLineItems() )
        {
            if ( shoppingCartItem.getProduct().getId().longValue() == productId.longValue() )
            {
                //get cart item
                itemToDelete =
                    getEntryToUpdate( shoppingCartItem.getId(), wishList );
                
                
                //break;

            } else {
              items.add(shoppingCartItem);
            }
        }
	    //delete item
	    wishListService.deleteShoppingCartItem(itemToDelete.getId());
        
        //remaining items
        wishList.setLineItems(items);
        ReadableWishList readableShoppingCart = null;
        
        if(items.size()>0) {
          wishListService.saveOrUpdate(wishList);//update cart with remaining items
          readableShoppingCart = this.getByCode(cartCode, merchant, language);
          
        }

	}
	
	private ReadableWishList readableShoppingCart(WishList wishList, PersistableWishListItem item, MerchantStore store,
			Language language) throws Exception {
		
		
		WishListItem itemModel = createCartItem(wishList, item, store);
		
		//need to check if the item is already in the cart
        boolean duplicateFound = false;
        //only if item has no attributes
        if(CollectionUtils.isEmpty(item.getAttributes())) {//increment quantity
        	//get duplicate item from the cart
        	Set<WishListItem> cartModelItems = wishList.getLineItems();
        	for(WishListItem cartItem : cartModelItems) {
        		if(cartItem.getProduct().getId().longValue()==item.getProduct().longValue()) {
        			if(CollectionUtils.isEmpty(cartItem.getAttributes())) {
        				if(!duplicateFound) {
        					if(!itemModel.isProductVirtual()) {
	        					cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
        					}
        					duplicateFound = true;
        					break;
        				}
        			}
        		}
        	}
        } 
        
        if(!duplicateFound) {
        	wishList.getLineItems().add( itemModel );
        }
        
        saveShoppingCart( wishList );

        //refresh cart
        wishList = wishListService.getById(wishList.getId(), store);

        ReadableWishListPopulator readableShoppingCart = new ReadableWishListPopulator();
        
        readableShoppingCart.setImageUtils(imageUtils);
        readableShoppingCart.setPricingService(pricingService);
        readableShoppingCart.setProductAttributeService(productAttributeService);

        ReadableWishList readableCart = new ReadableWishList();
        
        readableShoppingCart.populate(wishList, readableCart,  store, language);

		
		return readableCart;
		
	}


	
	private void saveShoppingCart(WishList shoppingCart) throws Exception {
		wishListService.save(shoppingCart);
	}
	
	private String uniqueShoppingCartCode() {
		return UUID.randomUUID().toString().replaceAll( "-", "" );
	}

	@Override
	public ReadableWishList getByCode(String code, MerchantStore store, Language language) throws Exception {
		
		WishList cart = wishListService.getByCode(code, store);
		
		ReadableWishList readableCart = null;
		
		if(cart != null) {
			
	        ReadableWishListPopulator readableShoppingCart = new ReadableWishListPopulator();
	        
	        readableShoppingCart.setImageUtils(imageUtils);
	        readableShoppingCart.setPricingService(pricingService);
	        readableShoppingCart.setProductAttributeService(productAttributeService);

	        readableCart = readableShoppingCart.populate(cart, null,  store, language);
			
			
		}
		
		return readableCart;
		
	}

	public ReadableWishList modifyCart(String code, PersistableWishListItem item, MerchantStore store,
			Language language) throws Exception {

		WishList cartModel = getWishListModel(code, store);


		WishListItem itemModel = createCartItem(cartModel, item, store);

		boolean itemModified = false;
		//check if existing product
		Set<WishListItem> items = cartModel.getLineItems();
		if(!CollectionUtils.isEmpty(items)) {
			Set<WishListItem> newItems = new HashSet<WishListItem>();
			Set<WishListItem> removeItems = new HashSet<WishListItem>();
			for(WishListItem anItem : items) {//take care of existing product
				if(itemModel.getProduct().getId().longValue() == anItem.getProduct().getId()) {
					if(item.getQuantity()==0) {//left aside item to be removed
						//don't add it to new list of item
						removeItems.add(anItem);
					} else {
						//new quantity
						anItem.setQuantity(item.getQuantity());
						newItems.add(anItem);
					}
					itemModified = true;
				} else {
					newItems.add(anItem);
				}
			}

			if(!removeItems.isEmpty()) {
				for(WishListItem emptyItem : removeItems) {
					wishListService.deleteShoppingCartItem(emptyItem.getId());
				}

			}

			if(!itemModified) {
				newItems.add(itemModel);
			}

			if(newItems.isEmpty()) {
				newItems = null;
			}

			cartModel.setLineItems(newItems);
		} else {
			//new item
			if(item.getQuantity() > 0) {
				cartModel.getLineItems().add( itemModel );
			}
		}

		//if cart items are null just return cart with no items

		saveShoppingCart( cartModel );

		//refresh cart
		cartModel = wishListService.getById(cartModel.getId(), store);

		if(cartModel==null) {
			return null;
		}

		ReadableWishListPopulator readableShoppingCart = new ReadableWishListPopulator();

		readableShoppingCart.setImageUtils(imageUtils);
		readableShoppingCart.setPricingService(pricingService);
		readableShoppingCart.setProductAttributeService(productAttributeService);

		ReadableWishList readableCart = new ReadableWishList();

		readableShoppingCart.populate(cartModel, readableCart,  store, language);


		return readableCart;

	}



}
