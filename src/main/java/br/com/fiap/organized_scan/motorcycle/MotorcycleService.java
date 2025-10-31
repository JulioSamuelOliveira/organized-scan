package br.com.fiap.organized_scan.motorcycle;

import br.com.fiap.organized_scan.enums.MotorcycleType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MotorcycleService {

    private final MotorcycleRepository repository;

    /** Lista com filtros dinâmicos e ordenação por id DESC */
    public List<Motorcycle> buscarComFiltros(Long portalId,
                                             LocalDate dataEntrada,
                                             MotorcycleType type,
                                             String licensePlate) {

        Specification<Motorcycle> spec = (root, query, cb) -> {
            List<Predicate> preds = new ArrayList<>();

            if (portalId != null) {
                preds.add(cb.equal(root.get("portal").get("id"), portalId));
            }
            if (dataEntrada != null) {
                preds.add(cb.equal(root.get("entryDate"), dataEntrada));
            }
            if (type != null) {
                preds.add(cb.equal(root.get("type"), type));
            }
            if (licensePlate != null && !licensePlate.isBlank()) {
                preds.add(cb.like(cb.lower(root.get("licensePlate")), "%" + licensePlate.toLowerCase() + "%"));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    /** Busca por ID */
    public Motorcycle getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Motorcycle %d não encontrada".formatted(id)));
    }

    /** Cria/atualiza */
    public Motorcycle save(Motorcycle motorcycle) {
        return repository.save(motorcycle);
    }

    /** Exclui por ID */
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
