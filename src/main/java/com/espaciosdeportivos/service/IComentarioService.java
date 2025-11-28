package com.espaciosdeportivos.service;

import com.espaciosdeportivos.dto.ComentarioDTO;
import com.espaciosdeportivos.model.Comentario;

import java.util.List;

public interface IComentarioService {

    List<ComentarioDTO> obtenerTodosLosComentarios();

    ComentarioDTO obtenerComentarioPorId(Long id);

    ComentarioDTO crearComentario(ComentarioDTO comentarioDTO);

    ComentarioDTO actualizarComentario(Long id, ComentarioDTO comentarioDTO);

    ComentarioDTO eliminarComentario(Long id); // baja logica T O F

    Comentario obtenerComentarioConBloqueo(Long id);

    void eliminarComentarioFisicamente(Long id);
    
    List<ComentarioDTO> getComentariosPorCancha(Long canchaId);

    // NUEVOS ENDPOINTS GET
    // Obtener comentarios más recientes
    List<ComentarioDTO> getComentariosMasRecientes(int limite);

    // Obtener comentarios con mayor puntuación
    List<ComentarioDTO> getComentariosMayorCalificacion(int limite);

    // Obtener comentarios con mayor puntuación y recientes
    List<ComentarioDTO> getComentariosMayorCalificacionMasRecientes(int limite);

    // Listar comentarios por calificación exacta
    List<ComentarioDTO> getComentariosPorCalificacion(int calificacion);

    // Listar comentarios de un cliente
    List<ComentarioDTO> getComentariosPorCliente(Long clienteId);

    // Listar comentarios por área deportiva
    List<ComentarioDTO> getComentariosPorAreaDeportiva(Long areaDeportivaId);

     // NUEVOS ENDPOINTS FILTRADOS POR CANCHA
    List<ComentarioDTO> getComentariosMasRecientesCancha(Long canchaId, int limite);
    List<ComentarioDTO> getComentariosMayorCalificacionCancha(Long canchaId, int limite);
    List<ComentarioDTO> getComentariosMayorCalificacionMasRecientesCancha(Long canchaId, int limite);
    List<ComentarioDTO> getComentariosPorCalificacionCancha(Long canchaId, int calificacion);
    List<ComentarioDTO> getComentariosPorClienteCancha(Long canchaId, Long clienteId);

}