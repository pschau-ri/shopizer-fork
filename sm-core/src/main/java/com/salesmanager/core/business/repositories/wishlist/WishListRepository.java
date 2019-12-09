package com.salesmanager.core.business.repositories.wishlist;

import com.salesmanager.core.model.wishlist.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WishListRepository extends JpaRepository<WishList, Long> {

	@Query("select c from WishList c left join fetch c.lineItems cl left join fetch cl.attributes cla join fetch c.merchantStore cm where c.id = ?1")
	WishList findOne(Long id);
	
	@Query("select c from WishList c left join fetch c.lineItems cl left join fetch cl.attributes cla join fetch c.merchantStore cm where c.wishListCode = ?1")
	WishList findByCode(String code);
	
	@Query("select c from WishList c left join fetch c.lineItems cl left join fetch cl.attributes cla join fetch c.merchantStore cm where cm.id = ?1 and c.id = ?2")
	WishList findById(Integer merchantId, Long id);
	
	@Query("select c from WishList c left join fetch c.lineItems cl left join fetch cl.attributes cla join fetch c.merchantStore cm where cm.id = ?1 and c.wishListCode = ?2")
	WishList findByCode(Integer merchantId, String code);
	
	@Query("select c from WishList c left join fetch c.lineItems cl left join fetch cl.attributes cla join fetch c.merchantStore cm where c.customerId = ?1")
	WishList findByCustomer(Long customerId);
	
}
