package ru.nvgrig.manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.nvgrig.manager.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "list")
    public String getProductList(Model model) {
        model.addAttribute("products", productService.findAllProducts());
        return "catalogue/products/list";
    }

}
