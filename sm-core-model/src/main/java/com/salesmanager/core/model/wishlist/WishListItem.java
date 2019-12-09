package com.salesmanager.core.model.wishlist;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.catalog.product.price.FinalPrice;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "WISH_LIST_ITEM", schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class WishListItem extends SalesManagerEntity<Long, WishListItem> implements Auditable, Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "WISH_LIST_ITEM_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "WISH_LIST_ITM_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;
	
	@ManyToOne(targetEntity = WishList.class)
	@JoinColumn(name = "WISH_LIST_ID", nullable = false)
	private WishList wishList;

	@Column(name="QUANTITY")
	private Integer quantity = new Integer(1);

	public WishList getWishList() {
		return wishList;
	}

	public void setWishList(WishList wishList) {
		this.wishList = wishList;
	}

	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@Column(name="PRODUCT_ID", nullable=false) //TODO CODE
	private Long productId;
	
	@Transient
	private boolean productVirtual;

	//@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true, mappedBy = "shoppingCartItem")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "wishListItem")
	private Set<WishListAttributeItem> attributes = new HashSet<WishListAttributeItem>();
	
	@Transient
	private BigDecimal itemPrice;//item final price including all rebates
	
	@Transient
	private BigDecimal subTotal;//item final price * quantity
	
	@Transient
	private FinalPrice finalPrice;//contains price details (raw prices)
	

	@Transient
	private Product product;
	
	@Transient
	private boolean obsolete = false;




	public WishListItem(WishList wishList, Product product) {
		this.product = product;
		this.productId = product.getId();
		this.quantity = 1;
		this.wishList = wishList;
		
	}
	
	public WishListItem(Product product) {
		this.product = product;
		this.productId = product.getId();
		this.quantity = 1;

	}
	
	public WishListItem() {
		
	}

	@Override
	public AuditSection getAuditSection() {
		return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection audit) {
		this.auditSection = audit;
		
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}



	public void setAttributes(Set<WishListAttributeItem> attributes) {
		this.attributes = attributes;
	}

	public Set<WishListAttributeItem> getAttributes() {
		return attributes;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Product getProduct() {
		return product;
	}
	
	public void addAttributes(WishListAttributeItem shoppingCartAttributeItem)
	{
	    this.attributes.add(shoppingCartAttributeItem);
	}
	
	public void removeAttributes(ShoppingCartAttributeItem shoppingCartAttributeItem)
	{
	    this.attributes.remove(shoppingCartAttributeItem);
	}

	public void removeAllAttributes(){
		this.attributes.removeAll(Collections.EMPTY_SET);
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setFinalPrice(FinalPrice finalPrice) {
		this.finalPrice = finalPrice;
	}

	public FinalPrice getFinalPrice() {
		return finalPrice;
	}
	
	public boolean isObsolete() {
		return obsolete;
	}

	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}
	

	public boolean isProductVirtual() {
		return productVirtual;
	}

	public void setProductVirtual(boolean productVirtual) {
		this.productVirtual = productVirtual;
	}

}
