import { Component, inject } from "@angular/core";
import { RouterModule } from "@angular/router";
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";
import { DialogModule } from 'primeng/dialog';
import { CartService } from "app/products/data-access/cart.service";
import { ProductCartComponent } from "app/products/features/product-cart/product-cart.component";
import { MatBadgeModule } from '@angular/material/badge';

@Component({
    selector: "app-root",
    templateUrl: "./app.component.html",
    styleUrls: ["./app.component.scss"],
    standalone: true,
    imports: [RouterModule, SplitterModule, ToolbarModule, PanelMenuComponent, DialogModule, ProductCartComponent, MatBadgeModule],
})
export class AppComponent {
    title = "ALTEN SHOP";
    public isDialogVisible = false;
    private readonly cartService = inject(CartService);

    public displayCart() {
        this.isDialogVisible = true;
    }

    public getCartAmountOfProducts(){
        return this.cartService.getCartAmountOfProducts();
    }
}
