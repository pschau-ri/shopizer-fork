package com.salesmanager.core.model.wishlist;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.catalog.product.attribute.ProductAttribute;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "WISH_LIST_ATTR_ITEM", schema=SchemaConstant.SALESMANAGER_SCHEMA)
public class WishListAttributeItem extends SalesManagerEntity<Long, WishListAttributeItem> implements Auditable {


	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "WISH_LIST_ATTR_ITEM_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "WISH_LIST_ATTR_ITM_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;

	@Embedded
	private AuditSection auditSection = new AuditSection();



	@Column(name="PRODUCT_ATTR_ID", nullable=false)
	private Long productAttributeId;

	@Transient
	private ProductAttribute productAttribute;


	public WishListItem getWishListItem() {
		return wishListItem;
	}

	public void setWishListItem(WishListItem wishListItem) {
		this.wishListItem = wishListItem;
	}

	@ManyToOne(targetEntity = WishListItem.class)
	@JoinColumn(name = "WISH_LIST_ITEM_ID", nullable = false)
	private WishListItem wishListItem;

	public WishListAttributeItem(WishListItem wishListItem, ProductAttribute productAttribute) {
		this.wishListItem = wishListItem;
		this.productAttribute = productAttribute;
		this.productAttributeId = productAttribute.getId();
	}

	public WishListAttributeItem() {

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


	public void setProductAttributeId(Long productAttributeId) {
		this.productAttributeId = productAttributeId;
	}

	public Long getProductAttributeId() {
		return productAttributeId;
	}

	public void setProductAttribute(ProductAttribute productAttribute) {
		this.productAttribute = productAttribute;
	}

	public ProductAttribute getProductAttribute() {
		return productAttribute;
	}


}
