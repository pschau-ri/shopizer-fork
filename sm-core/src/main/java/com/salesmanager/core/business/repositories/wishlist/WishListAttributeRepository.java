package com.salesmanager.core.business.repositories.wishlist;

import com.salesmanager.core.model.wishlist.WishListAttributeItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListAttributeRepository extends JpaRepository<WishListAttributeItem, Long> {


}
