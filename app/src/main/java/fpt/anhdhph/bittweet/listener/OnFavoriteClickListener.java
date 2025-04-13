package fpt.anhdhph.bittweet.listener;

import fpt.anhdhph.bittweet.model.Product;

public interface OnFavoriteClickListener {
    void onFavoriteClick(Product product, boolean isFavorite);
}