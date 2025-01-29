package ru.nvgrig.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.nvgrig.manager.controller.payload.UpdateProductPayload;
import ru.nvgrig.manager.entity.Product;
import ru.nvgrig.manager.service.ProductService;

@Controller
@RequestMapping("catalogue/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ModelAttribute("product")
    public Product product(@PathVariable("productId") int productId) {
        return productService.findProduct(productId).orElseThrow();
    }

    @GetMapping
    public String getProduct() {
        return "catalogue/products/product";
    }

    @GetMapping("edit")
    public String getProductEditPage() {
        return "catalogue/products/edit";
    }

    @PostMapping("edit")
    public String updateProduct(@ModelAttribute("product") Product product, UpdateProductPayload payload) {
        productService.updateProduct(product.getId(), payload.title(), payload.details());
        return "redirect:/catalogue/products/%d".formatted(product.getId());
    }

    @PostMapping("delete")
    public String deleteProduct(@ModelAttribute("product") Product product) {
        productService.deleteProduct(product.getId());
        return "redirect:/catalogue/products/list";
    }

}
