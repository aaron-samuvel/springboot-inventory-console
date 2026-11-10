package dev.aaron.inventory.web;

import dev.aaron.inventory.product.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductPageController {
    private final ProductService service;

    public ProductPageController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "") String q,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        populate(model, q, page);
        model.addAttribute("form", new ProductForm());
        return "products";
    }

    @PostMapping("/products")
    public String create(@Valid @ModelAttribute("form") ProductForm form,
                         BindingResult errors,
                         @RequestParam(defaultValue = "") String q,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            populate(model, q, 0);
            return "products";
        }
        service.create(form.toRequest());
        redirectAttributes.addFlashAttribute("notice", "Product added to inventory.");
        return "redirect:/products";
    }

    private void populate(Model model, String query, int page) {
        Slice<?> products = service.findPage(query, page);
        model.addAttribute("products", products);
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("currentPage", products.getNumber());
        model.addAttribute("hasNext", products.hasNext() && products.getNumber() < 10_000);
        model.addAttribute("hasPrevious", products.hasPrevious());
    }
}
