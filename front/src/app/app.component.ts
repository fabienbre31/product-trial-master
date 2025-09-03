import { Component } from "@angular/core";
import { RouterModule } from "@angular/router";
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";
import { DialogModule } from 'primeng/dialog';
import { ProductCartComponent } from "app/products/features/product-cart/product-cart.component";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [RouterModule, SplitterModule, ToolbarModule, PanelMenuComponent, DialogModule, ProductCartComponent],
})
export class AppComponent {
  title = "ALTEN SHOP";
  public isDialogVisible = false;

  public displayCart() {
    this.isDialogVisible = true;
  }
}
