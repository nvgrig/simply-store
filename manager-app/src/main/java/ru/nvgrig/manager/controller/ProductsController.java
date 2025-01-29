package ru.nvgrig.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nvgrig.manager.controller.payload.NewProductPayload;
import ru.nvgrig.manager.entity.Product;
import ru.nvgrig.manager.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {

    private final ProductService productService;

    @GetMapping(value = "list")
    public String getProductList(Model model) {
        model.addAttribute("products", productService.findAllProducts());
        return "catalogue/products/list";
    }

    @GetMapping("create")
    public String getNewProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String createProduct(NewProductPayload payload) {
        Product product = productService.createProduct(payload.title(), payload.details());
        return "redirect:/catalogue/products/%d".formatted(product.getId());
    }

}
