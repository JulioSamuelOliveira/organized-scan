package br.com.fiap.organized_scan.motorcycle;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.organized_scan.portal.Portal;
import br.com.fiap.organized_scan.portal.PortalRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class MotorcycleService {
        private final MotorcycleRepository motorcycleRepository;
    private final PortalRepository portalRepository;

    @Transactional
    @CacheEvict(value = "motorcycles", allEntries = true)
    public Motorcycle save(Motorcycle motorcycle) {
        if (motorcycle.getPortal() != null && motorcycle.getPortal().getId() != null) {
            Portal portal = portalRepository.findById(motorcycle.getPortal().getId())
                .orElseThrow(() -> new RuntimeException("Portal não encontrado com ID: " + motorcycle.getPortal().getId()));
            motorcycle.setPortal(portal);
        }
        return motorcycleRepository.save(motorcycle);
    }

    public Page<Motorcycle> findAll(Pageable pageable) {
        return motorcycleRepository.findAll(pageable);
    }

    public Optional<Motorcycle> findById(Long id) {
        return motorcycleRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = "motorcycles", allEntries = true)
    public void deleteById(Long id) {
        motorcycleRepository.deleteById(id);
    }

    public Optional<Portal> findPortalById(Long id) {
        return portalRepository.findById(id);
    }
}
