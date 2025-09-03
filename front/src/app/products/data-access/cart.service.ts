import { Injectable, signal } from '@angular/core';
import { Product } from "./product.model";

@Injectable({
    providedIn: 'root',
})
export class CartService {
    private _productsInCart = signal<{ cartItemId: number, product: Product }[]>([]);
    private currentId = 0;
    public readonly productsInCart = this._productsInCart.asReadonly();

    addToCart(product: Product) {
        const item = { cartItemId: this.currentId++, product };
        this._productsInCart.update((items) => [...items, item]);
    }

    deleteFromCart(cartItemId: number) {
        this._productsInCart.update((items) => items.filter(i => i.cartItemId !== cartItemId));
    }

    getTotalCartCost() : number {
        return this._productsInCart().reduce((total, item) => total + item.product.price, 0);
    }
}
