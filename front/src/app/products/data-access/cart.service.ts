import { Injectable, signal } from '@angular/core';
import { Product } from "./product.model";

@Injectable({
    providedIn: 'root',
})
export class CartService {
    private _productsInCart = signal<{ cartItemId: number, product: Product }[]>([]); //Signaux des articles présents dans le panier
    private currentId = 0; //Sert d'identification dans le cas ou le même article serait ajouté plusieurs fois au panier
    public readonly productsInCart = this._productsInCart.asReadonly();

    /**
     * Ajoute un article au panier, lui attribue un identifiant unique
     */
    addToCart(product: Product) {
        const item = { cartItemId: this.currentId++, product };
        this._productsInCart.update((items) => [...items, item]);
    }

    /**
     * Suppression d'un article du panier par son identifiant
     */
    deleteFromCart(cartItemId: number) {
        this._productsInCart.update((items) => items.filter(i => i.cartItemId !== cartItemId));
    }

    /**
     * Cout total de l'ensemble des articles présents dans le panier
     */
    getTotalCartCost() : number {
        return this._productsInCart().reduce((total, item) => total + item.product.price, 0);
    }

    /**
     * Nombre d'article dans le panier
     */
    getCartAmountOfProducts() : number {
        return this._productsInCart().length;
    }
}
