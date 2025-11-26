package com.espaciosdeportivos.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import com.espaciosdeportivos.dto.CanchaDTO;
import com.espaciosdeportivos.dto.DisciplinaDTO;
import com.espaciosdeportivos.dto.EquipamientoDTO;
import com.espaciosdeportivos.dto.ReservaDTO;
import com.espaciosdeportivos.model.Cancha;


public interface ICanchaService {
    //listar todas las canchas activas
    List<CanchaDTO> obtenerTodasLasCanchas();
    //listar todos
    List<CanchaDTO> ListarTodos();

    //Buscar por Id
    CanchaDTO obtenerCanchaPorId(Long idCancha);
    //Buscar por Nombre
    List<CanchaDTO> buscarPorNombre(String nombre);
    //crear nueva cancha
    CanchaDTO crearCancha(@Valid CanchaDTO canchaDTO);
    //Actualizar cancha existente
    CanchaDTO actualizarCancha(Long idCancha, @Valid CanchaDTO canchaDTO);
    //Eliminar físicamente (solo si realmente quieres borrar)
    void eliminarCanchaFisicamente(Long idCancha);
    //Desactivar (eliminación lógica)
    CanchaDTO eliminarCancha(Long idCancha, Boolean nuevoEstado); 
    
    //cancha con bloqueo optimista
    Cancha obtenerCanchaConBloqueo(Long idCancha); // para uso interno con bloqueo
    // uso interno

    //Cancha en un rango de fecha
    List<CanchaDTO> BuscarConFiltros(LocalTime horaInicio, LocalTime horaFin, Double costo, Integer capacidad,
                                            String tamano, String iluminacion, String cubierta);
                                            
    //listar canchas por area deportiva
    //List<CanchaDTO> listarPorAreaDeportiva(Long idAreaDeportiva);

    //listar canchas por zona 
    //List<CanchaDTO> listarPorZona(Long idZona);

    //Listar canchas por macrodistrito
    //List<CanchaDTO> listarPorMacrodistrito(Long idMacrodistrito);

    //listar diciplinas que tiene una cancha 
    List<DisciplinaDTO> obtenerDiciplinasPorCancha(Long idCancha);

    //Listar Resevas de una cancha
    //List<ReservaDTO> obtenerReservasPorCancha(Long idCancha);

    //Sacar el promedio de calificaciones de una cancha
    //Double obtenerPromedioCalificacionesPorCancha(Long idCancha);

    //listar equipamiento por cancha
    List<EquipamientoDTO> obtenerEquipamientoPorCancha(Long idCancha);

    //List<ReservaDTO> obtenerReservaPorCancha(Long idCancha);

    //ADMIN K - Obtener canchas por área deportiva
    List<CanchaDTO> obtenerCanchasPorArea(Long idArea);

    List<CanchaDTO> obtenerCanchasPorAreaActivas(Long idArea);
    // Gestión de imágenes específica
    CanchaDTO agregarImagenes(Long idCancha, List<MultipartFile> archivosImagenes);
    CanchaDTO eliminarImagen(Long idCancha, Long idImagenRelacion);
    CanchaDTO reordenarImagenes(Long idCancha, List<Long> idsImagenesOrden);
    //nuevas enpoints

    // 1. Mostrar canchas con el promedio de mejores calificaciones
    List<CanchaDTO> obtenerCanchasMejorCalificadas();

    // 2. Mostrar canchas con mayor número de reservas
    List<CanchaDTO> obtenerCanchasMasReservadas();

    // 3. Listar canchas por disciplinas
    List<CanchaDTO> obtenerCanchasPorDisciplina(Long idDisciplina);

    // 4. Listar canchas por zona
    List<CanchaDTO> obtenerCanchasPorZona(Long idZona);

    // 5. Listar canchas abiertas en estos momentos
    List<CanchaDTO> obtenerCanchasAbiertas();

    // 6. Listar canchas por horarios disponibles
    List<CanchaDTO> obtenerCanchasDisponibles(LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);

    // 7. Listar canchas que ha reservado un cliente
    List<CanchaDTO> obtenerCanchasReservadasPorCliente(Long idCliente);

    // 8. Buscar canchas por una disciplina (por nombre)
    List<CanchaDTO> buscarCanchasPorNombreDisciplina(String nombreDisciplina);
    // 9. Listar canchas con capacidad x
    List<CanchaDTO> obtenerCanchasPorCapacidad(Integer capacidad);

    // 10. Listar canchas por tipo de superficie
    List<CanchaDTO> obtenerCanchasPorTipoSuperficie(String tipoSuperficie);

    // 11. Listar canchas con iluminación
    List<CanchaDTO> obtenerCanchasPorIluminacion(String tipoIluminacion);

    // 12. Listar canchas con cubierta
    List<CanchaDTO> obtenerCanchasPorCubierta(String tipoCubierta);

    // Para después: cancelar y reprogramar
    // void cancelarReserva(Long idReserva);
    // void reprogramarReserva(Long idReserva, LocalDate nuevaFecha, LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin);


    
}
