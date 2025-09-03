import { Component, inject, signal } from "@angular/core";
import { Product } from "app/products/data-access/product.model";
import { CardModule } from "primeng/card";
import { CartService } from "app/products/data-access/cart.service";
import { DataViewModule } from "primeng/dataview";

@Component({
    selector: "app-product-cart",
    templateUrl: "./product-cart.component.html",
    styleUrls: ["./product-cart.component.scss"],
    standalone: true,
    imports: [DataViewModule, CardModule],
})
export class ProductCartComponent {

    private readonly cartService = inject(CartService);

    public productsInCart = this.cartService.productsInCart;

    /**
    * Ajoute un produit au panier
    */
    public addToCart(product: Product) {
        this.cartService.addToCart(product);
    }

    /**
    * Supprime un produit du panier
    */
    public deleteFromCart(cartItemId: number) {
        this.cartService.deleteFromCart(cartItemId);
    }

    public getTotalCartCost() {
        return this.cartService.getTotalCartCost();
    }
}
