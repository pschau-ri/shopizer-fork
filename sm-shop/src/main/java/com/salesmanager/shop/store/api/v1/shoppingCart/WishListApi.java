package com.salesmanager.shop.store.api.v1.shoppingCart;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.shoppingcart.ReadableShoppingCart;
import com.salesmanager.shop.model.wishlist.PersistableWishListItem;
import com.salesmanager.shop.model.wishlist.ReadableWishList;
import com.salesmanager.shop.store.controller.wishlist.facade.WishListFacade;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1")
public class WishListApi {

  @Inject private WishListFacade wishListFacade;

  @Inject private CustomerService customerService;

  private static final Logger LOGGER = LoggerFactory.getLogger(WishListApi.class);

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/wishlist", method = RequestMethod.POST)
  @ApiOperation(
      httpMethod = "POST",
      value = "Add product to wish list when no wish list exists, this will create a new wishlist id",
      notes =
          "No customer ID in scope. Add to cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":1}",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableWishList addToCart(
      @Valid @RequestBody PersistableWishListItem shoppingCartItem,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ReadableWishList cart =
          wishListFacade.addToCart(shoppingCartItem, merchantStore, language);

      return cart;

    } catch (Exception e) {
      LOGGER.error("Error while adding product to cart", e);
      try {
        response.sendError(503, "Error while adding product to cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(value = "/wishlist/{code}", method = RequestMethod.GET)
  @ApiOperation(
      httpMethod = "GET",
      value = "Get a wishlist by code",
      notes = "",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableWishList getByCode(
      @PathVariable String code,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ReadableWishList cart = wishListFacade.getByCode(code, merchantStore, language);

      if (cart == null) {
        response.sendError(404, "No ShoppingCart found for customer code : " + code);
        return null;
      }

      return cart;

    } catch (Exception e) {
      LOGGER.error("Error while getting cart", e);
      try {
        response.sendError(503, "Error while getting cart " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }
  
  @DeleteMapping(
      value = "/wishlist/{code}/product/{id}",
      produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
  @ResponseStatus(HttpStatus.OK)
  @ApiOperation(
      httpMethod = "DELETE",
      value = "Remove a product from a specific wishlist",
      notes = "",
      produces = "application/json",
      response = Void.class)
  @ApiImplicitParams({
    @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
    @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public void deleteWishItem(
      @PathVariable("code") String cartCode,
      @PathVariable("id") Long itemId,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language) throws Exception{

     wishListFacade.removeWishListItem(cartCode, itemId, merchantStore, language);

  }

  @ResponseStatus(HttpStatus.CREATED)
  @RequestMapping(value = "/wishlist/{code}", method = RequestMethod.PUT)
  @ApiOperation(
      httpMethod = "PUT",
      value = "Add to an existing wish list or modify an item quantity",
      notes =
          "No customer ID in scope. Modify cart for non authenticated users, as simple as {\"product\":1232,\"quantity\":0} for instance will remove item 1234 from cart",
      produces = "application/json",
      response = ReadableShoppingCart.class)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "store", dataType = "String", defaultValue = "DEFAULT"),
      @ApiImplicitParam(name = "lang", dataType = "String", defaultValue = "en")
  })
  public @ResponseBody ReadableWishList modifyCart(
      @PathVariable String code,
      @Valid @RequestBody PersistableWishListItem shoppingCartItem,
      @ApiIgnore MerchantStore merchantStore,
      @ApiIgnore Language language,
      HttpServletResponse response) {

    try {
      ReadableWishList cart =
          wishListFacade.modifyCart(code, shoppingCartItem, merchantStore, language);

      return cart;

    } catch (Exception e) {
      LOGGER.error("Error while modyfing wish list " + code + " ", e);
      try {
        response.sendError(503, "Error while modifying wish list " + code + " " + e.getMessage());
      } catch (Exception ignore) {
      }

      return null;
    }
  }
}
