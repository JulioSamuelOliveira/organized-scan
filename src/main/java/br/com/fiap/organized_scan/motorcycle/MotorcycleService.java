package br.com.fiap.organized_scan.motorcycle;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.organized_scan.enums.MotorcycleType;

public interface MotorcycleService {
    List<Motorcycle> buscarComFiltros(Long portalId, LocalDate dataEntrada, MotorcycleType type, String licensePlate);
    Motorcycle getById(Long id);
    Motorcycle save(Motorcycle motorcycle);
    void deleteById(Long id);
}