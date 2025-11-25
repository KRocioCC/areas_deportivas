package com.espaciosdeportivos.service;

import com.espaciosdeportivos.dto.ReservaDTO;
import com.espaciosdeportivos.model.Reserva;
import com.espaciosdeportivos.dto.CancelacionDTO;
import com.espaciosdeportivos.dto.QrDTO;
import com.espaciosdeportivos.dto.ReprogramacionDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IReservaService {

    // CRUD básico
    List<ReservaDTO> listarTodas();
    //listar reservas por idcliente
    //List<ReservaDTO> listarPorCliente(Long idCliente);

    ReservaDTO obtenerPorId(Long id);
    ReservaDTO crear(ReservaDTO reservaDTO);
    ReservaDTO actualizar(Long id, ReservaDTO reservaDTO);
    void eliminar(Long id);

    List<String> obtenerHorasDisponibles(Long idCancha, LocalDate fecha);
    ReservaDTO actualizarEstadoPagoReserva(Long idReserva);

    //ReservaDTO crearReserva(ReservaDTO reservaDTO);
    //ReservaDTO reprogramarReserva(Long idReserva, ReprogramacionDTO nuevaReserva);
    //void cancelarReserva2(Long idReserva, CancelacionDTO cancelacionDTO);
    
    // Búsquedas
    List<ReservaDTO> buscarPorCliente(Long idCliente);
    List<ReservaDTO> buscarPorEstado(String estado);
    List<ReservaDTO> buscarPorRangoFechas(LocalDate inicio, LocalDate fin);
    List<ReservaDTO> buscarReservasActivasDelCliente(Long clienteId);
    List<ReservaDTO> obtenerReservasDelDia(LocalDate fecha);
    // Reservas por administrador en rango de fechas
    List<ReservaDTO> buscarPorAdministradorEnRango(Long idAdministrador, LocalDate inicio, LocalDate fin);
    //RESERVAS POR CLIETES
    List<ReservaDTO> buscarTodasLasReservasDelCliente(Long clienteId);
    //ReservaDTO obtenerPorCodigoReserva(String codigoReserva);
    
    // Métodos de negocio
    //ReservaDTO confirmarReserva(Long idReserva);
    ReservaDTO cancelarReserva(Long idReserva, String motivo);
    ReservaDTO marcarComoEnCurso(Long idReserva);
    ReservaDTO marcarComoCompletada(Long idReserva);
    ReservaDTO marcarComoNoShow(Long idReserva);
    
    // Validaciones y reportes
    boolean validarDisponibilidad(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
    void validarFechaReserva(LocalDate fechaReserva);

    //List<ReservaDTO> obtenerReservasProximas();
    //Double calcularIngresosEnRango(LocalDate inicio, LocalDate fin);
    // Utilidades
    //String generarCodigoReserva();
    //List<Reserva> findReservaByCancha(Long idCancha);

    //ReservaDTO crearReserva(ReservaDTO dto);

       // Listar reservas por cancha k
    List<ReservaDTO> listarReservasPorCancha(Long idCancha);

    // --- NUEVO MÉTODO PARA EL REPORTE DE PAGOS ---
    List<ReservaDTO> buscarTodasPorAdministrador(Long idAdministrador);
}