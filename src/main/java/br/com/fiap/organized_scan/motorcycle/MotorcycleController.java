package br.com.fiap.organized_scan.motorcycle;

import br.com.fiap.organized_scan.enums.MotorcycleType;
import br.com.fiap.organized_scan.portal.Portal;
import br.com.fiap.organized_scan.portal.PortalRepository;
import br.com.fiap.organized_scan.config.MessageHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/motorcycle")
@RequiredArgsConstructor
public class MotorcycleController {

    private final MotorcycleService motorcycleService;   // serviço da sua entidade
    private final PortalRepository portalRepository;     // para listar/validar portais
    private final MessageHelper messageHelper;           // mensagens i18n (sucesso/erro)

    // LISTA com filtros (portal, data de entrada, tipo, placa) + envia enums e portais para filtros
    @GetMapping
    public String index(@RequestParam(required = false) Long portalId,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrada,
                        @RequestParam(required = false) MotorcycleType type,
                        @RequestParam(required = false) String licensePlate,
                        Model model,
                        @AuthenticationPrincipal OAuth2User user) {

        List<Motorcycle> motorcycles = motorcycleService.buscarComFiltros(portalId, dataEntrada, type, licensePlate);

        List<Portal> portais = portalRepository.findAll();
        List<MotorcycleType> tipos = Arrays.asList(MotorcycleType.values());

        model.addAttribute("motorcycles", motorcycles);
        model.addAttribute("portais", portais);
        model.addAttribute("tipos", tipos);
        model.addAttribute("user", user);
        return "motorcycle"; // view: src/main/resources/templates/motorcycle.html
    }

    // FORM de criação
    @GetMapping("/form")
    public String form(Model model, @AuthenticationPrincipal OAuth2User user) {
        List<Portal> portais = portalRepository.findAll();
        List<MotorcycleType> tipos = Arrays.asList(MotorcycleType.values());

        model.addAttribute("motorcycle", new Motorcycle());
        model.addAttribute("portais", portais);
        model.addAttribute("tipos", tipos);
        model.addAttribute("user", user);
        return "form-motorcycle"; // view: templates/form-motorcycle.html
    }

    // CRIAR
    @PostMapping("/form")
    public String create(@Valid Motorcycle motorcycle,
                         BindingResult result,
                         RedirectAttributes redirect,
                         @RequestParam(required = false) Long portalId) {
        if (result.hasErrors()) return "form-motorcycle";

        // garante que o Portal existe (funciona se vier portal.id no form ou um portalId solto)
        Long pid = portalId;
        if (pid == null && motorcycle.getPortal() != null) {
            pid = motorcycle.getPortal().getId();
        }
        if (pid == null) {
            result.rejectValue("portal", "portal.required", "O portal deve ser informado");
            return "form-motorcycle";
        }
        Portal portal = portalRepository.findById(pid)
                .orElseThrow(() -> new NoSuchElementException("Portal %d não encontrado".formatted(pid)));
        motorcycle.setPortal(portal);

        motorcycleService.save(motorcycle);
        redirect.addFlashAttribute("message", messageHelper.get("motorcycle.create.success"));
        return "redirect:/motorcycle";
    }

    // EDITAR (carrega formulário)
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User user) {
        Motorcycle motorcycle = motorcycleService.getById(id);
        List<Portal> portais = portalRepository.findAll();
        List<MotorcycleType> tipos = Arrays.asList(MotorcycleType.values());

        model.addAttribute("motorcycle", motorcycle);
        model.addAttribute("portais", portais);
        model.addAttribute("tipos", tipos);
        model.addAttribute("user", user);
        return "form-motorcycle";
    }

    // ATUALIZAR
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid Motorcycle motorcycle,
                         BindingResult result,
                         RedirectAttributes redirect,
                         @RequestParam(required = false) Long portalId) {
        if (result.hasErrors()) {
            // mantém o id no objeto para reexibir corretamente no form
            motorcycle.setId(id);
            return "form-motorcycle";
        }

        Motorcycle antigo = motorcycleService.getById(id);

        // consolida portal
        Long pid = portalId;
        if (pid == null && motorcycle.getPortal() != null) {
            pid = motorcycle.getPortal().getId();
        }
        if (pid == null && antigo.getPortal() != null) {
            pid = antigo.getPortal().getId();
        }
        if (pid == null) {
            result.rejectValue("portal", "portal.required", "O portal deve ser informado");
            motorcycle.setId(id);
            return "form-motorcycle";
        }
        Portal portal = portalRepository.findById(pid)
                .orElseThrow(() -> new NoSuchElementException("Portal %d não encontrado".formatted(pid)));

        // aplica alterações
        antigo.setType(motorcycle.getType());
        antigo.setLicensePlate(motorcycle.getLicensePlate());
        antigo.setChassi(motorcycle.getChassi());
        antigo.setRfid(motorcycle.getRfid());
        antigo.setPortal(portal);
        antigo.setProblemDescription(motorcycle.getProblemDescription());
        antigo.setEntryDate(motorcycle.getEntryDate());
        antigo.setAvailabilityForecast(motorcycle.getAvailabilityForecast());

        motorcycleService.save(antigo);

        redirect.addFlashAttribute("message", messageHelper.get("motorcycle.update.success"));
        return "redirect:/motorcycle";
    }

    // DELETAR (simples; se você tiver vínculos como "movimentos", trate aqui como no seu exemplo)
    @DeleteMapping("{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        motorcycleService.deleteById(id);
        redirect.addFlashAttribute("message", messageHelper.get("motorcycle.delete.success"));
        return "redirect:/motorcycle";
    }

    // LISTA por portal (visão semelhante ao /leitor-moto/{leitorId} do seu exemplo)
    @GetMapping("/portal/{portalId}")
    public String porPortal(@PathVariable Long portalId,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrada,
                            @RequestParam(required = false) MotorcycleType type,
                            @RequestParam(required = false) String licensePlate,
                            Model model,
                            @AuthenticationPrincipal OAuth2User user) {
        Portal portal = portalRepository.findById(portalId)
                .orElseThrow(() -> new NoSuchElementException("Portal %d não encontrado".formatted(portalId)));

        List<Motorcycle> motorcycles = motorcycleService.buscarComFiltros(portalId, dataEntrada, type, licensePlate);

        List<Portal> portais = portalRepository.findAll();
        List<MotorcycleType> tipos = Arrays.asList(MotorcycleType.values());

        model.addAttribute("portalSelecionado", portal);
        model.addAttribute("motorcycles", motorcycles);
        model.addAttribute("portais", portais);
        model.addAttribute("tipos", tipos);
        model.addAttribute("user", user);

        return "portal-motorcycle"; // view: templates/portal-motorcycle.html
    }
}
