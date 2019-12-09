/**
 * 
 */
package com.salesmanager.core.model.wishlist;

import com.salesmanager.core.constants.SchemaConstant;
import com.salesmanager.core.model.common.audit.AuditListener;
import com.salesmanager.core.model.common.audit.AuditSection;
import com.salesmanager.core.model.common.audit.Auditable;
import com.salesmanager.core.model.generic.SalesManagerEntity;
import com.salesmanager.core.model.merchant.MerchantStore;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "WISH_LIST", schema=SchemaConstant.SALESMANAGER_SCHEMA, indexes= { @Index(name = "WISH_LIST_CODE_IDX", columnList = "WISH_LIST_CODE"), @Index(name = "WISH_LIST_CUSTOMER_IDX", columnList = "CUSTOMER_ID")})
public class WishList extends SalesManagerEntity<Long, WishList> implements Auditable{

	
	private static final long serialVersionUID = 1L;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@Id
	@Column(name = "WISH_LIST_ID", unique=true, nullable=false)
	@TableGenerator(name = "TABLE_GEN", table = "SM_SEQUENCER", pkColumnName = "SEQ_NAME", valueColumnName = "SEQ_COUNT", pkColumnValue = "WISH_LIST_SEQ_NEXT_VAL")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
	private Long id;

	public String getWishListCode() {
		return wishListCode;
	}

	public void setWishListCode(String wishListCode) {
		this.wishListCode = wishListCode;
	}

	/**
	 * Will be used to fetch shopping cart model from the controller
	 * this is a unique code that should be attributed from the client (UI)
	 * 
	 */
	@Column(name = "WISH_LIST_CODE", unique=true, nullable=false)
	private String wishListCode;
	
	//@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval=true, mappedBy = "shoppingCart")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "wishList")
	private Set<WishListItem> lineItems = new HashSet<WishListItem>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore merchantStore;
	
	@Column(name = "CUSTOMER_ID", nullable = true)
	private Long customerId;
	
	@Transient
	private boolean obsolete = false;//when all items are obsolete
    
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
	

	public boolean isObsolete() {
		return obsolete;
	}

	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}

	public Set<WishListItem> getLineItems() {
		return lineItems;
	}

	public void setLineItems(Set<WishListItem> lineItems) {
		this.lineItems = lineItems;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setMerchantStore(MerchantStore merchantStore) {
		this.merchantStore = merchantStore;
	}

	public MerchantStore getMerchantStore() {
		return merchantStore;
	}



}
