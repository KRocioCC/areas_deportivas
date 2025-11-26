package com.espaciosdeportivos.controller;

import com.espaciosdeportivos.dto.ReservaDTO;
import com.espaciosdeportivos.model.Reserva;
import com.espaciosdeportivos.service.IReservaService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    // CRUD
    public ResponseEntity<List<ReservaDTO>> listarTodas() {
        List<ReservaDTO> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerPorId(@PathVariable Long id) {
        ReservaDTO reserva = reservaService.obtenerPorId(id);
        return ResponseEntity.ok(reserva);
    }


    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaDTO dto) {
        ReservaDTO reservaCreada = reservaService.crear(dto);
        return ResponseEntity.ok(reservaCreada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReservaDTO dto) {
        ReservaDTO reservaActualizada = reservaService.actualizar(id, dto);
        return ResponseEntity.ok(reservaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // BÚSQUEDAS
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<ReservaDTO>> buscarPorCliente(@PathVariable Long idCliente) {
        List<ReservaDTO> reservas = reservaService.buscarPorCliente(idCliente);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaDTO>> buscarPorEstado(@PathVariable String estado) {
        List<ReservaDTO> reservas = reservaService.buscarPorEstado(estado);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<ReservaDTO>> buscarPorRangoFechas(
            @RequestParam("inicio") String inicio,
            @RequestParam("fin") String fin) {
        List<ReservaDTO> reservas = reservaService.buscarPorRangoFechas(
            LocalDate.parse(inicio), LocalDate.parse(fin));
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/activas/cliente/{idCliente}")
    public ResponseEntity<List<ReservaDTO>> buscarReservasActivasDelCliente(@PathVariable Long idCliente) {
        List<ReservaDTO> reservas = reservaService.buscarReservasActivasDelCliente(idCliente);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/dia/{fecha}")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasDelDia(@PathVariable String fecha) {
        List<ReservaDTO> reservas = reservaService.obtenerReservasDelDia(LocalDate.parse(fecha));
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/horario-disponible")
    public ResponseEntity<List<String>> getHorasDisponibles(
            @RequestParam Long canchaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<String> horasDisponibles = reservaService.obtenerHorasDisponibles(canchaId, fecha);

        // Ya son strings del tipo "08:00 - 08:30"
        return ResponseEntity.ok(horasDisponibles);
    }


    @PostMapping("/{id}/en-curso")
    public ResponseEntity<ReservaDTO> marcarEnCurso(@PathVariable Long id) {
        ReservaDTO reservaEnCurso = reservaService.marcarComoEnCurso(id);
        return ResponseEntity.ok(reservaEnCurso);
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<ReservaDTO> completarReserva(@PathVariable Long id) {
        ReservaDTO reservaCompletada = reservaService.marcarComoCompletada(id);
        return ResponseEntity.ok(reservaCompletada);
    }

    @PostMapping("/{id}/no-show")
    public ResponseEntity<ReservaDTO> marcarNoShow(@PathVariable Long id) {
        ReservaDTO reservaNoShow = reservaService.marcarComoNoShow(id);
        return ResponseEntity.ok(reservaNoShow);
    }

    // VALIDACIONES Y REPORTES
    @GetMapping("/disponibilidad")
    public ResponseEntity<Map<String, Boolean>> validarDisponibilidad(
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {
        boolean disponible = reservaService.validarDisponibilidad(
            LocalDate.parse(fecha), 
            LocalTime.parse(horaInicio), 
            LocalTime.parse(horaFin)
        );
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    @GetMapping("/cliente/{idCliente}/activas")
    public ResponseEntity<List<ReservaDTO>> buscarReservasActivas(@PathVariable Long idCliente) {
        List<ReservaDTO> reservas = reservaService.buscarReservasActivasDelCliente(idCliente);
        return ResponseEntity.ok(reservas);
    }



    /*@GetMapping("/proximas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasProximas() {
        List<ReservaDTO> reservas = reservaService.obtenerReservasProximas();
        return ResponseEntity.ok(reservas);
    }*/

    /*@GetMapping("/reporte/ingresos")
    public ResponseEntity<Map<String, Object>> calcularIngresos(
            @RequestParam String inicio,
            @RequestParam String fin) {
        Double ingresos = reservaService.calcularIngresosEnRango(
            LocalDate.parse(inicio), LocalDate.parse(fin));
        return ResponseEntity.ok(Map.of(
            "inicio", inicio,
            "fin", fin,
            "ingresos", ingresos,
            "moneda", "BOB"
        ));
    }*/

    // HEALTH CHECK
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "Reservas"));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        String motivo = request.get("motivo");
        ReservaDTO reservaCancelada = reservaService.cancelarReserva(id, motivo);
        return ResponseEntity.ok(reservaCancelada);
    }

    /*este es el mas importnate que sirve para la generacion de qr y reserva */
    @PutMapping("/{id}/actualizar-pago")
    public ResponseEntity<?> actualizarEstadoPago(@PathVariable Long id) {
        try {
            ReservaDTO reservaDTO = reservaService.actualizarEstadoPagoReserva(id);
            return ResponseEntity.ok(reservaDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el estado de pago.");
        }
    }
    //RESERVAS DEL CLIENTE
    @GetMapping("/cliente/{clienteId}/reservas")
    public ResponseEntity<List<ReservaDTO>> buscarTodasPorCliente(@PathVariable Long clienteId) {
        List<ReservaDTO> reservas = reservaService.buscarTodasLasReservasDelCliente(clienteId);
        return ResponseEntity.ok(reservas);
    }


    /*@GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReservaDTO> obtenerPorCodigo(@PathVariable String codigo) {
        ReservaDTO reserva = reservaService.obtenerPorCodigoReserva(codigo);
        return ResponseEntity.ok(reserva);
    }*/

    // OPERACIONES DE NEGOCIO
    /*@PostMapping("/{id}/confirmar")
    public ResponseEntity<ReservaDTO> confirmarReserva(@PathVariable Long id) {
        ReservaDTO reservaConfirmada = reservaService.confirmarReserva(id);
        return ResponseEntity.ok(reservaConfirmada);
    }*/

    // RESERVAS POR ADMINISTRADOR EN RANGO DE FECHAS
    //PAra administrador ver sus reservas en rango de fechas k
    @GetMapping("/administrador/{idAdministrador}/rango-fechas")
    public ResponseEntity<List<ReservaDTO>> buscarPorAdministradorEnRango(
            @PathVariable Long idAdministrador,
            @RequestParam String inicio,
            @RequestParam String fin) {
        List<ReservaDTO> reservas = reservaService.buscarPorAdministradorEnRango(
                idAdministrador, LocalDate.parse(inicio), LocalDate.parse(fin));
        return ResponseEntity.ok(reservas);
    }

    // Listar reservas por cancha k
    /*@GetMapping("/{idCancha}/reservas")
    public ResponseEntity<List<Reserva>> obtenerReservasPorCancha(@PathVariable Long idCancha) {
        List<Reserva> reservas = reservaService.listarReservasPorCancha(idCancha);
        return ResponseEntity.ok(reservas);
    }/* */

    // Listar reservas por cancha k
    @GetMapping("/{idCancha}/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasPorCancha(@PathVariable Long idCancha) {
        List<ReservaDTO> reservas = reservaService.listarReservasPorCancha(idCancha);
        return ResponseEntity.ok(reservas);
    }

    // Eliminado método duplicado `getPorCliente` para evitar mapeos idénticos.

    // Buscar por cliente y estado
    @GetMapping("/cliente/{idCliente}/estado/{estado}")
    public ResponseEntity<List<ReservaDTO>> buscarPorClienteYEstado(
            @PathVariable Long idCliente,
            @PathVariable String estado
    ) {
        return ResponseEntity.ok(reservaService.buscarPorClienteYEstado(idCliente, estado));
    }




        // 4. Ordenar reservas por fecha de creación ASC
    @GetMapping("/cliente/{idCliente}/orden/asc")
    public ResponseEntity<List<ReservaDTO>> ordenarAsc(@PathVariable Long idCliente) {
        return ResponseEntity.ok(reservaService.ordenarPorFechaCreacionAsc(idCliente));
    }


    // 5. Ordenar reservas por fecha de creación DESC
    @GetMapping("/cliente/{idCliente}/orden/desc")
    public ResponseEntity<List<ReservaDTO>> ordenarDesc(@PathVariable Long idCliente) {
        return ResponseEntity.ok(reservaService.ordenarPorFechaCreacionDesc(idCliente));
    }


        // Listar invitados por reserva (solo IDs)
    @GetMapping("/{idReserva}/invitados")
    public ResponseEntity<List<Long>> listarInvitados(
            @PathVariable Long idReserva
    ) {
        return ResponseEntity.ok(reservaService.listarInvitados(idReserva));
    }
// --- NUEVO ENDPOINT PARA REPORTE DE PAGOS (ESTO ES LO QUE FALTABA) ---
@GetMapping("/admin/{idAdmin}")
public ResponseEntity<List<ReservaDTO>> getReservasByAdmin(@PathVariable Long idAdmin) {
    // Llama al método que busca TODAS las reservas del admin (sin filtro de fecha)
    // Esto permitirá ver el historial completo de pagos
    List<ReservaDTO> reservas = reservaService.buscarTodasPorAdministrador(idAdmin);
    return ResponseEntity.ok(reservas);
}
       
}