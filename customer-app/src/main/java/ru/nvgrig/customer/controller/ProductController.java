package ru.nvgrig.customer.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.nvgrig.customer.client.ProductsClient;
import ru.nvgrig.customer.controller.payload.NewProductReviewPayload;
import ru.nvgrig.customer.entity.Product;
import ru.nvgrig.customer.service.FavoriteProductsService;
import ru.nvgrig.customer.service.ProductReviewsService;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
public class ProductController {

    private final ProductsClient productsClient;
    private final FavoriteProductsService favoriteProductsService;
    private final ProductReviewsService productReviewsService;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") int id) {
        return productsClient.findProduct(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.not_found")));
    }

    @GetMapping
    public Mono<String> getProductPage(@PathVariable("productId") int id, Model model) {
        model.addAttribute("inFavorite", false);
        return productReviewsService.findProductReviewsByProduct(id)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .then(favoriteProductsService.findByProduct(id)
                        .doOnNext(favoriteProduct -> model.addAttribute("inFavorite", true)))
                .thenReturn("customer/products/product");
    }

    @PostMapping("add-to-favorites")
    public Mono<String> addProductToFavorites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favoriteProductsService.addProductToFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("remove-from-favorites")
    public Mono<String> removeProductFromFavorites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> favoriteProductsService.removeProductFromFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(@PathVariable("productId") int id,
                                     @Valid NewProductReviewPayload payload,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("inFavorite", false);
            model.addAttribute("payload", payload);
            model.addAttribute("errors", bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .toList());
            return favoriteProductsService.findByProduct(id)
                    .doOnNext(favoriteProduct -> model.addAttribute("inFavorite", true))
                    .thenReturn("customer/products/product");
        }
        return productReviewsService.createProductReview(id, payload.rating(), payload.review())
                .thenReturn("redirect:/customer/products/%d".formatted(id));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }
}
