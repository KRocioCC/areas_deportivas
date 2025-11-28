package com.espaciosdeportivos.repository;

import com.espaciosdeportivos.model.Comentario;
import com.espaciosdeportivos.dto.ComentarioDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//import com.espaciosdeportivos.model.Equipamiento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Buscar comentarios por ID
    List<Comentario> findByPersona_Id(Long id);

    // Buscar comentarios por ID de cancha
    List<Comentario> findByCancha_IdCancha(Long idCancha);

    // Buscar comentarios con calificación mayor o igual a cierto valor
    List<Comentario> findByCalificacionGreaterThanEqual(Integer calificacion);

    // Buscar comentarios recientes
    List<Comentario> findAllByOrderByFechaDesc();

    // Obtener comentarios por cancha ordenados por fecha descendente
    List<Comentario> findByCancha_IdCanchaOrderByFechaDesc(Long idCancha);

    // Obtener comentarios con mayor calificación
    List<Comentario> findByOrderByCalificacionDesc();

    // Obtener comentarios con mayor calificación y recientes
    @Query("SELECT c FROM Comentario c WHERE c.estado = true ORDER BY c.calificacion DESC, c.fecha DESC")
    List<Comentario> findMayorCalificacionMasRecientes();

    // Obtener comentarios por área deportiva
    @Query("SELECT c FROM Comentario c WHERE c.cancha.areaDeportiva.idAreaDeportiva = :idArea AND c.estado = true ORDER BY c.fecha DESC")
    List<Comentario> findByAreaDeportiva(@Param("idArea") Long idArea);

    List<Comentario> findByCalificacion(Integer calificacion);

///ssa

// Obtener comentarios por cancha ordenados por calificación descendente
List<Comentario> findByCancha_IdCanchaOrderByCalificacionDesc(Long idCancha);

// Obtener comentarios por cancha ordenados por calificación DESC y fecha DESC
List<Comentario> findByCancha_IdCanchaOrderByCalificacionDescFechaDesc(Long idCancha);

// Obtener comentarios por cancha y calificación
List<Comentario> findByCancha_IdCanchaAndCalificacion(Long idCancha, Integer calificacion);

// Obtener comentarios por cancha y cliente
List<Comentario> findByCancha_IdCanchaAndPersona_Id(Long idCancha, Long personaId);

    
}