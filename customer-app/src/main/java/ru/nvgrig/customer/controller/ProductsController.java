package ru.nvgrig.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.client.ProductsClient;
import ru.nvgrig.customer.entity.FavoriteProduct;
import ru.nvgrig.customer.service.FavoriteProductsService;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products")
public class ProductsController {

    private final ProductsClient productsClient;
    private final FavoriteProductsService favoriteProductsService;

    @GetMapping("list")
    public Mono<String> getProductsListPage(Model model,
                                            @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return productsClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favorites")
    public Mono<String> getFavoritesList(Model model,
                                         @RequestParam(name = "filter", required = false) String filter) {
        model.addAttribute("filter", filter);
        return favoriteProductsService.findFavoriteProducts()
                .map(FavoriteProduct::getProductId)
                .collectList()
                .flatMap(favoriteProductIds -> productsClient.findAllProducts(filter)
                        .filter(product -> favoriteProductIds.contains(product.id()))
                        .collectList()
                        .doOnNext(products -> model.addAttribute("products", products)))
                .thenReturn("customer/products/favorites");
    }
}
