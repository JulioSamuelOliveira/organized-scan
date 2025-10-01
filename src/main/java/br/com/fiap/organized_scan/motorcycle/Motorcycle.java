package br.com.fiap.organized_scan.motorcycle;

import java.time.LocalDate;

import br.com.fiap.organized_scan.enums.MotorcycleType;
import br.com.fiap.organized_scan.portal.Portal;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Motorcycle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O tipo de moto é obrigatório")
    private MotorcycleType type;

    @NotBlank(message = "Placa é um campo obrigatório")
    @Pattern(regexp = "^[a-zA-Z0-9 ]*$", message = "A placa não pode conter caracteres especiais")
    private String licensePlate;

    @NotBlank(message = "O chassi é obrigatório")
    private String chassi;

    @NotBlank(message = "RFID é um campo obrigatório")
    private String rfid;

    @ManyToOne
    @JoinColumn(name = "portal") // <<< garante coluna 'portal' no banco
    @NotNull(message = "O portal deve ser informado")
    private Portal portal;

    @Size(min = 10, max = 500, message = "A descrição do problema deve ter entre 10 e 500 caracteres")
    private String problemDescription;

    @NotNull(message = "A data de entrada é obrigatória")
    @PastOrPresent(message = "A data de entrada deve estar no passado ou presente")
    private LocalDate entryDate;

    @NotNull(message = "A previsão de disponibilidade é obrigatória")
    @FutureOrPresent(message = "A previsão de disponibilidade deve estar no presente ou no futuro")
    private LocalDate availabilityForecast;
}
