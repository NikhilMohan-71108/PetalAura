package com.petalaura.library.Service;


import com.petalaura.library.model.WishList;

import java.util.List;

public interface WishListService {
void addToWishList(String  username,Long productId);
void removeFromWishList(Long wishListId);
List<WishList> findWishListByCustomer(String username);

}
