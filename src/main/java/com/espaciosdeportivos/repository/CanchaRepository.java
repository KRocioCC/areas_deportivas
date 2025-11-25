package com.espaciosdeportivos.repository;

import com.espaciosdeportivos.dto.CanchaDTO;
import com.espaciosdeportivos.model.Cancha;
//import com.espaciosdeportivos.model.Equipamiento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CanchaRepository extends JpaRepository<Cancha, Long> {

    // Solo activas (soft delete)
    List<Cancha> findByEstadoTrue();

    // Activa por idfindByIdCanchaAndEstadoTrue
    Optional<Cancha> findByIdCanchaAndEstadoTrue(Long idCancha);

    // Por área deportiva (solo activas)
    //List<Cancha> findByAreaDeportiva_IdAreaDeportivaAndEstadoboolTrue(Long idAreaDeportiva);

    @Query("SELECT a FROM Cancha a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cancha> buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM Cancha c WHERE " +
        "(:horaInicio IS NULL OR c.horaInicio >= :horaInicio) AND " +
        "(:horaFin IS NULL OR c.horaFin <= :horaFin) AND " +
        "(:costo IS NULL OR c.costoHora <= :costo) AND " +
        "(:capacidad IS NULL OR c.capacidad >= :capacidad) AND " +
        "(:tamano IS NULL OR LOWER(c.tamano) = LOWER(:tamano)) AND " +
        "(:iluminacion IS NULL OR LOWER(c.iluminacion) = LOWER(:iluminacion)) AND " +
        "(:cubierta IS NULL OR LOWER(c.cubierta) = LOWER(:cubierta)) AND " +
        "c.estado = true")
    List<Cancha> buscarFiltros(@Param("horaInicio") java.time.LocalTime horaInicio,
                               @Param("horaFin") java.time.LocalTime horaFin,
                               @Param("costo") Double costo,
                               @Param("capacidad") Integer capacidad,
                               @Param("tamano") String tamano,
                               @Param("iluminacion") String iluminacion,
                               @Param("cubierta") String cubierta);

                               // (Opcional) Unicidad de nombre dentro del área
   // boolean existsByNombreIgnoreCaseAndAreaDeportiva_IdAreaDeportiva(String nombre, Long idAreaDeportiva);

   //ADMIN - Obtener canchas por área deportiva
    List<Cancha> findByAreaDeportiva_IdAreaDeportiva(Long idArea);


    @Query("SELECT COUNT(c) > 0 FROM Cancha c " +
        "WHERE c.idCancha = :idCancha " +
        "AND c.areaDeportiva.idAreaDeportiva = :idArea")
    boolean existeCanchaEnArea(
        @Param("idCancha") Long idCancha,
        @Param("idAreaDeportiva") Long idArea
    );

     // 1. Canchas con promedio de calificaciones más altas
    @Query("SELECT c FROM Cancha c LEFT JOIN c.comentario com " +
            "GROUP BY c " +
            "ORDER BY AVG(com.calificacion) DESC")
    List<Cancha> findCanchasMejorCalificadas();

    // 2. Canchas con mayor número de reservas
    @Query("SELECT c FROM Cancha c LEFT JOIN c.incluidos i " +
            "GROUP BY c " +
            "ORDER BY COUNT(i) DESC")
    List<Cancha> findCanchasMasReservadas();

    // 3. Listar canchas por disciplina
    @Query("SELECT c FROM Cancha c JOIN c.sePractica sp " +
            "WHERE sp.disciplina.idDisciplina = :idDisciplina")
    List<Cancha> findCanchasPorDisciplina(@Param("idDisciplina") Long idDisciplina);

    // 4. Listar canchas por zona
    @Query("SELECT c FROM Cancha c WHERE c.areaDeportiva.zona.idZona = :idZona")
    List<Cancha> findCanchasPorZona(@Param("idZona") Long idZona);

    // 5. Listar canchas abiertas en este momento
    @Query("SELECT c FROM Cancha c WHERE c.horaInicio <= :horaActual AND c.horaFin >= :horaActual AND c.estado = true")
    List<Cancha> findCanchasAbiertas(@Param("horaActual") LocalTime horaActual);

    // 6. Listar canchas disponibles en fecha y rango de hora
    @Query("SELECT c FROM Cancha c WHERE c.estado = true AND c.idCancha NOT IN (" +
            "SELECT i.cancha.idCancha FROM Incluye i WHERE i.reserva.fechaReserva = :fecha " +
            "AND ((i.reserva.horaInicio <= :horaFin AND i.reserva.horaFin >= :horaInicio))" +
            ")")
    List<Cancha> findCanchasDisponibles(@Param("fecha") LocalDate fecha,
                                        @Param("horaInicio") LocalTime horaInicio,
                                        @Param("horaFin") LocalTime horaFin);

    // 7. Listar canchas que ha reservado un cliente
    @Query("SELECT DISTINCT c FROM Cancha c JOIN c.incluidos i " +
            "WHERE i.reserva.cliente.id = :idCliente")
    List<Cancha> findCanchasReservadasPorCliente(@Param("idCliente") Long idCliente);

    // 8. Buscar canchas por nombre de disciplina
    @Query("SELECT DISTINCT c FROM Cancha c JOIN c.sePractica sp " +
            "WHERE LOWER(sp.disciplina.nombre) LIKE LOWER(CONCAT('%', :nombreDisciplina, '%'))")
    List<Cancha> buscarCanchasPorDisciplinaNombre(@Param("nombreDisciplina") String nombreDisciplina);

    // 9. Listar canchas por capacidad
    List<Cancha> findByCapacidad(Integer capacidad);

    // 10. Listar canchas por tipo de superficie
    List<Cancha> findByTipoSuperficieIgnoreCase(String tipoSuperficie);

    // 11. Listar canchas con iluminación específica
    List<Cancha> findByIluminacionIgnoreCase(String iluminacion);

    // 12. Listar canchas con cubierta específica
    List<Cancha> findByCubiertaIgnoreCase(String cubierta);
}
