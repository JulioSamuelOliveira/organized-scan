package br.com.fiap.organized_scan.portal;

import br.com.fiap.organized_scan.config.MessageHelper;
import br.com.fiap.organized_scan.enums.PortalType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/portal")
@RequiredArgsConstructor
public class PortalController {

    private final PortalRepository portalRepository;
    private final MessageHelper messageHelper;

    // LISTA com filtros opcionais (type e busca por nome)
    @GetMapping
    public String index(@RequestParam(name = "type", required = false) PortalType type,
                        @RequestParam(name = "q", required = false) String q,
                        Model model,
                        @AuthenticationPrincipal OAuth2User user) {

        List<Portal> portais = portalRepository.findAll();

        if (type != null) {
            portais = portais.stream()
                    .filter(p -> p.getType() == type)
                    .toList();
        }
        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            portais = portais.stream()
                    .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(term))
                    .toList();
        }

        model.addAttribute("portais", portais);
        model.addAttribute("tipos", Arrays.asList(PortalType.values()));
        model.addAttribute("user", user);
        return "portal"; // templates/portal.html
    }

    // FORM de criação
    @GetMapping("/form")
    public String form(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAttribute("portal", new Portal());
        model.addAttribute("tipos", Arrays.asList(PortalType.values()));
        model.addAttribute("user", user);
        return "form-portal"; // templates/form-portal.html
    }

    // CRIAR
    @PostMapping("/form")
    public String create(@Valid @ModelAttribute("portal") Portal portal,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) return "form-portal";

        portalRepository.save(portal);
        redirect.addFlashAttribute("message", messageHelper.get("portal.create.success", "Portal criado com sucesso"));
        return "redirect:/portal";
    }

    // EDITAR (carregar form)
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model,
                       @AuthenticationPrincipal OAuth2User user) {
        Portal portal = portalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Portal %d não encontrado".formatted(id)));

        model.addAttribute("portal", portal);
        model.addAttribute("tipos", Arrays.asList(PortalType.values()));
        model.addAttribute("user", user);
        return "form-portal";
    }

    // ATUALIZAR
    @PostMapping("/{id}")
    public String update(@PathVariable(name = "id") Long id,
                         @Valid @ModelAttribute("portal") Portal portal,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            portal.setId(id);
            return "form-portal";
        }

        Portal antigo = portalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Portal %d não encontrado".formatted(id)));

        antigo.setName(portal.getName());
        antigo.setType(portal.getType());

        portalRepository.save(antigo);
        redirect.addFlashAttribute("message", messageHelper.get("portal.update.success", "Portal atualizado com sucesso"));
        return "redirect:/portal";
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public String delete(@PathVariable(name = "id") Long id, RedirectAttributes redirect) {
        portalRepository.deleteById(id);
        redirect.addFlashAttribute("message", messageHelper.get("portal.delete.success", "Portal removido com sucesso"));
        return "redirect:/portal";
    }
}
