package com.salesmanager.shop.populator.wishlist;

import com.salesmanager.core.business.exception.ConversionException;
import com.salesmanager.core.business.services.catalog.product.PricingService;
import com.salesmanager.core.business.services.catalog.product.attribute.ProductAttributeService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.catalog.product.attribute.ProductOption;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionDescription;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValue;
import com.salesmanager.core.model.catalog.product.attribute.ProductOptionValueDescription;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.core.model.wishlist.WishList;
import com.salesmanager.core.model.wishlist.WishListAttributeItem;
import com.salesmanager.core.model.wishlist.WishListItem;
import com.salesmanager.shop.model.order.total.ReadableOrderTotal;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOption;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartAttributeOptionValue;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCartItem;
import com.salesmanager.shop.model.wishlist.ReadableWishList;
import com.salesmanager.shop.model.wishlist.ReadableWishListAttribute;
import com.salesmanager.shop.model.wishlist.ReadableWishListtem;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import com.salesmanager.shop.utils.ImageFilePath;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadableWishListPopulator extends AbstractDataPopulator<WishList, ReadableWishList> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReadableWishListPopulator.class);
	
	private PricingService pricingService;
    private ProductAttributeService productAttributeService;
    
    private ImageFilePath imageUtils;
	
	@Override
	public ReadableWishList populate(WishList source, ReadableWishList target, MerchantStore store,
			Language language) throws ConversionException {
    	Validate.notNull(source, "Requires ShoppingCart");
    	Validate.notNull(language, "Requires Language not null");
    	Validate.notNull(store, "Requires MerchantStore not null");
    	Validate.notNull(pricingService, "Requires to set pricingService");
    	Validate.notNull(productAttributeService, "Requires to set productAttributeService");
    	Validate.notNull(imageUtils, "Requires to set imageUtils");
    	
    	if(target == null) {
    		target = new ReadableWishList();
    	}
    	target.setCode(source.getWishListCode());
    	int cartQuantity = 0;
    	
    	target.setCustomer(source.getCustomerId());
    	
    	try {
    	
    		Set<WishListItem> items = source.getLineItems();

            if(items!=null) {

                for(WishListItem item : items) {


                	ReadableWishListtem shoppingCartItem = new ReadableWishListtem();

                	ReadableProductPopulator readableProductPopulator = new ReadableProductPopulator();
                	readableProductPopulator.setPricingService(pricingService);
                	readableProductPopulator.setimageUtils(imageUtils);
                	readableProductPopulator.populate(item.getProduct(), shoppingCartItem,  store, language);



                    shoppingCartItem.setPrice(item.getItemPrice());
					shoppingCartItem.setFinalPrice(pricingService.getDisplayAmount(item.getItemPrice(),store));
			
                    shoppingCartItem.setQuantity(item.getQuantity());
                    
                    cartQuantity = cartQuantity + item.getQuantity();
                    
                    BigDecimal subTotal = pricingService.calculatePriceQuantity(item.getItemPrice(), item.getQuantity());
                    
                    //calculate sub total (price * quantity)
                    shoppingCartItem.setSubTotal(subTotal);

					shoppingCartItem.setDisplaySubTotal(pricingService.getDisplayAmount(subTotal,store));


                    Set<WishListAttributeItem> attributes = item.getAttributes();
                    if(attributes!=null) {
                        for(WishListAttributeItem attribute : attributes) {

                        	ProductAttribute productAttribute = productAttributeService.getById(attribute.getProductAttributeId());
                        	
                        	if(productAttribute==null) {
                        		LOGGER.warn("Product attribute with ID " + attribute.getId() + " not found, skipping cart attribute " + attribute.getId());
                        		continue;
                        	}
                        	
                        	ReadableWishListAttribute cartAttribute = new ReadableWishListAttribute();
                        	

                            cartAttribute.setId(attribute.getId());
                            
                            ProductOption option = productAttribute.getProductOption();
                            ProductOptionValue optionValue = productAttribute.getProductOptionValue();


                            List<ProductOptionDescription> optionDescriptions = option.getDescriptionsSettoList();
                            List<ProductOptionValueDescription> optionValueDescriptions = optionValue.getDescriptionsSettoList();
                            
                            String optName = null;
                            String optValue = null;
                            if(!CollectionUtils.isEmpty(optionDescriptions) && !CollectionUtils.isEmpty(optionValueDescriptions)) {
                            	
                            	optName = optionDescriptions.get(0).getName();
                            	optValue = optionValueDescriptions.get(0).getName();
                            	
                            	for(ProductOptionDescription optionDescription : optionDescriptions) {
                            		if(optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optName = optionDescription.getName();
                            			break;
                            		}
                            	}
                            	
                            	for(ProductOptionValueDescription optionValueDescription : optionValueDescriptions) {
                            		if(optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optValue = optionValueDescription.getName();
                            			break;
                            		}
                            	}

                            }
                            
                            if(optName != null) {
                            	ReadableShoppingCartAttributeOption attributeOption = new ReadableShoppingCartAttributeOption();
                            	attributeOption.setCode(option.getCode());
                            	attributeOption.setId(option.getId());
                            	attributeOption.setName(optName);
                            	cartAttribute.setOption(attributeOption);
                            }
                            
                            if(optValue != null) {
                            	ReadableShoppingCartAttributeOptionValue attributeOptionValue = new ReadableShoppingCartAttributeOptionValue();
                            	attributeOptionValue.setCode(optionValue.getCode());
                            	attributeOptionValue.setId(optionValue.getId());
                            	attributeOptionValue.setName(optValue);
                            	cartAttribute.setOptionValue(attributeOptionValue);
                            }
                            shoppingCartItem.getCartItemattributes().add(cartAttribute);  
                        }
                       
                    }
                    target.getProducts().add(shoppingCartItem);
                }
            }

            
            target.setQuantity(cartQuantity);
            target.setId(source.getId());
            
            
    	} catch(Exception e) {
    		throw new ConversionException(e);
    	}

        return target;
    	
 
	}

	@Override
	protected ReadableWishList createTarget() {
		return null;
	}

	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	public ImageFilePath getImageUtils() {
		return imageUtils;
	}

	public void setImageUtils(ImageFilePath imageUtils) {
		this.imageUtils = imageUtils;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

}
